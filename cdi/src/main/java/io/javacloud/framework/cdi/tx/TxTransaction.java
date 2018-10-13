package io.javacloud.framework.cdi.tx;

import javax.inject.Provider;

import io.javacloud.framework.tx.Transactional;

/**
 * Transaction backed by underline transaction.
 * 
 * @author ho
 *
 * @param <T>
 */
public interface TxTransaction<Tx> extends Provider<Tx> {
	/**
	 * 
	 * @return
	 */
	public Transactional getTransactional();
	
	/**
	 * 
	 * @return
	 */
	public boolean isActive();
	
	/**
	 * 
	 */
	public void commit();
	
	/**
	 * 
	 */
	public void rollback();
}
