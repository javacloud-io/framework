package io.javacloud.framework.tx.test;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import io.javacloud.framework.cdi.test.ServiceTest;
import io.javacloud.framework.tx.spi.TxSessionManager;
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
