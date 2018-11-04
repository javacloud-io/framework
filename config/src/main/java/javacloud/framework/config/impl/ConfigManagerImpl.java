package javacloud.framework.config.impl;

import javax.inject.Singleton;

import javacloud.framework.config.ConfigManager;
import javacloud.framework.config.ConfigSource;
import javacloud.framework.config.internal.ConfigSourceResolver;
import javacloud.framework.config.internal.DefaultConfigSource;
import javacloud.framework.util.Converters;
import javacloud.framework.util.ProxyInvocationHandler;
import javacloud.framework.util.ResourceLoader;

/**
 * 1. Default configuration source from SystemProperties & configure file
 * 2. Runtime discovery sources
 * 
 * @author ho
 *
 */
@Singleton
public class ConfigManagerImpl implements ConfigManager {
	private static final String MAIN_CONFIG = "META-INF/javacloud.config.properties";
	
	private final ConfigSourceResolver sourceResolver;
	public ConfigManagerImpl() {
		//DEFAULT SOURCES FROM PROPERTIES
		this.sourceResolver = new ConfigSourceResolver(new DefaultConfigSource(MAIN_CONFIG, ResourceLoader.getClassLoader()))
			.overrideBy(new DefaultConfigSource(System.getProperties()));
		
		//DYNAMIC DISCOVER SOURCES
		for(ConfigSource source: ResourceLoader.loadServices(ConfigSource.class)) {
			this.sourceResolver.overrideBy(source);
		}
	}
	
	/**
	 * return property setting using NAME
	 */
	@Override
	public <T> T getProperty(String name, Class<T> type) {
		String svalue = sourceResolver.getProperty(name);
		if(svalue == null) {
			return null;
		}
		return Converters.toObject(svalue, type);
	}
	
	/**
	 * return safe config bundle
	 */
	@Override
	public <T> T getConfig(Class<T> type) {
		ProxyInvocationHandler configHandler = new ConfigInvocationHandlerImpl(sourceResolver);
		return ProxyInvocationHandler.newInstance(configHandler, type);
	}
}
