package javacloud.framework.config;
/**
 * To discover the configuration during runtime in both testing and production.
 * 
 * 1. By default, configuration will be individually loaded
 * 2. Other auto discovery ConfigSource if any will be discover through CDI
 * 
 * @author ho
 *
 */
public interface ConfigSource {
	/**
	 * return property value of given property name
	 * 
	 * @param name
	 * @return
	 */
	public String getProperty(String name);
}
