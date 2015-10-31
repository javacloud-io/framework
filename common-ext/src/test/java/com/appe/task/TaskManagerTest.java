package com.appe.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.registry.internal.GuiceTestCase;
import com.appe.task.TaskExecutor;
import com.appe.task.TaskManager;
import com.appe.task.internal.BlockingTaskQueue;
import com.appe.task.internal.ExponentialRetryExecutor;
import com.appe.util.Objects;

/**
 * 
 * @author tobi
 *
 */
public class TaskManagerTest extends GuiceTestCase {
	private static final Logger logger = LoggerFactory.getLogger(TaskManagerTest.class);
	
	@Inject
	TaskManager taskManager;
	
	static class TestExecutor implements TaskExecutor<String> {
		CountDownLatch counter;
		public TestExecutor(CountDownLatch counter) {
			this.counter = counter;
		}
		@Override
		public void execute(String task) throws Exception {
			//if(Math.random() > 0.5) {
			//	throw new Exception("Exception task: " + task);
			//}
			Objects.sleep(100, TimeUnit.MILLISECONDS);
			logger.debug(task);
			counter.countDown();
		}
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	@Test
	public void testTask() throws Exception {
		CountDownLatch counter = new CountDownLatch(100);
		TaskQueue<String> taskQueue = new BlockingTaskQueue<String>();
		taskManager.startExecutor(taskQueue, new ExponentialRetryExecutor<>(new TestExecutor(counter), 100, 5), 3);
		
		for(int i = 0; i < counter.getCount(); i ++) {
			taskQueue.offer("task: " + i);
		}
		
		//WAIT UNTIL DONE
		counter.await();
		taskManager.shutdown();
	}
}
