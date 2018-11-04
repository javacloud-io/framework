package javacloud.framework.txn.spi;
/**
 * 
 * @author ho
 *
 */
public class TxTransactionRequiredException extends TxTransactionException {
	private static final long serialVersionUID = 1799723808551192399L;

	public TxTransactionRequiredException(String message) {
		super(message);
	}
}
