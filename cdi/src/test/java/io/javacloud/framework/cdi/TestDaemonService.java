package io.javacloud.framework.cdi;
/**
 * Test a daemon service is running from runlist
 * 
 * @author ho
 *
 */
public class TestDaemonService {
	public void start() {
		System.out.println("HELLO TEST!");
	}
	public void stop() {
		System.out.println("BYE TEST!");
	}
}
