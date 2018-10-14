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
public class SqlTransaction implements TxTransaction {
	private final Connection connection;
	private final Transactional transactional;
	private boolean active = true;
	public SqlTransaction(Connection connection, Transactional transactional) {
		this.connection = connection;
		this.transactional = transactional;
	}
	
	/**
	 * return current active connection for WORK.
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}
	
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
			connection.commit();
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
			connection.rollback();
		}catch(SQLException ex) {
			throw new TxTransactionException(ex.getMessage(), ex);
		} finally {
			active = false;
		}
	}
}
