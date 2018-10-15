package io.javacloud.framework.cdi.test;

import io.javacloud.framework.cdi.ServiceRegistry;
import io.javacloud.framework.cdi.ServiceRunner;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
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
	 * Make sure to start/stop service after a RUN.
	 */
	@Override
	public void run(RunNotifier notifier) {
		try {
			ServiceRunner.get().startServices();
			try {
				super.run(notifier);
			} finally {
				ServiceRunner.get().stopServices();
			}
		} catch(Exception ex) {
			notifier.fireTestFailure(new Failure(getDescription(), ex));
		}
	}

	/**
	 * Always inject the members to test instance
	 */
	@Override
	protected Object createTest() throws Exception {
		return	ServiceRegistry.get().getInstance(getTestClass().getJavaClass());
	}
}
