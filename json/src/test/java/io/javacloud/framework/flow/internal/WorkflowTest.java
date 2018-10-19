package io.javacloud.framework.flow.internal;

import org.junit.Test;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateMachine;
import io.javacloud.framework.flow.builder.FlowBuilder;
import io.javacloud.framework.util.Dictionaries;
import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class WorkflowTest extends TestCase {
	@Test
	public void testStep1() {
		StateMachine workflow = new FlowBuilder()
							.withStartAt("step1")
							.withState("step1", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("x", "abc");
									return StateHandler.Status.SUCCESS;
								}
							}, "step2")
							.withState("step2", new StateHandler() {
								@Override
								public StateHandler.Status handle(StateContext context) throws Exception {
									context.setAttribute("x2", "abc");
									return StateHandler.Status.SUCCESS;
								}
							}, null).build();
		
		FlowState state = new FlowLocalExecutor(workflow).start(Dictionaries.asDict("a", "b"));
		System.out.println(state.getAttributes());
	}
}
