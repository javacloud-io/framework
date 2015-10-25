package com.appe.task.internal;


//import com.google.common.util.concurrent.RateLimiter;
import com.appe.task.TaskTuple;
/**
 * To avoid contention from original tuple, a rate limiter will be applied so polling to exceed the processing rate.
 * It will be number of task / seconds which task executor are able to pull.
 * 
 * TODO: Need to implement rate limiter
 * @author tobi
 *
 * @param <T>
 */
public class ThrottlerTaskTuple<T> implements TaskTuple<T> {
	private TaskTuple<T> tuple;
	//private RateLimiter limiter;
	/**
	 * Limit to number of poll task / seconds. 
	 * @param tuple
	 * @param limit
	 */
	public ThrottlerTaskTuple(TaskTuple<T> tuple, double limit) {
		this.tuple = tuple;
		//this.limiter = RateLimiter.create(limit);
	}
	
	/**
	 * This process have to WAIT until task is able to WRITE.
	 * 
	 * TODO: this will not work at all if poll not always return a task. It does help in the sense of not calling poll to offten but
	 * doesn't actually guarantee the task rate.
	 */
	@Override
	public T poll(int timeoutSeconds) throws InterruptedException {
		//limiter.acquire(1);
		return	tuple.poll(timeoutSeconds);
	}
}
