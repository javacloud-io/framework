package javacloud.framework.txn.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.txn.Propagation;
import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.spi.TxTransactionManager;
import javacloud.framework.txn.spi.TxTransactionRequiredException;
import javacloud.framework.txn.sql.SqlTransaction;
import javacloud.framework.txn.test.TxServiceTest;
/**
 * 
 * @author ho
 *
 */
public class DerbyServiceTest extends TxServiceTest {
	@Inject
	TxTransactionManager transactionManager;
	
	@Test
	@Transactional()
	public void testTx() throws SQLException {
		Connection connection = ((SqlTransaction)transactionManager.getTransaction()).getConnection();
		Assert.assertNotNull(connection);
		
		testNewTx();
	}
	
	/**
	 * An exception will throw without any transaction
	 */
	@Test(expected=TxTransactionRequiredException.class)
	@Transactional(propagation=Propagation.MANDATORY)
	public void testNoTx(){
	}
	
	/**
	 * 
	 */
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void testNewTx(){
	}
}
