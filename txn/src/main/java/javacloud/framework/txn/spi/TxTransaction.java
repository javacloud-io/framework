package javacloud.framework.txn.spi;

import javacloud.framework.txn.Transactional;

/**
 * Transaction backed by underline transaction.
 * 
 * @author ho
 * 
 */
public interface TxTransaction {
	/**
	 * 
	 * @return
	 */
	boolean isActive();
	
	/**
	 * 
	 * @return
	 */
	Transactional getTransactional();
	
	/**
	 * 
	 */
	void commit();
	
	/**
	 * 
	 */
	void rollback();
}
