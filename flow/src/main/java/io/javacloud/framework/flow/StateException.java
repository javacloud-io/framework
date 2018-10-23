package io.javacloud.framework.flow;

import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.UncheckedException;
/**
 * 
 * @author ho
 *
 */
public class StateException extends UncheckedException {
	private static final long serialVersionUID = -3512853000283198674L;
	public static final String ALL 	   = "States.ALL";
	public static final String TIMEOUT = "States.Timeout";
	
	/**
	 * 
	 * @param code
	 */
	public StateException(String code) {
		super(code);
	}
	
	/**
	 * ALWAYS USING MESSAGE AS REASON CODE
	 */
	@Override
	public String getCode() {
		String message = super.getMessage();
		return Objects.isEmpty(message) ? super.getCode() : message;
	}
}
