package io.javacloud.framework.flow.internal;

import java.util.Map;

import org.junit.Test;
import org.junit.Assert;
import junit.framework.TestCase;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateFlow;
import io.javacloud.framework.flow.builder.FlowBuilder;
import io.javacloud.framework.flow.builder.RetryBuilder;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.flow.internal.FlowState;
import io.javacloud.framework.flow.spec.StateSpec;
import io.javacloud.framework.flow.test.FlowExecutor;
import io.javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class WorkflowTest extends TestCase {
	private static final FlowExecutor flowExecutor = new FlowExecutor();
	
	@Test
	public void testSuccess() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									context.setAttribute("t1", "abc");
									return Objects.asMap("t1", "abc");
								}
							}, "state2")
							.withState("state2", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return Objects.asMap("t2", "xyz");
								}
							}, null).build();
		
		FlowState state = flowExecutor.run(workflow, Objects.asMap("a", "b"));
		Map<String, Object> output = state.output();
		
		Assert.assertFalse(state.isFailed());
		//Assert.assertEquals("abc", output.get("t1"));
		Assert.assertEquals("xyz", output.get("t2"));
	}
	
	@Test
	public void testFailure() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									return Objects.asMap("t1", "abc");
								}
							}, "state2")
							.withState("state2", new StateHandler<Map<String, Object>, StateHandler.Status>() {
								@Override
								public StateHandler.Status handle(Map<String, Object> parameters, StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.FAILURE;
								}
							}, null).build();
		
		FlowState state = flowExecutor.run(workflow, Objects.asMap("a", "b"));
		Map<String, Object> output = state.output();
		
		Assert.assertTrue(state.isFailed());
		Assert.assertEquals("abc", output.get("t1"));
		Assert.assertNull(output.get("t2"));
	}
	
	@Test
	public void testRetry() {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler<Map<String, Object>, StateHandler.Status>() {
								@Override
								public StateHandler.Status handle(Map<String, Object> parameters, StateContext context) throws Exception {
									if(context.getTryCount() < 5) {
										return StateHandler.Status.RETRY;
									}
									context.setAttribute("t1", "abc");
									return TransitionBuilder.success(context, Objects.asMap("t1", "abc"));
								}
							},
							new RetryBuilder().withRetrier(new StateSpec.Retrier().withMaxAttempts(5)).build(), "state2")
							.withState("state2", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									return Objects.asMap("t2", "xyz");
								}
							}, null).build();
		
		FlowState state = flowExecutor.run(workflow, Objects.asMap("a", "b"));
		Map<String, Object> output = state.output();
		
		Assert.assertFalse(state.isFailed());
		//Assert.assertEquals("abc", output.get("t1"));
		Assert.assertEquals("xyz", output.get("t2"));
	}
}
