package javacloud.framework.txn.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.internal.TxLocalTransactionManager;
import javacloud.framework.txn.spi.TxTransaction;
import javacloud.framework.txn.spi.TxTransactionException;
import javacloud.framework.util.ProxyInvocationHandler;
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
		} catch (SQLException ex) {
			throw new TxTransactionException("Unable to obtain a connection", ex);
		}
	}
	
	//
	//PROXY THE CONNECTION TO ENSURE CORRECTNESS
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
