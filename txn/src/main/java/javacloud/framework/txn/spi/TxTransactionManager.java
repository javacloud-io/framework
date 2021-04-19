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
	TxTransaction getTransaction();
	
	/**
	 * Start a new transaction
	 * @return
	 */
	TxTransaction beginTransaction(Transactional transactional);
}
