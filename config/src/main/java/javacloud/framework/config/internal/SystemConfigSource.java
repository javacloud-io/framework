package javacloud.framework.config.internal;

import java.io.File;
import java.util.Properties;

import javacloud.framework.util.Converters;
/**
 * Use in replace of System.getProperties() to inject runtime properties
 * 
 * 1. Environment variables
 * 1. Configuration file
 * 2. System properties
 */
public final class SystemConfigSource extends StandardConfigSource {
	private static final String CONFIG_FILE = "javacloud.config.file";
	private static final SystemConfigSource SYSTEM = new SystemConfigSource();
	
	SystemConfigSource() {
		super(systemProperties());
	}
	
	public static SystemConfigSource get() {
		return SYSTEM;
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name, Converters.toString(value));
	}
	
	public void setIfAbsent(String name, Object value) {
		properties.putIfAbsent(name, Converters.toString(value));
	}
	
	@Override
	public String getProperty(String name) {
		String value = super.getProperty(name);
		if (value == null) {
			return getEnv(name);
		}
		return value;
	}
	
	static String getEnv(String name) {
		// direct lookup from system ENV
		String value = System.getenv(name);
		
		// using upper case abc.test => ABC_TEST
		if (value == null) {
			String uname = name.replaceAll("\\.", "_").toUpperCase();
			value = System.getenv(uname);
		}
		return value;
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
