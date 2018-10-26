package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class OutputBuilder {
	private String resultPath = JsonPath.ROOT;
	private String outputPath = JsonPath.ROOT;
	
	private String next;
	public OutputBuilder() {	
	}
	/**
	 * 
	 * @param resultPath
	 * @return
	 */
	public OutputBuilder withResultPath(String resultPath) {
		this.resultPath = resultPath;
		return this;
	}
	
	/**
	 * 
	 * @param outputPath
	 * @return
	 */
	public OutputBuilder withOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}
	
	/**
	 * 
	 * @param next
	 * @return
	 */
	public OutputBuilder withNext(String next) {
		this.next = next;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateHandler.OutputHandler build() {
		return new StateHandler.OutputHandler() {
			@Override
			public StateTransition.Success onOutput(StateContext context) {
				//USING REAL RESULT IF NOT OVERRIDE
				Object finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				
				//PROCESS RESULT
				if(finalResult != null) {
					//DISCARD RESULT IF NO PATH
					if(Objects.isEmpty(resultPath)) {
						finalResult = context.getParameters();
					} else {
						finalResult = new JsonPath(context.getParameters()).merge(resultPath, finalResult);
					}
				} else {
					finalResult = context.getParameters();
				}
				
				//EMPTY OUTPUT IF NULL
				finalResult = new JsonPath(finalResult).select(outputPath);
				
				//EMPTY IF GOT NOTHING
				if(finalResult == null) {
					finalResult = new Dictionary();
				}
				//SET BACK RESULT
				context.setAttribute(StateContext.ATTRIBUTE_RESULT, finalResult);
				return TransitionBuilder.success(next);
			}
		};
	}
}
