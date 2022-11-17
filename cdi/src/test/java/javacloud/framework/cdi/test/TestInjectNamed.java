package javacloud.framework.cdi.test;

import javax.inject.Inject;
import javax.inject.Named;
/**
 * 
 * @author ho
 *
 */
public class TestInjectNamed {
	@Inject @Named("named")
	TestService service;
	
	public TestService getService() {
		return service;
	}
}
