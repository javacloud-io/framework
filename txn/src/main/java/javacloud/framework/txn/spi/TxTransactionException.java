package javacloud.framework.txn.spi;

import javacloud.framework.util.ValidationException;
/**
 * 
 * @author ho
 *
 */
public class TxTransactionException extends ValidationException {
	private static final long serialVersionUID = 7482734435469434117L;
	public TxTransactionException(String message) {
		super(message);
	}
	
	public TxTransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
