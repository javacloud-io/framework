package io.javacloud.framework.flow.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * An implement of reservation workers using SynchronousQueue to avoid over supplying tasks than workers can handle.
 * 
 * 1. Prior to poll task for work, a poller much make a reservation with an intended work and how long the reservation
 * might last.
 * 
 * 2. A poller then go pull the work and assign to Reservation or cancel due to no-work or error.
 * 
 * 3. A Worker wait for work to be assigned, execute and return back result.
 * 
 * @author ho
 * 
 * @param <T>
 */
public class TaskQueue<T> {
	private static final Logger logger = Logger.getLogger(TaskQueue.class.getName());
	
	/**
	 * Holding the reservation of a worker need to confirm or cancel after reserved
	 * 
	 * @param <T>
	 */
	public interface Reservation<T> {
		void confirm(T value);
		void cancel(Throwable cause);
	}
	
	//OFFER ONLY IF WORKER AVAILABLE
	private final SynchronousQueue<Reservation<T>> reservationQueue = new SynchronousQueue<>();
	public TaskQueue() {
	}
	
	/**
	 * Reserve up to number of worker
	 * 
	 * @param timeout
	 * @param unit
	 * @param numberOfWorkers
	 * @return
	 */
	public List<Reservation<T>> reserve(long timeout, TimeUnit unit, int numberOfWorkers) {
		List<TaskQueue.Reservation<T>> reservations = new ArrayList<>();
		for(int i = 0; i < numberOfWorkers; i ++) {
			Reservation<T> reservation = reserve(timeout, unit);
			if(reservation == null) {
				break;
			}
			reservations.add(reservation);
		}
		return reservations;
	}
	
	/**
	 * Call by poller to reserve a worker prior to pulling task
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public Reservation<T> reserve(long timeout, TimeUnit unit) {
		ReservationTask<T> resveration = new ReservationTask<T>(timeout, unit);
		if(reservationQueue.offer(resveration)) {
			return resveration;
		}
		return null;
	}
	
	/**
	 * Call by work to pull task for WORK.
	 * 
	 * @param timeout
	 * @param unit
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		ReservationTask<T> resveration = (ReservationTask<T>)reservationQueue.poll(timeout, unit);
		//DONT HAVE OR EXPIRES => ASSUMING NO WORK
		if(resveration == null) {
			logger.fine("No task available for worker after waiting: " + unit.toMillis(timeout) + "(ms)");
			return null;
		}
		
		//WAIT FOR CONFIRMATION
		try {
			return resveration.get(resveration.timeout, resveration.unit);
		} catch(TimeoutException ex) {
			//CONFIRMATION IS NOT MAKE WITHIN TIME
			logger.fine("No task confirmation after reservation timeout: " + resveration.unit.toMillis(resveration.timeout) + "(ms)");
		} catch(ExecutionException ex) {
			//SOME ISSUE AFTER RESERVATION
			logger.fine("Unknown issue occurred after reservation, cause: " + ex);
		} catch(CancellationException ex) {
			//RESERVATION CANCLED
			logger.fine("Reservation cancelled!");
		}
		return null;
	}
	
	//PROMISE WITH FUTURE
	static class ReservationTask<T> extends FutureTask<T> implements Reservation<T> {
		final long timeout;
		final TimeUnit unit;
		public ReservationTask(long timeout, TimeUnit unit) {
			super(new Callable<T>() {
				@Override
				public T call() throws Exception {
					throw new IllegalStateException();
				}
			});
			this.timeout = timeout;
			this.unit 	 = unit;
		}
		
		@Override
		public void confirm(T value) {
			super.set(value);
		}

		@Override
		public void cancel(Throwable cause) {
			if(cause == null) {
				super.cancel(true);
			} else {
				super.setException(cause);
			}
		}
	}
}
