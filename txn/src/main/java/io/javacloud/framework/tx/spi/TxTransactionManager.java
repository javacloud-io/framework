package io.javacloud.framework.tx.spi;

import io.javacloud.framework.tx.Transactional;

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
