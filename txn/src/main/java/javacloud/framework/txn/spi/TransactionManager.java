package javacloud.framework.txn.spi;

import javacloud.framework.txn.Transactional;

/**
 * 
 * @author ho
 *
 */
public interface TransactionManager {
	/**
	 * 
	 * @return current transaction
	 */
	Transaction getTransaction();
	
	/**
	 * endTransaction by commit or roll-back 
	 * 
	 * @return a new transaction
	 */
	Transaction beginTransaction(Transactional transactional);
}
