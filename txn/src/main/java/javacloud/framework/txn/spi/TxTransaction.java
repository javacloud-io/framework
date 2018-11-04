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
