package io.javacloud.framework.tx.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.internal.TxLocalTransactionManager;
import io.javacloud.framework.tx.spi.TxTransaction;
import io.javacloud.framework.tx.spi.TxTransactionException;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class SqlLocalTransactionManager extends TxLocalTransactionManager {
	private final DataSource dataSource;
	
	/**
	 * 
	 * @param dataSource
	 */
	@Inject
	public SqlLocalTransactionManager(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * ENSURE CONNECTION IS CLOSE AFTER COMMIT.
	 */
	@Override
	protected void endTransaction(TxTransaction tx) {
		Connection connection = ((SqlTransaction)tx).getConnection();
		try {
			connection.close();
		} catch(SQLException ex) {
			//WARN ABOUT CONNECTION CLOSING
		} finally {
			super.endTransaction(tx);
		}
	}

	/**
	 * 1. ENSURE TO OPEN A CONNECTION IF ONE NOT EXISTED.
	 */
	@Override
	protected TxTransaction newTransaction(Transactional transactional) {
		try {
			final Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			connection.setReadOnly(transactional.readOnly());
			return new SqlTransaction(connection, transactional) {
				@Override
				public void commit() {
					try {
						super.commit();
					} finally {
						endTransaction(this);
					}
				}

				@Override
				public void rollback() {
					try {
						super.rollback();
					} finally {
						endTransaction(this);
					}
				}
			};
		} catch(SQLException ex) {
			throw new TxTransactionException("Unable to obtain a connection", ex);
		}
	}
}
