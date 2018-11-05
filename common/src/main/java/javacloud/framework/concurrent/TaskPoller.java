package javacloud.framework.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.concurrent.TaskQueue.Reservation;

/**
 * 
 * @author ho
 *
 */
public abstract class TaskPoller<T> implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskPoller.class.getName());
	
	private final TaskQueue<T> taskQueue;
	private final int numberOfWorkers;
	private final int reservationSeconds;
	/**
	 * 
	 * @param taskQueue
	 * @param numberOfWorkers
	 * @param reservationSeconds
	 */
	public TaskPoller(TaskQueue<T> taskQueue, int numberOfWorkers, int reservationSeconds) {
		this.taskQueue = taskQueue;
		this.numberOfWorkers = numberOfWorkers;
		this.reservationSeconds = reservationSeconds;
	}
	
	/**
	 * 
	 * DOING ONE RESERVATION EACH RUN
	 */
	@Override
	public void run() {
		List<TaskQueue.Reservation<T>> reservations = new ArrayList<>();
		for(int i = 0; i < numberOfWorkers; i ++) {
			Reservation<T> reservation = taskQueue.reserve(reservationSeconds, TimeUnit.SECONDS);
			if(reservation == null) {
				break;
			}
			reservations.add(reservation);
		}
		
		//PULL AS MUCH TASKS AS CAN HANDLE
		if(!reservations.isEmpty()) {
			logger.log(Level.FINE, "Reservered {0} worker(s)", reservations.size());
			try {
				List<T> tasks = poll(reservations.size());
				logger.log(Level.FINE, "Confirming {0} and cancelling {1} reservation(s)", new Object[]{tasks.size(), reservations.size() - tasks.size()});
				for(int i = 0; i < reservations.size(); i ++) {
					TaskQueue.Reservation<T> reservation = reservations.get(i);
					if(i < tasks.size()) {
						reservation.confirm(tasks.get(i));
					} else {
						reservation.cancel(null);
					}
				}
			} catch(Exception ex) {
				logger.log(Level.WARNING, "Cancelling all {0} reservation(s) due to unexpected error: {1}", new Object[] {reservations.size(), ex});
				for(int i = 0; i < reservations.size(); i ++) {
					TaskQueue.Reservation<T> reservation = reservations.get(i);
					reservation.cancel(ex);
				}
			}
		} else {
			logger.log(Level.FINE, "No worker available!");
		}
	}
	
	/**
	 * Pull maximum number of task that can be handle by all workers.
	 * @param numberOfTasks
	 * @return
	 */
	protected abstract List<T> poll(int numberOfTasks);
}
