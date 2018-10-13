package io.javacloud.framework.cdi.test;

import io.javacloud.framework.cdi.internal.GuiceRegistry;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
/**
 * Use to run anything test related to Guice using RunWith()
 * 
 * @author ho
 *
 */
public class ServiceJUnit4Runner extends BlockJUnit4ClassRunner {
	/**
	 * 
	 * @param klass
	 * @throws InitializationError
	 */
	public ServiceJUnit4Runner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	/**
	 * Always inject the members to test instance
	 */
	@Override
	protected Object createTest() throws Exception {
		return	GuiceRegistry.get().getInstance(getTestClass().getJavaClass());
	}
}
