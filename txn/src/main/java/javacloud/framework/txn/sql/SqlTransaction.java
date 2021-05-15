package javacloud.framework.txn.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.spi.Transaction;
import javacloud.framework.txn.spi.TransactionException;
/**
 * 
 * @author ho
 *
 */
public abstract class SqlTransaction implements Transaction {
	private final Transactional transactional;
	private boolean active = true;
	
	public SqlTransaction(Transactional transactional) {
		this.transactional = transactional;
	}
	
	/**
	 * 
	 * @return current active connection for WORK.
	 */
	public abstract Connection getConnection();
	
	/**
	 * 
	 */
	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * 
	 */
	@Override
	public Transactional getTransactional() {
		return transactional;
	}
	
	/**
	 * 
	 */
	@Override
	public void commit() {
		try {
			getConnection().commit();
			active = false;
		} catch(SQLException ex) {
			throw new TransactionException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void rollback() {
		try {
			getConnection().rollback();
			active = false;
		} catch(SQLException ex) {
			throw new TransactionException(ex.getMessage(), ex);
		}
	}
}
