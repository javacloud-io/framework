package io.javacloud.framework.tx.sql;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import io.javacloud.framework.internal.ProxyInvocationHandler;
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
	 * FIXME: ENSURE native CONNECTION commit() or rollback() should behave same TX.
	 */
	@Override
	protected TxTransaction newTransaction(Transactional transactional) {
		try {
			Connection rawConnection = dataSource.getConnection();
			rawConnection.setAutoCommit(false);
			rawConnection.setReadOnly(transactional.readOnly());
			return new SqlTransactionImpl(rawConnection, transactional);
		} catch(SQLException ex) {
			throw new TxTransactionException("Unable to obtain a connection", ex);
		}
	}
	
	//
	//PROXY THE CONNECTION TO ENSURE CORRECTNESS
	class SqlTransactionImpl extends SqlTransaction {
		private final Connection connection;
		public SqlTransactionImpl(final Connection rawConnection, Transactional transactional) {
			super(transactional);
			this.connection = (Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class}, new ProxyInvocationHandler() {
				@Override
				protected Object invoke(Method method, Object[] args) throws Throwable {
					try {
						return method.invoke(rawConnection, args);
					} finally {
						if("commit".equals(method.getName()) || "rollback".equals(method.getName())) {
							endTransaction(SqlTransactionImpl.this);
						}
					}
				}
			});
		}
		
		@Override
		public Connection getConnection() {
			return connection;
		}
	}
}
