package io.javacloud.framework.server.test;

import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.AfterClass;

/**
 * Make sure only ONE server per all the tests method. Basically just to make sure container start right after create
 * and never be shutdown unless the class teared down.
 * 
 * @author ho
 *
 */
public abstract class RestApplicationTest extends JerseyApplicationTest {
	private static TestContainerFactoryImpl testContainerFactory = null;
	public RestApplicationTest() {
	}
	
	/**
	 * Make sure to shutdown the container because it just started by ONLY ONE
	 * 
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {
		if(testContainerFactory != null) {
			try {
				testContainerFactory.shutdown();
			} finally {
				testContainerFactory = null;
			}
		}
	}
	
	/**
	 * Statically cache so it can be shutdown after the class
	 */
	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		if(testContainerFactory == null) {
			testContainerFactory = new TestContainerFactoryImpl(super.getTestContainerFactory());
		}
		return testContainerFactory;
	}
}
