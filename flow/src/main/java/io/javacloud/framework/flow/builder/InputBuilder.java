package io.javacloud.framework.flow.builder;

import java.io.IOException;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.json.internal.JacksonConverter;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class InputBuilder {
	private String 		inputPath;
	private Class<?> 	inputClass;
	private Externalizer externalizer;
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
	 * @param inputClass
	 * @return
	 */
	public InputBuilder withInputClass(Class<?> inputClass) {
		this.inputClass = inputClass;
		return this;
	}
	public InputBuilder withExternalizer(Externalizer externalizer) {
		this.externalizer = externalizer;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateHandler.InputHandler build() {
		return new StateHandler.InputHandler() {
			@Override
			public <T> T onInput(StateContext context) {
				Object parameters = new JsonPath(context.getParameters()).select(inputPath);
				//AUTO CONVERSION
				if(parameters != null && inputClass != null && !inputClass.isInstance(parameters) && externalizer != null) {
					JacksonConverter converter = new JacksonConverter(externalizer);
					try {
						parameters = converter.toObject(converter.toBytes(parameters), inputClass);
					} catch(IOException ex) {
						throw new IllegalArgumentException(ex);
					}
				}
				return Objects.cast(parameters);
			}
		};
	}
}
