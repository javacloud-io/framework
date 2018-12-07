package javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javacloud.framework.flow.StateContext;
import javacloud.framework.flow.StateHandler;
import javacloud.framework.flow.StateTransition;
import javacloud.framework.flow.spec.CatcherDefinition;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;

/**
 * {
 * 		"Catch":[
 * 			{ "ErrorEquals": ["a", "b"],
 * 			  "Next": "zzzz"
 * 			}
 * 		]
 * }
 * @author ho
 *
 */
public class FailureBuilder {
	private Map<String, CatcherDefinition> catchers;
	public FailureBuilder() {
	}
	
	/**
	 * 
	 * @param catcher
	 * @param errors
	 * @return
	 */
	public FailureBuilder withCatcher(CatcherDefinition catcher, String... errors) {
		if(catchers == null) {
			catchers = new HashMap<>();
		}
		if(Objects.isEmpty(errors)) {
			if(Objects.isEmpty(catcher.getErrorEquals())) {
				catchers.put(StateHandler.ERROR_ALL, catcher);
			} else {
				for(String error: catcher.getErrorEquals()) {
					catchers.put(error, catcher);
				}
			}
		} else {
			for(String error: errors) {
				catchers.put(error, catcher);
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @param catchers
	 * @return
	 */
	public FailureBuilder withCatchers(List<CatcherDefinition> catchers) {
		if(!Objects.isEmpty(catchers)) {
			for(CatcherDefinition catcher: catchers) {
				withCatcher(catcher);
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateHandler.FailureHandler build() {
		return new StateHandler.FailureHandler() {
			@Override
			public StateTransition onFailure(StateContext context, Exception ex) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				if(error == null && ex != null) {
					error = Exceptions.findReason(ex);
				}
				CatcherDefinition catcher = null;
				if(error != null) {
					catcher = (catchers == null? null : catchers.get(error));
				}
				if(catcher == null && catchers != null) {
					catcher = catchers.get(StateHandler.ERROR_ALL);
				}
				
				//GIVE UP WITHOUT CATCHER
				if(catcher == null) {
					return TransitionBuilder.failure();
				}
				
				//RESULT/OUTPUT
				OutputBuilder	builder = newOutputBuilder(catcher).withNext(catcher.getNext());
				Object result = catcher.getResult();
				if(result != null) {
					builder.withResult(result);
				}
				Object output = catcher.getOutput();
				if(output != null) {
					builder.withOutput(output);
				}
				return builder.build().onOutput(context);
			}
		};
	}
	
	/**
	 * return output handler from catcher
	 * 
	 * @param catcher
	 * @return
	 */
	protected OutputBuilder newOutputBuilder(CatcherDefinition catcher) {
		return new OutputBuilder();
	}
}
