package javacloud.framework.flow.internal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.flow.worker.TaskPoller;
import javacloud.framework.flow.worker.TaskQueue;
import javacloud.framework.flow.worker.TaskRunner;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.worker.FlowHandler;
import javacloud.framework.flow.worker.StateExecution;
import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;

/**
 * A local execution to simulate state execution:
 * 
 * 1. Submit to get back future result
 * 2. Cancel an execution
 * 3. Graceful termination to wait for executing task to complete.
 * 4. Simulate heart beat and enforce execution timeout.
 * 
 * @author ho
 *
 */
public class LocalFlowExecutor {
	private static final Logger logger = Logger.getLogger(LocalFlowExecutor.class.getName());
	
	//KEEP THE ACTIVE 
	static class HandlerTask extends FutureTask<StateExecution> implements Delayed {
		final FlowHandler handler;
		final StateExecution state;
		long availableAt;
		//INVOKE RUN TO COMPLETE TASK
		public HandlerTask(FlowHandler handler, StateExecution state) {
			super(new Callable<StateExecution>() {
				@Override
				public StateExecution call() throws Exception {
					handler.complete(state);
					return state;
				}
			});
			this.handler = handler;
			this.state   = state;
			this.availableAt = System.nanoTime();
		}
		
		@Override
		public int compareTo(Delayed o) {
			HandlerTask delayed = (HandlerTask)o;
			return Objects.signum(this.availableAt - delayed.availableAt);
		}
		
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(availableAt - System.nanoTime(), TimeUnit.NANOSECONDS);
		}
		
		//RENEW TASK TO RE-RUN
		public HandlerTask renew(int delaySeconds) {
			availableAt  = System.nanoTime() + TimeUnit.SECONDS.toNanos(delaySeconds);
			return this;
		}
	}
	//
	private final Externalizer externalizer;
	private final DelayQueue<HandlerTask> availableTasks = new DelayQueue<>();
	
	private final TaskQueue<HandlerTask>  taskQueue = new TaskQueue<HandlerTask>();
	private final ScheduledExecutorService workersPool;
	private final ScheduledExecutorService pollerPool;
	public LocalFlowExecutor(Externalizer externalizer, int numberOfWorkers, int reservationSeconds) {
		this.externalizer = externalizer;
		
		//POLL EVERY SECOND
		this.pollerPool = Executors.newScheduledThreadPool(1);
		this.pollerPool.scheduleAtFixedRate(new TaskPoller<HandlerTask>(taskQueue, numberOfWorkers, reservationSeconds) {
			@Override
			protected List<HandlerTask> poll(int numberOfTasks) {
				return pollTasks(numberOfTasks);
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		
		//SUBMIT WORKERS
		workersPool = Executors.newScheduledThreadPool(numberOfWorkers);
		for(int i = 0; i < numberOfWorkers; i ++) {
			workersPool.scheduleAtFixedRate(new TaskRunner<HandlerTask>(taskQueue, reservationSeconds * 2) {
				@Override
				protected void run(HandlerTask task) {
					runTask(task);
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * DEFAULT 5 WORKERS, 60 SECONDS RESERVATION
	 */
	public LocalFlowExecutor() {
		this(null, 2, 60);
	}
	
	/**
	 * 
	 * @param numberOfTasks
	 * @return
	 */
	protected List<HandlerTask> pollTasks(int numberOfTasks) {
		List<HandlerTask> tasks = new ArrayList<>();
		while(numberOfTasks-- > 0) {
			HandlerTask task = availableTasks.poll();
			if(task == null) {
				break;
			} else if(!task.isCancelled()) {
				tasks.add(task);
			}
		}
		return tasks;
	}
	
	/**
	 * ENSURE EXECUTION IS NOT FAILURE
	 * 
	 * @param task
	 */
	protected void runTask(HandlerTask task) {
		StateTransition transition = task.handler.execute(task.state);
		
		//FAILURE/SUCCESS => RUN COMPLETION
		if(transition.isEnd()) {
			task.run();
		} else {
			int delaySeconds = 0;
			if(transition instanceof StateTransition.Retry) {
				delaySeconds = ((StateTransition.Retry)transition).getDelaySeconds();
				if(delaySeconds < FlowHandler.MIN_DELAY_SECONDS) {
					delaySeconds = FlowHandler.MIN_DELAY_SECONDS;
				}
			} else if(transition instanceof StateTransition.Success) {
				delaySeconds = ((StateTransition.Success)transition).getDelaySeconds();
			}
			availableTasks.offer(task.renew(delaySeconds));
		}
	}
	
	/**
	 * 
	 * @param stateFlow
	 * @param parameters
	 * @return
	 */
	public <T> Future<StateExecution> submit(StateFlow stateFlow, T parameters) {
		FlowHandler handler = new FlowHandler(stateFlow, externalizer);
		String executionId = Codecs.randomID();
		logger.log(Level.FINE, "Starting execution: {0}", executionId);
		
		StateExecution state = handler.start(parameters);
		state.setExecutionId(executionId);
		
		//QUEUE TASK
		HandlerTask task = new HandlerTask(handler, state);
		availableTasks.offer(task);
		return task;
	}
	
	/**
	 * 
	 * @param stateFlow
	 * @param parameters
	 * @return
	 */
	public <T> StateExecution run(StateFlow stateFlow, T parameters) {
		try {
			return submit(stateFlow, parameters).get();
		} catch(InterruptedException | ExecutionException ex) {
			throw Exceptions.asUnchecked(ex);
		}
	}
}
