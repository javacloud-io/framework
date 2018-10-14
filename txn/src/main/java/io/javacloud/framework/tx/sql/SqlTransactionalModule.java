package io.javacloud.framework.tx.sql;

import io.javacloud.framework.tx.internal.TxTransactionalModule;
import io.javacloud.framework.tx.spi.TxSessionManager;
import io.javacloud.framework.tx.spi.TxTransactionManager;
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
