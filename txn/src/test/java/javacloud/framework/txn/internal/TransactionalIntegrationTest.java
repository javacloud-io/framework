package javacloud.framework.txn.internal;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;

import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.txn.spi.SessionManager;
/**
 * 
 * @author ho
 *
 */
public abstract class TransactionalIntegrationTest extends IntegrationTest {
	@Inject SessionManager sessionManager;
	
	@Before
	public void beginSession() {
		sessionManager.beginSession();
	}
	
	@After
	public void endSession() {
		sessionManager.endSession();
	}
}
