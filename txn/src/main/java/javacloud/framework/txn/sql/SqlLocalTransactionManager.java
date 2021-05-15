package javacloud.framework.txn.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.internal.LocalTransactionManager;
import javacloud.framework.txn.spi.Transaction;
import javacloud.framework.txn.spi.TransactionException;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ProxyInvocationHandler;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class SqlLocalTransactionManager extends LocalTransactionManager {
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
	 * FIXME: ENSURE native CONNECTION commit() or rollback() should behave same TX.
	 */
	@Override
	protected Transaction newTransaction(Transactional transactional) {
		try {
			Connection rawConnection = dataSource.getConnection();
			rawConnection.setAutoCommit(false);
			rawConnection.setReadOnly(transactional.readOnly());
			return new SqlTransactionImpl(rawConnection, transactional);
		} catch (SQLException ex) {
			throw new TransactionException("Unable to obtain a connection", ex);
		}
	}
	
	/**
	 * ENSURE CONNECTION IS CLOSE AFTER COMMIT.
	 */
	@Override
	protected boolean endTransaction(Transaction tx) {
		Connection connection = ((SqlTransaction)tx).getConnection();
		Objects.closeQuietly(connection);
		return super.endTransaction(tx);
	}
	
	//
	// PROXY THE CONNECTION TO ENSURE CORRECTNESS WHEN CLOSE RAW CONNECTION!
	class SqlTransactionImpl extends SqlTransaction {
		private final Connection connection;
		
		public SqlTransactionImpl(final Connection rawConnection, Transactional transactional) {
			super(transactional);
			this.connection = ProxyInvocationHandler.newInstance(new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					try {
						return method.invoke(rawConnection, args);
					} finally {
						if("commit".equals(method.getName()) || "rollback".equals(method.getName())) {
							endTransaction(SqlTransactionImpl.this);
						}
					}
				}
			}, Connection.class);
		}
		
		@Override
		public Connection getConnection() {
			return connection;
		}
	}
}
