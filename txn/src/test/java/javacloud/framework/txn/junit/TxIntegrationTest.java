package javacloud.framework.txn.junit;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;

import javacloud.framework.cdi.junit.IntegrationTest;
import javacloud.framework.txn.spi.TxSessionManager;
/**
 * 
 * @author ho
 *
 */
public abstract class TxIntegrationTest extends IntegrationTest {
	@Inject TxSessionManager sessionManager;
	
	@Before
	public void beginSession() {
		sessionManager.beginSession();
	}
	
	@After
	public void endSession() {
		sessionManager.endSession();
	}
}
