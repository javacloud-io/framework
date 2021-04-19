package javacloud.framework.cdi;

import javacloud.framework.util.ResourceLoader;

/**
 * Helper class to invoke a method using CDI. Start/Stop managed service from: META-INF/javacloud.cdi.services.runlist
 * 
 * @author ho
 *
 */
public abstract class ServiceBootstrapper {
	private static final ServiceBootstrapper BOOTSTRAP = ResourceLoader.loadService(ServiceBootstrapper.class);
	
	protected ServiceBootstrapper() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final ServiceBootstrapper get() {
		return BOOTSTRAP;
	}
	
	/**
	 * Start managed services registered to run-list. It's safe to be call multiple times.
	 * 
	 * @throws Exception
	 */
	public abstract void startup() throws Exception;
	
	/**
	 * Stop managed services registered to run-list
	 * 
	 * @throws Exception
	 */
	public abstract void shutdown() throws Exception;
}
