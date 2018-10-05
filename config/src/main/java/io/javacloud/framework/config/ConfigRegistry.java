package io.javacloud.framework.config;
/**
 * return config for given type safe
 * 
 * @author ho
 *
 */
public interface ConfigRegistry {
	/**
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> T getProperty(String name, Class<T> type);
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getConfig(Class<T> type);
}
