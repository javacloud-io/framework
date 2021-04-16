package javacloud.framework.cdi.test;

import java.util.logging.Logger;

/**
 * Test a daemon service is running from runlist
 * 
 * @author ho
 *
 */
public class TestDaemonService {
	private static final Logger logger = Logger.getLogger(TestDaemonService.class.getName());
	
	public void start() {
		logger.info("STARTED: HELLO TEST!");
	}
	public void stop() {
		logger.info("STOPPED: BYE TEST!");
	}
}
