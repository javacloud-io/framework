package javacloud.framework.flow.spi;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class FlowExecutionTest extends TestCase {
	@Test
	public void testCompleted() {
		Assert.assertTrue(FlowExecution.Status.SUCCEEDED.isCompleted());
		Assert.assertTrue(FlowExecution.Status.FAILED.isCompleted());
		Assert.assertTrue(FlowExecution.Status.CANCELLED.isCompleted());
	}
	
	@Test
	public void testNotCompleted() {
		Assert.assertFalse(FlowExecution.Status.INPROGRESS.isCompleted());
		Assert.assertFalse(FlowExecution.Status.SCHEDULED.isCompleted());
	}
}
