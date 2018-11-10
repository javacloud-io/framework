package javacloud.framework.flow.builder;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateFunction;
import javacloud.framework.json.internal.JsonPath;
import javacloud.framework.json.internal.JsonTemplate;
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
	public StateFunction.InputHandler<?> build() {
		return new StateFunction.InputHandler<Object>() {
			@Override
			public Object onInput(StateContext context) {
				//NOT DEFINED => USING ORIGINAL INPUT
				if(input == null) {
					return context.getInput();
				}
				return	new JsonTemplate(context.getInput()).compile(input);
			}
		};
	}
}
