package javacloud.framework.config;
/**
 * return config for given type safe
 * 
 * @author ho
 *
 */
public interface ConfigManager {
	/**
	 * return property value of given type
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> T getProperty(String name, Class<T> type);
	
	/**
	 * return config in class, type is any type-safe interface with method annotate with @ConfigProperty
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getConfig(Class<T> type);
}
