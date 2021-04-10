package javacloud.framework.config.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javacloud.framework.config.ConfigSource;
import javacloud.framework.util.GenericException;
import javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
public class StandardConfigSource implements ConfigSource {
	private final Map<String, String> properties;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public StandardConfigSource(Properties properties) {
		this.properties = (Map)properties;
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
	 * Standard configuration from -name value pair
	 * @param arguments
	 */
	public StandardConfigSource(String[] arguments) {
		this(parseProperties(arguments));
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
			throw GenericException.of("Unable to load config resource: " + resource, ex);
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
			throw GenericException.of("Unable to load config resource: " + resource, ex);
		}
	}
	
	/**
	 * Command line: CMD [options][operands]
	 * options: -X -Y --xyz value...
	 * operands: A B C
	 * 
	 * @param arguments
	 * @return
	 */
	static final Properties parseProperties(String[] arguments) {
		Properties props = new Properties();
		if(arguments != null) {
			List<String> operands = new ArrayList<>();
			for(int i = 0; i < arguments.length;) {
				String opt = arguments[i ++];
				if(opt.startsWith("-")) {
					String val = null;
					//expected value after --
					if(opt.startsWith("--")) {
						opt = opt.substring(2);
						if(i < arguments.length && !arguments[i].startsWith("-")) {
							val = arguments[i ++];
						}
					} else {
						opt = opt.substring(1);
					}
					props.put(opt, val == null? "" : val);
				} else {
					operands.add(opt);
				}
			}
			props.put("", operands);
		}
		return props;
	}
}
