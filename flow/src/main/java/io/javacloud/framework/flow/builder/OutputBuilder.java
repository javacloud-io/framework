package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class OutputBuilder {
	private String resultPath;
	private String outputPath;
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
				Object result = context.getAttribute(StateContext.RESULT_ATTRIBUTE);
				if(result != null) {
					if(Objects.isEmpty(resultPath)) {
						result = context.getParameters();
					} else {
						result = new JsonPath(context.getParameters()).merge(resultPath, result);
					}
				} else {
					result = context.getParameters();
				}
				//OUTPUT
				result = new JsonPath(result).select(outputPath);
				TransitionBuilder.success(context, result);
				return TransitionBuilder.success(next);
			}
		};
	}
}
