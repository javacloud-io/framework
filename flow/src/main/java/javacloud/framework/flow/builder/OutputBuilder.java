package javacloud.framework.flow.builder;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.json.internal.JsonPath;
import javacloud.framework.json.internal.JsonTemplate;
import javacloud.framework.util.Objects;

/**
 * By default RESULT will overwrite the INPUT to be OUTPUT. OUTPUT will be AS IS to pass to INPUT
 * 
 * @author ho
 *
 */
public class OutputBuilder {
	protected Object result = JsonPath.ROOT;
	protected Object output = JsonPath.ROOT;
	
	private int delaySeconds;
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
	 * 
	 * @param delaySeconds
	 * @return
	 */
	public OutputBuilder withDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
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
				
				//IF RESULT IS PATH => TAKE CURRENT RESULT AND MERGE TO INPUT
				String resultPath = JsonPath.ROOT;
				if(result == null) {
					finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				} else if(result instanceof String && JsonPath.is((String)result)){
					resultPath = (String)result;
					finalResult = context.getAttribute(StateContext.ATTRIBUTE_RESULT);
				} else {
					finalResult = new JsonTemplate(context.getInput()).compile(result);
				}
				
				//PROCESS RESULT
				if(finalResult != null) {
					finalResult = new JsonPath(context.getInput()).merge(resultPath, finalResult);
				} else {
					finalResult = context.getInput();
				}
				
				//IF THERE IS OUTPUT DEFINE => COMPILE IT
				if(output != null) {
					finalResult = new JsonTemplate(finalResult).compile(output);
				}
				
				//SET BACK RESULT
				context.setAttribute(StateContext.ATTRIBUTE_RESULT, finalResult == null? Objects.asMap() : finalResult);
				return TransitionBuilder.success(next, delaySeconds);
			}
		};
	}
}
