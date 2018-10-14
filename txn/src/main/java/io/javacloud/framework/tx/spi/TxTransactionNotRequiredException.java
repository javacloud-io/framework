package io.javacloud.framework.tx.spi;
/**
 * 
 * @author ho
 *
 */
public class TxTransactionNotRequiredException extends TxTransactionException {
	private static final long serialVersionUID = 1799723808551192399L;

	public TxTransactionNotRequiredException(String message) {
		super(message);
	}
}
