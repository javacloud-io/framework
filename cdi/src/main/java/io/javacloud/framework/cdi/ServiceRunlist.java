package io.javacloud.framework.cdi;

import io.javacloud.framework.util.ResourceLoader;

/**
 * Helper class to invoke a method using CDI. Start/Stop managed service from: META-INF/javacloud.cdi.services.runlist
 * 
 * @author ho
 *
 */
public abstract class ServiceRunlist {
	private static final ServiceRunlist RUNLIST = ResourceLoader.loadService(ServiceRunlist.class);
	protected ServiceRunlist() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final ServiceRunlist get() {
		return RUNLIST;
	}
	
	/**
	 * Start managed services registered to runlist. It's safe to be call multiple times.
	 * 
	 * @throws Exception
	 */
	public abstract void startServices() throws Exception;
	
	/**
	 * Stop managed services registered to runlist
	 * 
	 * @throws Exception
	 */
	public abstract void stopServices() throws Exception;
		
	/**
	 * Run a method of service in unmanaged mode
	 * 
	 * @param zclass
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public abstract <T> T runMethod(Class<?> zclass, String methodName, Object...args)
			throws Exception;
	
}
