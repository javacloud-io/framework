package javacloud.framework.txn.sql;

import javacloud.framework.txn.internal.TransactionalModule;
import javacloud.framework.txn.spi.SessionManager;
import javacloud.framework.txn.spi.TransactionManager;
/**
 * 
 * @author ho
 *
 */
public class SqlTransactionalModule extends TransactionalModule {
	@Override
	protected void configure() {
		bind(SessionManager.class).to(SqlLocalTransactionManager.class);
		bind(TransactionManager.class).to(SqlLocalTransactionManager.class);
		
		super.configure();
	}
}
