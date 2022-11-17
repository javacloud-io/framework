package javacloud.framework.cdi.test;

import javacloud.framework.cdi.internal.GuiceModule;

import javax.inject.Singleton;
/**
 * @author ho
 *
 */
public class TestModule extends GuiceModule {
	@Override
	public void configure() {
		bind(TestService.class).to(TestServiceImpl.class);
		bind(TestInject.class);
		
		bindToName(TestService.class, "named").to(TestServiceImpl.class);
		bind(TestInjectNamed.class);
	}
	
	//TEST
	@Singleton
	public static class TestServiceImpl implements TestService {
	}
}
