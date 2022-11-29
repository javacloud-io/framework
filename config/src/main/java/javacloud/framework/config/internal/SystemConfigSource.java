package javacloud.framework.config.internal;

import java.io.File;
import java.util.Properties;

import javacloud.framework.util.Converters;
/**
 * Use in replace of System.getProperties() to inject runtime properties
 *
 */
public final class SystemConfigSource extends StandardConfigSource {
	private static final String CONFIG_FILE = "javacloud.config.file";
	private static final SystemConfigSource SOURCE = new SystemConfigSource();
	
	SystemConfigSource() {
		super(systemProperties());
	}
	
	public static SystemConfigSource get() {
		return SOURCE;
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name, Converters.toString(value));
	}
	
	public void setPropertyIfAbsent(String name, Object value) {
		properties.putIfAbsent(name, Converters.toString(value));
	}
	
	/**
	 * 
	 * @return merge of file and system properties
	 */
	static Properties systemProperties() {
		Properties properties = new Properties();
		String configFile = System.getProperty(CONFIG_FILE, System.getenv(CONFIG_FILE));
		if (configFile != null) {
			properties.putAll(loadProperties(new File(configFile)));
		}
		properties.putAll(System.getProperties());
		properties.remove(CONFIG_FILE);
		return properties;
	}
}
