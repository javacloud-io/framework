package javacloud.framework.config;
/**
 * return config for given type safe
 * 
 * @author ho
 *
 */
public interface ConfigManager {
	/**
	 * 
	 * @param name
	 * @param type
	 * @return property value of given type
	 */
	public <T> T getProperty(String name, Class<T> type);
	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @param cacheable
	 * @return
	 */
	public <T> T getConfig(Class<T> type, boolean cacheable);
	
	/**
	 * 
	 * @param type
	 * @return config in class, type is any type-safe interface with method annotate with @ConfigProperty
	 */
	default public <T> T getConfig(Class<T> type) {
		return getConfig(type, true);
	}
}
