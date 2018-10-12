package io.javacloud.framework.cdi.tx;

import io.javacloud.framework.tx.Transactional;

/**
 * 
 * @author ho
 *
 */
public interface TxTransaction {
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
