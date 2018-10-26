package io.javacloud.framework.flow.internal;

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
	private Object result;
	
	private String outputPath = JsonPath.ROOT;
	private Object output;
	
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
	 * @param result
	 * @return
	 */
	public <T> OutputBuilder withResult(T result) {
		this.result = result;
		return this;
	}
	
	/**
	 * 
	 * @param output
	 * @return
	 */
	public <T> OutputBuilder withOutput(T output) {
		this.output = output;
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
				Object finalResult = result;
				if(finalResult == null) {
					finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				}
				
				//PROCESS RESULT
				if(finalResult != null) {
					//DISCARD RESULT IF NO PATH
					if(Objects.isEmpty(resultPath)) {
						finalResult = context.getParameters();
					} else {
						finalResult = new JsonPath(context.getParameters()).merge(resultPath, result);
					}
				} else {
					finalResult = context.getParameters();
				}
				
				//EMPTY OUTPUT IF NULL
				JsonPath jsonPath = new JsonPath(finalResult);
				if(output != null) {
					finalResult = jsonPath.compile(output);
				} else {
					finalResult = jsonPath.select(outputPath);
				}
				
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
