package com.appe.framework;

import java.util.List;

/**
 * Basic registry operation for lookup & register object, module...
 * 
 * @author ho
 *
 */
public abstract class AppeRegistry {
	private static final AppeRegistry REGISTRY = AppeLoader.loadService(AppeRegistry.class);
	protected AppeRegistry() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final AppeRegistry get() {
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
	 * return all instances of the given service type, intended for plugin and extension. Plugin should bind
	 * with random name
	 * 
	 * @param type
	 * @param names
	 * @return
	 */
	public abstract <T> List<T> getInstances(Class<T> type, String... names);
}
