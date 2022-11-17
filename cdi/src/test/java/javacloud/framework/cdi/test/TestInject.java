package javacloud.framework.cdi.test;

import javax.inject.Inject;
/**
 * 
 * @author ho
 *
 */
public class TestInject {
	@Inject
	TestService service;
	
	public TestService getService() {
		return service;
	}
}
