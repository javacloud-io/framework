package io.javacloud.framework.flow.builder;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.json.internal.JsonTemplate;
/**
 * An input can be a path from parameters or actual input object
 * - Input: "$.xyz"
 * Will take xyz from context.getParameters() as input
 * 
 * - Input: {
 * 		"abc": "$.xyz"
 * }
 * Pass Input as json with abc has value of $.xyz
 * 
 * @author ho
 *
 */
public class InputBuilder {
	protected Object input = JsonPath.ROOT;
	public InputBuilder() {
	}
	
	/**
	 * 
	 * @param inputPath
	 * @return
	 */
	public InputBuilder withInputPath(String inputPath) {
		this.input = inputPath;
		return this;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public InputBuilder withInput(Object input) {
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
				if(input == null) {
					return context.getInput();
				}
				if((input instanceof String) && (JsonPath.is((String)input))) {
					return new JsonPath(context.getInput()).select((String)input);
				}
				return compileInput(new JsonPath(context.getInput()));
			}
		};
	}
	
	/**
	 * Compile the input to has correct value substitution
	 * 
	 * @param jsonPath
	 * @return
	 */
	protected Object compileInput(JsonPath jsonPath) {
		return new JsonTemplate(jsonPath).compile(input);
	}
}
