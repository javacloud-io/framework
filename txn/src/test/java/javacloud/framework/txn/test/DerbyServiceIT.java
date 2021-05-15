package javacloud.framework.txn.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.txn.Propagation;
import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.internal.TransactionalIntegrationTest;
import javacloud.framework.txn.spi.TransactionManager;
import javacloud.framework.txn.sql.SqlTransaction;
import javacloud.framework.txn.spi.TransactionException;
/**
 * 
 * @author ho
 *
 */
public class DerbyServiceIT extends TransactionalIntegrationTest {
	@Inject
	TransactionManager transactionManager;
	
	@Test
	@Transactional()
	public void testTx() throws SQLException {
		Connection connection = ((SqlTransaction)transactionManager.getTransaction()).getConnection();
		Assert.assertNotNull(connection);
		System.out.println("tx");
		
		// call new tx
		testNewTx();
	}
	
	/**
	 * An exception will throw without any transaction
	 */
	@Test(expected=TransactionException.Required.class)
	@Transactional(propagation=Propagation.MANDATORY)
	public void testNoTx(){
		System.out.println("noTx");
	}
	
	/**
	 * 
	 */
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void testNewTx(){
		System.out.println("newTx");
	}
}
