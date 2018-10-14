package io.javacloud.framework.tx.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import io.javacloud.framework.tx.Propagation;
import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.spi.TxTransactionException;
import io.javacloud.framework.tx.spi.TxTransactionManager;
import io.javacloud.framework.tx.test.TxServiceTest;
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
	@Test(expected=TxTransactionException.class)
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
