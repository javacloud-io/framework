package javacloud.framework.cdi;
/**
 * Test a daemon service is running from runlist
 * 
 * @author ho
 *
 */
public class TestDaemonService {
	public void start() {
		System.out.println("STARTED: HELLO TEST!");
	}
	public void stop() {
		System.out.println("STOPPED: BYE TEST!");
	}
}
