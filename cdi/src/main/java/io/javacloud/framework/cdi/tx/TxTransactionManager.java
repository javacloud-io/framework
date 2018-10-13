package io.javacloud.framework.cdi.tx;

import io.javacloud.framework.tx.Transactional;

/**
 * 
 * @author ho
 *
 */
public interface TxTransactionManager<Tx> {
	/**
	 * 
	 * @return
	 */
	public TxTransaction<Tx> beginTransaction(Transactional transactional);
	
	/**
	 * 
	 * @return
	 */
	public TxTransaction<Tx> getTransaction();
}
