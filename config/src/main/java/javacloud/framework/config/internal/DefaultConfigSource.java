package javacloud.framework.config.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javacloud.framework.config.ConfigSource;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.ResourceLoader;
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
	 * Load from an external file
	 * @param resource
	 */
	public DefaultConfigSource(File resource) {
		this(loadProperties(resource));
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
			throw Exceptions.asUnchecked("Unable to load config resource: " + resource, ex);
		}
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	static final Properties loadProperties(File resource) {
		try (FileReader reader = new FileReader(resource)){
			Properties props = new Properties();
			props.load(reader);
			return props;
		} catch (IOException ex) {
			throw Exceptions.asUnchecked("Unable to load config resource: " + resource, ex);
		}
	}
}
