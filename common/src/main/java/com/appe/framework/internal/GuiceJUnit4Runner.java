package com.appe.framework.internal;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Injector;
/**
 * Use to run anything test related to Guice. Using RunWith()
 * 
 * @author ho
 *
 */
public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {
	private Injector injector;
	/**
	 * 
	 * @param klass
	 * @throws InitializationError
	 */
	public GuiceJUnit4Runner(Class<?> klass) throws InitializationError {
		super(klass);
		this.injector = resolveInjector();
	}
	
	/**
	 * By default we always use the injector from registry
	 * 
	 * @return
	 */
	protected Injector resolveInjector() {
		return GuiceFactory.registryInjector();
	}
	
	/**
	 * Always inject the members to test instance
	 */
	@Override
	protected Object createTest() throws Exception {
		Object instance = super.createTest();
		injector.injectMembers(instance);
		return instance;
	}
	
}
