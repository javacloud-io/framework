package javacloud.framework.txn.test;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;

import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.txn.spi.TxSessionManager;
/**
 * 
 * @author ho
 *
 */
public abstract class TxServiceTest extends ServiceTest {
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
