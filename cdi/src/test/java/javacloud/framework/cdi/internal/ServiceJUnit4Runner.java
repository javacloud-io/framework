package javacloud.framework.cdi.internal;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.cdi.ServiceBootstrapper;
/**
 * Use to run anything test related to Guice using RunWith()
 * 
 * @author ho
 *
 */
public class ServiceJUnit4Runner extends BlockJUnit4ClassRunner {
	/**
	 * 
	 * @param testClass
	 * @throws InitializationError
	 */
	public ServiceJUnit4Runner(Class<?> testClass) throws InitializationError {
		super(testClass);
		
	}
	
	/**
	 * Make sure to start/stop services after a class RUN.
	 */
	@Override
	public void run(RunNotifier notifier) {
		try {
			ServiceBootstrapper.get().startup();
			try {
				super.run(notifier);
			} finally {
				ServiceBootstrapper.get().shutdown();
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
