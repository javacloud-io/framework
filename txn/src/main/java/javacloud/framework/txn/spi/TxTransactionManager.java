package javacloud.framework.txn.spi;

import javacloud.framework.txn.Transactional;

/**
 * 
 * @author ho
 *
 */
public interface TxTransactionManager {
	/**
	 * 
	 * @return
	 */
	public TxTransaction getTransaction();
	
	/**
	 * Start a new transaction
	 * @return
	 */
	public TxTransaction beginTransaction(Transactional transactional);
}
