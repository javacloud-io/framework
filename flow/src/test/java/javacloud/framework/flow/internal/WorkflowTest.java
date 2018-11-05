package javacloud.framework.flow.internal;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.Assert;
import junit.framework.TestCase;
import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFlow;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.builder.FlowBuilder;
import javacloud.framework.flow.builder.RetryBuilder;
import javacloud.framework.flow.builder.TransitionBuilder;
import javacloud.framework.flow.internal.FlowState;
import javacloud.framework.flow.spi.RetrierSpec;
import javacloud.framework.flow.test.FlowExecutor;
import javacloud.framework.util.Objects;
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
							.withStartAt("state3")
							.withState("state3", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									return Objects.asMap("t1", "abc");
								}
							}, "state4")
							.withState("state4", new StateHandler<Map<String, Object>, StateHandler.Status>() {
								@Override
								public StateHandler.Status handle(Map<String, Object> parameters, StateContext context) throws Exception {
									context.setAttribute("t2", "xyz");
									return StateHandler.Status.FAILED;
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
							.withStartAt("state5")
							.withState("state5", new StateHandler<Map<String, Object>, StateHandler.Status>() {
								@Override
								public StateHandler.Status handle(Map<String, Object> parameters, StateContext context) throws Exception {
									if(context.getTryCount() < 5) {
										return StateHandler.Status.RETRY;
									}
									context.setAttribute("t1", "abc");
									return TransitionBuilder.succeed(context, Objects.asMap("t1", "abc"));
								}
							},
							new RetryBuilder().withRetrier(new RetrierSpec().withMaxAttempts(5)).build(), "state6")
							.withState("state6", new StateHandler<Map<String, Object>, Map<String, Object>>() {
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
	
	@Test
	public void testCancel() throws Exception {
		StateFlow workflow = new FlowBuilder()
							.withStartAt("c1")
							.withState("c1", new StateHandler<Map<String, Object>, StateHandler.Status>() {
								@Override
								public StateHandler.Status handle(Map<String, Object> parameters, StateContext context) throws Exception {
									if(context.getTryCount() < 5) {
										return StateHandler.Status.RETRY;
									}
									context.setAttribute("t1", "abc");
									return TransitionBuilder.succeed(context, Objects.asMap("t1", "abc"));
								}
							},
							new RetryBuilder().withRetrier(new RetrierSpec().withMaxAttempts(5)).build(), "c2")
							.withState("c2", new StateHandler<Map<String, Object>, Map<String, Object>>() {
								@Override
								public Map<String, Object> handle(Map<String, Object> parameters, StateContext context) throws Exception {
									return Objects.asMap("t2", "xyz");
								}
							}, null).build();
		
		Future<FlowState> state = flowExecutor.submit(workflow, Objects.asMap("a", "b"));
		Objects.sleep(5, TimeUnit.SECONDS);
		state.cancel(true);
	}
}
