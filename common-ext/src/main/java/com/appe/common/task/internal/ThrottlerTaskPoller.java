package com.appe.task.internal;


//import com.google.common.util.concurrent.RateLimiter;
import com.appe.task.TaskPoller;
/**
 * To avoid contention from original tuple, a rate limiter will be applied so polling to exceed the processing rate.
 * It will be number of task / seconds which task executor are able to pull.
 * 
 * TODO: Need to implement rate limiter
 * @author tobi
 *
 * @param <T>
 */
public class ThrottlerTaskPoller<T> implements TaskPoller<T> {
	private TaskPoller<T> poller;
	//private RateLimiter limiter;
	/**
	 * Limit to number of poll task / seconds. 
	 * @param tuple
	 * @param limit
	 */
	public ThrottlerTaskPoller(TaskPoller<T> poller, double limit) {
		this.poller = poller;
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
		return	poller.poll(timeoutSeconds);
	}
}
