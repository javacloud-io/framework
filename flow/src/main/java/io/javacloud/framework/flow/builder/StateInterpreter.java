package io.javacloud.framework.flow.builder;

import java.text.ParseException;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateFunction;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.StateTransition.Success;
import io.javacloud.framework.flow.spi.StateSpec;
import io.javacloud.framework.json.internal.JsonTemplate;
import io.javacloud.framework.util.DateFormats;
import io.javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public abstract class StateInterpreter implements StateFunction {
	protected final StateSpec spec;
	protected StateInterpreter(StateSpec spec) {
		this.spec = spec;
	}
	
	/**
	 * DEFAULT TO OBJECT TYPE
	 */
	@Override
	public Class<?> getParametersType() {
		return Object.class;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public Object onInput(StateContext context) {
		InputBuilder builder = new InputBuilder()
				.withInput(spec.getInput());
		return builder.build().onInput(context);
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public StateTransition onFailure(StateContext context, Exception ex) {
		FailureBuilder builder = new FailureBuilder()
				.withCatchers(spec.getCatchers());
		return builder.build().onFailure(context, ex);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public StateTransition onRetry(StateContext context) {
		RetryBuilder builder = new RetryBuilder()
				.withRetriers(spec.getRetriers());
		return builder.build().onRetry(context);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public Success onOutput(StateContext context) {
		OutputBuilder builder = new OutputBuilder()
				.withResult(spec.getResult())
				.withOutput(spec.getOutput())
				.withNext(spec.getNext())
				.withDelaySeconds(getDelaySeconds(context));
		return builder.build().onOutput(context);
	}
	
	/**
	 * Delay seconds prior to move to next
	 * @return
	 */
	protected int getDelaySeconds(StateContext context) {
		return 0;
	}
	
	//PASS
	public static class Pass extends StateInterpreter {
		public Pass(StateSpec.Pass spec) {
			super(spec);
		}

		@Override
		public Status handle(Object parameters, StateContext context) throws Exception {
			return Status.SUCCESS;
		}
	}
	
	//FAIL
	public static class Fail extends StateInterpreter {
		public Fail(StateSpec.Fail spec) {
			super(spec);
		}

		@Override
		public Status handle(Object parameters, StateContext context) throws Exception {
			return TransitionBuilder.failure(context, ((StateSpec.Fail)spec).getError());
		}
	}
	
	//WAIT
	public static class Wait extends StateInterpreter {
		public Wait(StateSpec.Wait spec) {
			super(spec);
		}

		@Override
		public Status handle(Object parameters, StateContext context) throws Exception {
			return Status.SUCCESS;
		}
		
		//FIXME: CALCULATE TMEOUT SECONDS
		protected int getDelaySeconds(StateContext context) {
			try {
				StateSpec.Wait waitSpec = (StateSpec.Wait)spec;
				int delaySeconds;
				if(!Objects.isEmpty(waitSpec.getTimestamp())) {
					String source = new JsonTemplate(context.getInput()).compile(waitSpec.getTimestamp());
					delaySeconds= (int)((DateFormats.getUTC().parse(source).getTime() - System.currentTimeMillis()) / 1000L);
				} else {
					delaySeconds= ((StateSpec.Wait)spec).getSeconds();
				}
				return delaySeconds;
			}catch(ParseException ex) {
				return 0;
			}
		}
	}
	
	//TASK, CHOICE, PARALLEL
}
