package javacloud.framework.flow.worker;
import java.util.ArrayList;
import java.util.Date;
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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.spi.FlowExecution;
import javacloud.framework.io.Externalizer;
import javacloud.framework.json.internal.JsonConverter;
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
public class StandardFlowService {
	private static final Logger logger = Logger.getLogger(StandardFlowService.class.getName());
	
	//KEEP THE ACTIVE 
	static class ExecutionTask extends FutureTask<FlowState> implements Delayed {
		final FlowExecutor executor;
		final FlowState state;
		long availableAt;
		final Date startDate = new Date();
		Date completionDate	 = null;
		//INVOKE RUN TO COMPLETE TASK
		public ExecutionTask(FlowExecutor executor, FlowState state) {
			super(new Callable<FlowState>() {
				@Override
				public FlowState call() throws Exception {
					executor.complete(state);
					return state;
				}
			});
			this.executor = executor;
			this.state    = state;
			this.availableAt = System.nanoTime();
		}
		
		@Override
		public int compareTo(Delayed o) {
			ExecutionTask delayed = (ExecutionTask)o;
			return Objects.signum(this.availableAt - delayed.availableAt);
		}
		
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(availableAt - System.nanoTime(), TimeUnit.NANOSECONDS);
		}
		
		//RENEW TASK TO RE-RUN
		public ExecutionTask renew(int delaySeconds) {
			availableAt  = System.nanoTime() + TimeUnit.SECONDS.toNanos(delaySeconds);
			return this;
		}
	}
	//
	private final Externalizer externalizer;
	private final DelayQueue<ExecutionTask> availableTasks 	= new DelayQueue<>();
	
	private final ReservationQueue<ExecutionTask>  taskQueue= new ReservationQueue<ExecutionTask>();
	private final ScheduledExecutorService workersPool;
	private final ScheduledExecutorService pollerPool;
	public StandardFlowService(Externalizer externalizer, int numberOfWorkers, int reservationSeconds) {
		this.externalizer = externalizer;
		
		//POLL EVERY SECOND
		this.pollerPool = Executors.newScheduledThreadPool(1);
		this.pollerPool.scheduleAtFixedRate(new TaskPoller<ExecutionTask>(taskQueue, numberOfWorkers, reservationSeconds) {
			@Override
			protected List<ExecutionTask> poll(int numberOfTasks) {
				return pollTasks(numberOfTasks);
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		
		//SUBMIT WORKERS
		workersPool = Executors.newScheduledThreadPool(numberOfWorkers);
		for(int i = 0; i < numberOfWorkers; i ++) {
			workersPool.scheduleAtFixedRate(new TaskRunner<ExecutionTask>(taskQueue, reservationSeconds * 2) {
				@Override
				protected void run(ExecutionTask task) {
					runTask(task);
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * DEFAULT 2 WORKERS, 60 SECONDS RESERVATION
	 */
	public StandardFlowService() {
		this(null, 2, 60);
	}
	
	/**
	 * 
	 * @param numberOfTasks
	 * @return
	 */
	protected List<ExecutionTask> pollTasks(int numberOfTasks) {
		List<ExecutionTask> tasks = new ArrayList<>();
		while(numberOfTasks-- > 0) {
			ExecutionTask task = availableTasks.poll();
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
	protected void runTask(ExecutionTask task) {
		StateTransition transition = task.executor.execute(task.state);
		
		//FAILURE/SUCCESS => RUN COMPLETION
		if(transition.isEnd()) {
			task.run();
			task.completionDate = new Date();
		} else {
			int delaySeconds = 0;
			if(transition instanceof StateTransition.Retry) {
				delaySeconds = ((StateTransition.Retry)transition).getDelaySeconds();
				if(delaySeconds < FlowExecutor.MIN_DELAY_SECONDS) {
					delaySeconds = FlowExecutor.MIN_DELAY_SECONDS;
				}
			} else if(transition instanceof StateTransition.Success) {
				delaySeconds = ((StateTransition.Success)transition).getDelaySeconds();
			}
			availableTasks.offer(task.renew(delaySeconds));
		}
	}
	
	/**
	 * Execute and return the execution result
	 * 
	 * @param stateFlow
	 * @param input
	 * @return
	 */
	public <T> FlowExecution execute(StateFlow stateFlow, T input) {
		try {
			return startExecution(stateFlow, input).get();
		} catch(InterruptedException | ExecutionException ex) {
			throw Exceptions.asUnchecked(ex);
		}
	}
	
	/**
	 * 
	 * @param stateFlow
	 * @param input
	 * @return
	 */
	public <T> Future<FlowExecution> startExecution(StateFlow stateFlow, T input) {
		FlowExecutor executor = new FlowExecutor(stateFlow, externalizer);
		String executionId = Codecs.randomID();
		logger.log(Level.FINE, "Starting execution: {0}", executionId);
		
		FlowState state = executor.start(input);
		state.setExecutionId(executionId);
		
		//QUEUE TASK
		final ExecutionTask task = new ExecutionTask(executor, state);
		availableTasks.offer(task);
		return new Future<FlowExecution>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return task.cancel(mayInterruptIfRunning);
			}

			@Override
			public boolean isCancelled() {
				return task.isCancelled();
			}

			@Override
			public boolean isDone() {
				return task.isDone();
			}

			@Override
			public FlowExecution get() throws InterruptedException, ExecutionException {
				return flowExecution(task.get());
			}

			@Override
			public FlowExecution get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return flowExecution(task.get(timeout, unit));
			}
			
			FlowExecution flowExecution(FlowState state) {
				return new FlowExecution() {
					@Override
					public String getId() {
						return state.getExecutionId();
					}

					@Override
					public String getName() {
						return state.getName();
					}

					@Override
					public Date getStartDate() {
						return task.startDate;
					}
					
					@Override
					public Date getCompletionDate() {
						return task.completionDate;
					}
					
					@Override
					public Status getStatus() {
						return state.getStatus();
					}

					@Override
					public <R> R getOutput(Class<R> type) {
						Object output = state.output();
						if(output != null && externalizer != null && !type.isInstance(output)) {
							output = new JsonConverter(externalizer).to(type).apply(output);
						}
						return Objects.cast(output);
					}
				};
			}
		};
	}
}
