package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.spi.StateSpec;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;

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
	private Map<String, StateSpec.Catcher> catchers;
	public FailureBuilder() {
	}
	
	/**
	 * 
	 * @param catcher
	 * @param errors
	 * @return
	 */
	public FailureBuilder withCatcher(StateSpec.Catcher catcher, String... errors) {
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
	public FailureBuilder withCatchiers(List<StateSpec.Catcher> catchers) {
		for(StateSpec.Catcher catcher: catchers) {
			withCatcher(catcher);
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
					error = UncheckedException.resolveCode(ex);
				}
				StateSpec.Catcher catcher = null;
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
	protected OutputBuilder newOutputBuilder(StateSpec.Catcher catcher) {
		return new OutputBuilder();
	}
}
