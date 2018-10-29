package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.json.internal.JsonTemplate;
import io.javacloud.framework.util.Dictionary;

/**
 * 
 * @author ho
 *
 */
public class OutputBuilder {
	protected Object result = JsonPath.ROOT;
	protected Object output = JsonPath.ROOT;
	
	private String next;
	public OutputBuilder() {	
	}
	/**
	 * 
	 * @param result
	 * @return
	 */
	public OutputBuilder withResult(Object result) {
		this.result = result;
		return this;
	}
	
	/**
	 * 
	 * @param output
	 * @return
	 */
	public OutputBuilder withOutput(Object output) {
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
	 * If there is hard-coded result => Use it.
	 * 
	 * @return
	 */
	public StateHandler.OutputHandler build() {
		return new StateHandler.OutputHandler() {
			@Override
			public StateTransition.Success onOutput(StateContext context) {
				Object finalResult;
				
				//FILTER RESULT
				String resultPath = JsonPath.ROOT;
				if(result == null) {
					finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				} else if(result instanceof String && JsonPath.is((String)result)){
					resultPath = (String)result;
					finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				} else {
					finalResult = compileResult(new JsonPath(context.getParameters()));
				}
				
				//PROCESS RESULT
				if(finalResult != null) {
					finalResult = new JsonPath(context.getParameters()).merge(resultPath, finalResult);
				} else {
					finalResult = context.getParameters();
				}
				
				//FILTER OUTPUT
				if(output == null) {
					output = finalResult;
				} else if(output instanceof String && JsonPath.is((String)output)) {
					finalResult = new JsonPath(finalResult).select((String)output);
				} else {
					output = compileOutput(new JsonPath(finalResult));
				}
				
				//SET BACK RESULT
				context.setAttribute(StateContext.ATTRIBUTE_RESULT, finalResult == null? new Dictionary() : finalResult);
				return TransitionBuilder.success(next);
			}
		};
	}
	
	/**
	 * 
	 * @param jsonPath
	 * @return
	 */
	protected Object compileResult(JsonPath jsonPath) {
		return new JsonTemplate(jsonPath).compile(result);
	}
	
	/**
	 * 
	 * @param jsonPath
	 * @return
	 */
	protected Object compileOutput(JsonPath jsonPath) {
		return new JsonTemplate(jsonPath).compile(output);
	}
}
