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
