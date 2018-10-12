package io.javacloud.framework.cdi.tx;

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
	public TxTransaction beginTransaction(Transactional transactional);
	
	/**
	 * 
	 * @return
	 */
	public TxTransaction getTransaction();
}
