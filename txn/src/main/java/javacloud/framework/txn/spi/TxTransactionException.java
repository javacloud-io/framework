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
	
	//REQUIRED A TRANSACTION
	public static class Required extends TxTransactionException {
		private static final long serialVersionUID = 1799723808551192399L;

		public Required(String message) {
			super(message);
		}
	}
	
	//NOT REQUIRED A TRANSACTION
	public static class NotRequired extends TxTransactionException {
		private static final long serialVersionUID = 1799723808551192399L;

		public NotRequired(String message) {
			super(message);
		}
	}
}
