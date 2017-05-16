package com.appe.framework.resource;
/**
 * Manage the config and message bundle all together
 * 
 * @author ho
 *
 */
public interface ResourceManager {
	/**
	 * Which class loader to be use for resource hunting, will re-discover the resources.
	 * Can be use to initialize the resource prior to any form of loading.
	 */
	public boolean initResourceLoader();
	
	/**
	 * return current resource loader if one set
	 * @return
	 */
	public ClassLoader getResourceLoader();
	
	/**
	 * return a config bundle, using the system properties if baseName of bundle is EMPTY.
	 * 
	 * @param type
	 * @return
	 */
	public <T extends ConfigBundle> T getConfigBundle(Class<T> type);
	
	/**
	 * return i18n message bundle, if not specify a baseName a UNIVERSAL bundle will be loaded.
	 * 
	 * @param type
	 * @return
	 */
	public <T extends MessageBundle> T getMessageBundle(Class<T> type);
}
