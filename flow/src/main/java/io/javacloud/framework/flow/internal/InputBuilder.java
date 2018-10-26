package io.javacloud.framework.flow.internal;

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
	private Object		input;	//default input object
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
	 * @param input
	 * @return
	 */
	public <T> InputBuilder withInput(T input) {
		this.input = input;
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
				JsonPath jsonPath = new JsonPath(context.getParameters());
				if(input != null) {
					return jsonPath.compile(input);
				}
				return jsonPath.select(inputPath);
			}
		};
	}
}
