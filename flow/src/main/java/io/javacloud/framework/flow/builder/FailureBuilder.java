package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.util.Objects;

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
	public static class Catcher implements StateTransition.Success {
		private Object result = JsonPath.ROOT;
		private Object output = JsonPath.ROOT;
		
		private String next;
		public Catcher() {
		}
		@Override
		public boolean isEnd() {
			return false;
		}
		@Override
		public String getNext() {
			return next;
		}
		public Catcher withNext(String next) {
			this.next = next;
			return this;
		}
		
		public Object getResult() {
			return result;
		}
		public Catcher withResult(Object result) {
			this.result = result;
			return this;
		}
		
		public Object getOutput() {
			return output;
		}
		public Catcher withOutput(Object output) {
			this.output = output;
			return this;
		}
	}
	private Map<String, Catcher> catchers;
	public FailureBuilder() {
	}
	
	/**
	 * 
	 * @param catcher
	 * @param errors
	 * @return
	 */
	public FailureBuilder withCatchier(Catcher catcher, String... errors) {
		if(catchers == null) {
			catchers = new HashMap<>();
		}
		if(Objects.isEmpty(errors)) {
			catchers.put(StateHandler.ERROR_ALL, catcher);
		} else {
			for(String error: errors) {
				catchers.put(error, catcher);
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
					error = ex.getClass().getName();
				}
				Catcher catcher = null;
				if(error != null) {
					catcher = (catchers == null? null : catchers.get(error));
				}
				if(catcher == null && catchers != null) {
					catcher = catchers.get(StateHandler.ERROR_ALL);
				}
				return (catcher == null? TransitionBuilder.failure() : outputHandler(catcher).onOutput(context));
			}
		};
	}
	
	/**
	 * return output handler from catcher
	 * @param catcher
	 * @return
	 */
	protected StateHandler.OutputHandler outputHandler(Catcher catcher) {
		return	new OutputBuilder()
								.withOutput(catcher.output)
								.withResult(catcher.result)
								.withNext(catcher.next)
								.build();
	}
}
