package com.appe.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.task.TaskExecutor;
import com.appe.task.TaskManager;
import com.appe.task.TaskQueue;
import com.appe.task.internal.ExponentialRetryExecutor;
import com.appe.task.impl.LocalTaskManagerImpl;
import com.appe.util.Objects;

/**
 * 
 * @author tobi
 *
 */
public class TaskManagerTest extends TestCase {
	private static final Logger logger = LoggerFactory.getLogger(TaskManagerTest.class);
	
	static final CountDownLatch conter = new CountDownLatch(100);
	static class TestExecutor implements TaskExecutor<String> {
		@Override
		public void execute(String task) throws Exception {
			//if(Math.random() > 0.5) {
			//	throw new Exception("Exception task: " + task);
			//}
			Objects.sleep(100, TimeUnit.MILLISECONDS);
			logger.debug(task);
			conter.countDown();
		}
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	@Test
	public void testTask() throws Exception {
		TaskManager taskManager = new LocalTaskManagerImpl();
		
		TaskQueue<String> taskQueue = taskManager.bindQueue("test", String.class);
		taskManager.startExecutor(taskQueue, new ExponentialRetryExecutor<>(new TestExecutor(), 100, 5), 3);
		
		for(int i = 0; i < conter.getCount(); i ++) {
			taskQueue.offer("task: " + i);
		}
		
		//WAIT UNTIL DONE
		conter.await();
		taskManager.shutdown();
	}
}
