package io.javacloud.framework.tx.spi;

import io.javacloud.framework.tx.Transactional;

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
	public boolean isActive();
	
	/**
	 * 
	 * @return
	 */
	public Transactional getTransactional();
	
	/**
	 * 
	 */
	public void commit();
	
	/**
	 * 
	 */
	public void rollback();
}
