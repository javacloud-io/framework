package io.javacloud.framework.config.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import io.javacloud.framework.config.ConfigSource;
import io.javacloud.framework.util.ResourceLoader;
import io.javacloud.framework.util.UncheckedException;
/**
 * 
 * @author ho
 *
 */
public class DefaultConfigSource implements ConfigSource {
	private final Map<String, String> properties;
	/**
	 * 
	 * @param properties
	 */
	public DefaultConfigSource(Map<String, String> properties) {
		this.properties = properties;
	}
	
	/**
	 * 
	 * @param properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultConfigSource(Properties properties) {
		this.properties = (Map)properties;
	}
	
	/**
	 * 
	 * @param resource
	 * @param loader
	 */
	public DefaultConfigSource(String resource, ClassLoader loader) {
		this(loadProperties(resource, loader));
	}
	
	/**
	 * 
	 */
	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	/**
	 * Load from resource from class path
	 * 
	 * @param resource
	 * @param loader
	 * @return
	 */
	static final Properties loadProperties(String resource, ClassLoader loader) {
		try {
			Properties props = ResourceLoader.loadProperties(resource, loader);
			return (props == null? new Properties() : props);
		} catch (IOException ex) {
			throw new UncheckedException("Unable to load config resource: " + resource, ex);
		}
	}
}
