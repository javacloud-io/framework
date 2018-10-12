package io.javacloud.framework.tx;

import io.javacloud.framework.util.UncheckedException;
/**
 * 
 * @author ho
 *
 */
public class TransactionException extends UncheckedException {
	private static final long serialVersionUID = 7482734435469434117L;
	public TransactionException(String message) {
		super(message);
	}
	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
