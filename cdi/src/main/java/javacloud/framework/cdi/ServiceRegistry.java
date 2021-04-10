package javacloud.framework.cdi;

import java.util.List;

import javacloud.framework.util.ResourceLoader;

/**
 * Basic registry operation for lookup & register object, module...from: META-INF/javacloud.cdi.services
 * 
 * @author ho
 *
 */
public abstract class ServiceRegistry {
	private static final ServiceRegistry REGISTRY = ResourceLoader.loadService(ServiceRegistry.class);
	protected ServiceRegistry() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final ServiceRegistry get() {
		return REGISTRY;
	}
	
	/**
	 * Get class instance from registry
	 * 
	 * @param type
	 * @return
	 */
	public abstract <T> T getInstance(Class<T> type);
	
	/**
	 * Get instance using @Named
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	public abstract <T> T getInstance(Class<T> type, String name);
	
	/**
	 * 
	 * @param type
	 * @param names
	 * @return all instances of the given service type, intended for plugin and extension. Plugin should bind
	 * with random name
	 */
	public abstract <T> List<T> getInstances(Class<T> type, String... names);
}
