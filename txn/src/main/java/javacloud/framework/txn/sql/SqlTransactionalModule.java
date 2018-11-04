package javacloud.framework.txn.sql;

import javacloud.framework.txn.internal.TxTransactionalModule;
import javacloud.framework.txn.spi.TxSessionManager;
import javacloud.framework.txn.spi.TxTransactionManager;
/**
 * 
 * @author ho
 *
 */
public class SqlTransactionalModule extends TxTransactionalModule {
	@Override
	protected void configure() {
		bind(TxSessionManager.class).to(SqlLocalTransactionManager.class);
		bind(TxTransactionManager.class).to(SqlLocalTransactionManager.class);
		
		super.configure();
	}
}
