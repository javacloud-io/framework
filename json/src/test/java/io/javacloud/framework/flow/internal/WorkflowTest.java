package io.javacloud.framework.flow.internal;

import org.junit.Test;
import org.junit.Assert;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.StateTransition.Retry;
import io.javacloud.framework.flow.builder.FlowBuilder;
import io.javacloud.framework.flow.builder.TransitionBuilder;
import io.javacloud.framework.util.Dictionaries;
import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class WorkflowTest extends TestCase {
	@Test
	public void testSuccess() {
		StateMachine workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("t1", "abc");
									return StateHandler.Status.SUCCESS;
								}
							}, "state2")
							.withState("state2", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.SUCCESS;
								}
							}, null).build();
		
		FlowState state = new FlowLocalExecutor(workflow).start(Dictionaries.asDict("a", "b"));
		Assert.assertFalse(state.isFailed());
		Assert.assertEquals("abc", state.getAttributes().get("t1"));
		Assert.assertEquals("xyz", state.getAttributes().get("t2"));
	}
	
	@Test
	public void testFailure() {
		StateMachine workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("t1", "abc");
									return StateHandler.Status.FAILURE;
								}
							}, "state2")
							.withState("state2", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.SUCCESS;
								}
							}, null).build();
		
		FlowState state = new FlowLocalExecutor(workflow).start(Dictionaries.asDict("a", "b"));
		Assert.assertTrue(state.isFailed());
		Assert.assertEquals("abc", state.getAttributes().get("t1"));
		Assert.assertNull(state.getAttributes().get("t2"));
	}
	
	@Test
	public void testRetry() {
		StateMachine workflow = new FlowBuilder()
							.withStartAt("state1")
							.withState("state1", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									if(context.getRetryCount() < 5) {
										return StateHandler.Status.RETRY;
									}
									context.setAttribute("t1", "abc");
									return StateHandler.Status.SUCCESS;
								}
							}, new StateHandler.RetryHandler() {
								@Override
								public Retry onRetry(StateContext context) {
									return new TransitionBuilder().withMaxAttempts(6).retry();
								}
							}, "state2")
							.withState("state2", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.SUCCESS;
								}
							}, null).build();
		
		FlowState state = new FlowLocalExecutor(workflow).start(Dictionaries.asDict("a", "b"));
		Assert.assertFalse(state.isFailed());
		Assert.assertEquals("abc", state.getAttributes().get("t1"));
		Assert.assertEquals("xyz", state.getAttributes().get("t2"));
	}
}
