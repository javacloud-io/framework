package javacloud.framework.txn.spi;

import javacloud.framework.util.UncheckedException;
/**
 * 
 * @author ho
 *
 */
public class TxTransactionException extends UncheckedException {
	private static final long serialVersionUID = 7482734435469434117L;
	public TxTransactionException(String message) {
		super(message);
	}
	
	public TxTransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
