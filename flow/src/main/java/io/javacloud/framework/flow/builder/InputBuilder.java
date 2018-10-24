package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.json.internal.JsonPath;
/**
 * 
 * @author ho
 *
 */
public class InputBuilder {
	private String 		inputPath	= JsonPath.ROOT;
	public InputBuilder() {
	}
	
	/**
	 * 
	 * @param inputPath
	 * @return
	 */
	public InputBuilder withInputPath(String inputPath) {
		this.inputPath = inputPath;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateHandler.InputHandler<?> build() {
		return new StateHandler.InputHandler<Object>() {
			@Override
			public Object onInput(StateContext context) {
				return new JsonPath(context.getParameters()).select(inputPath);
			}
		};
	}
}
