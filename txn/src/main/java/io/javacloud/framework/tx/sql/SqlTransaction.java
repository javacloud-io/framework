package io.javacloud.framework.tx.sql;

import java.sql.Connection;
import java.sql.SQLException;

import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.spi.TxTransaction;
import io.javacloud.framework.tx.spi.TxTransactionException;
/**
 * 
 * @author ho
 *
 */
public abstract class SqlTransaction implements TxTransaction {
	private final Transactional transactional;
	private boolean active = true;
	public SqlTransaction(Transactional transactional) {
		this.transactional = transactional;
	}
	
	/**
	 * return current active connection for WORK.
	 * @return
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
		} catch(SQLException ex) {
			throw new TxTransactionException(ex.getMessage(), ex);
		} finally {
			active = false;
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void rollback() {
		try {
			getConnection().rollback();
		}catch(SQLException ex) {
			throw new TxTransactionException(ex.getMessage(), ex);
		} finally {
			active = false;
		}
	}
}
