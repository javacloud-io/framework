package io.javacloud.framework.flow.internal;

import org.junit.Test;
import org.junit.Assert;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateFlow;
import io.javacloud.framework.flow.builder.FlowBuilder;
import io.javacloud.framework.flow.builder.RetryBuilder;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.flow.spi.StateSpec;
import io.javacloud.framework.flow.test.FlowExecutor;
import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class WorkflowTest extends TestCase {
	@Test
	public void testSuccess() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									context.setAttribute("t1", "abc");
									return successResult(context, "t1", "abc");
								}
							}, "state2")
							.withState("state2", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return successResult(context, "t2", "xyz");
								}
							}, null).build();
		
		FlowState state = FlowExecutor.start(workflow, Dictionaries.asDict("a", "b"));
		Dictionary output = state.output();
		
		Assert.assertFalse(state.isFailed());
		Assert.assertEquals("abc", output.get("t1"));
		Assert.assertEquals("xyz", output.get("t2"));
	}
	
	@Test
	public void testFailure() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									return successResult(context, "t1", "abc");
								}
							}, "state2")
							.withState("state2", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.FAILURE;
								}
							}, null).build();
		
		FlowState state = FlowExecutor.start(workflow, Dictionaries.asDict("a", "b"));
		Dictionary output = state.output();
		
		Assert.assertTrue(state.isFailed());
		Assert.assertEquals("abc", output.get("t1"));
		Assert.assertNull(output.get("t2"));
	}
	
	@Test
	public void testRetry() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									if(context.getTryCount() < 5) {
										return StateHandler.Status.RETRY;
									}
									context.setAttribute("t1", "abc");
									return successResult(context, "t1", "abc");
								}
							},
							new RetryBuilder().withRetrier(new StateSpec.Retrier().withMaxAttempts(5)).build(), "state2")
							.withState("state2", new StateHandler<Dictionary>() {
								@Override
								public StateHandler.Status handle(Dictionary parameters, StateContext context) throws Exception {
									return successResult(context, "t2", "xyz");
								}
							}, null).build();
		
		FlowState state = FlowExecutor.start(workflow, Dictionaries.asDict("a", "b"));
		Dictionary output = state.output();
		
		Assert.assertFalse(state.isFailed());
		Assert.assertEquals("abc", output.get("t1"));
		Assert.assertEquals("xyz", output.get("t2"));
	}
	
	/**
	 * Combine INPUT/OUTPUT
	 * 
	 * @param context
	 * @param dict
	 * @return
	 */
	static StateHandler.Status successResult(StateContext context, String name, String value) {
		Dictionary result = new Dictionary();
		result.putAll((Dictionary)context.getInput());
		result.put(name, value);
		return TransitionBuilder.success(context, result);
	}
}
