package io.javacloud.framework.tx.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import io.javacloud.framework.tx.Transactional;
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
	@Transactional(readOnly=true)
	public void testTx() throws SQLException {
		Connection connection = ((SqlTransaction)transactionManager.getTransaction()).getConnection();
		Assert.assertNotNull(connection);
	}
}
