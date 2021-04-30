package javacloud.framework.config.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javacloud.framework.config.ConfigSource;
import javacloud.framework.util.Converters;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
public class StandardConfigSource implements ConfigSource {
	protected final Map<String, String> properties;
	
	/**
	 * 
	 * @param properties
	 */
	public StandardConfigSource(Map<String, String> properties) {
		this.properties = properties;
	}
	
	/**
	 * 
	 * @param properties
	 */
	public StandardConfigSource(Properties properties) {
		this(properties.entrySet().stream()
					   .collect(Collectors.toMap(e -> (String)e.getKey(), e -> Converters.toString(e.getValue()))));
	}
	
	/**
	 * 
	 * @param resource
	 * @param loader
	 */
	public StandardConfigSource(String resource, ClassLoader loader) {
		this(loadProperties(resource, loader));
	}
	
	/**
	 * Load from an external file
	 * @param resource
	 */
	public StandardConfigSource(File resource) {
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
	 * 
	 * @param name
	 * @param defval
	 * @return
	 */
	public String getProperty(String name, String defval) {
		String val = getProperty(name);
		return (val == null? defval: val);
	}
	
	/**
	 * 
	 * @return all available keys
	 */
	public Set<String> keySet() {
		return Collections.unmodifiableSet(properties.keySet());
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
			throw InternalException.of("Unable to load config resource: " + resource, ex);
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
			throw InternalException.of("Unable to load config resource: " + resource, ex);
		}
	}
}
