package com.appe.framework.resource.impl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.AppeException;
import com.appe.framework.AppeLoader;
import com.appe.framework.AppeLocale;
import com.appe.framework.resource.ConfigBundle;
import com.appe.framework.resource.MessageBundle;
import com.appe.framework.resource.ResourceManager;
import com.appe.framework.resource.internal.ConfigBundleHandler;
import com.appe.framework.resource.internal.I18nResourceBundlesControl;
import com.appe.framework.resource.internal.MessageBundleHandler;
import com.appe.framework.util.Objects;
/**
 * TODO: Need to make the I18n handle to be able to load all combine resources if need.
 * 
 * @author ho
 *
 */
@Singleton
public class ResourceManagerImpl implements ResourceManager {
	private static final Logger logger = LoggerFactory.getLogger(ResourceManagerImpl.class);
	
	private static final String CONF_EXTENSION 	= ".properties";
	private static final String CONF_RESOURCES 	= "conf/";
	private static final String I18N_RESOURCES 	= "i18n/";
	
	private final I18nResourceBundlesControl CONTROL;
	private final ConcurrentMap<Class<?>, Object> configCache = new ConcurrentHashMap<Class<?>, Object>();
	private ClassLoader classLoader;
	private AppeLocale appeLocale;
	
	/**
	 * Assign the default LOADER
	 */
	@Inject
	public ResourceManagerImpl(AppeLocale appeLocale) {
		this.appeLocale = appeLocale;
		CONTROL = new I18nResourceBundlesControl(appeLocale);
	}
	
	/**
	 * Switch class loader will lead to re-scan the bundle
	 */
	protected boolean initResourceLoader() {
		if(this.classLoader != null) {
			logger.warn("Resource loader already initialized with classLoader: {}", classLoader);
			return false;
		}
		
		//CACHE CURRENT CLASS LOADER
		try {
			this.classLoader = AppeLoader.getClassLoader();
			CONTROL.scanBundles(classLoader, true);
			
			logger.debug("Found I18n resource bundles: {}", CONTROL.getBundleNames());
			return true;
		} catch(IOException ex) {
			throw new AppeException("Unable to scan I18N bundles", ex);
		}
	}
	
	/**
	 * return current resource loader NULL if not initialize YET.
	 */
	@Override
	public ClassLoader getResourceLoader() {
		return classLoader;
	}

	/**
	 * Always lookup from cache
	 * 
	 */
	@Override
	public <T extends ConfigBundle> T getConfigBundle(Class<T> type) {
		return getResourceBundle(type);
	}
	
	/**
	 * Always lookup from a cache
	 * 
	 */
	@Override
	public <T extends MessageBundle> T getMessageBundle(Class<T> type) {
		return getResourceBundle(type);
	}
	
	/**
	 * Load and cache the bundle, it's OK if somehow has multiple load. ONLY ONE WILL MAKE TO CACHE!
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getResourceBundle(Class<T> type) {
		Object config = configCache.get(type);
		if(config == null) {
			synchronized(configCache) {
				//INITIALIZE THE LOADER IF NOT YET DONE SO
				if(classLoader == null) {
					initResourceLoader();
				}
				
				//FOUND FROM CACHE
				config = configCache.get(type);
				if(config == null) {
					config = loadResourceBundle(type);
					configCache.put(type, config);
				}
			}
		}
		return (T)config;
	}
	
	/**
	 * Always using the annotation to identity the resource to be loaded. In theory we assuming just load them UP ONE.
	 * So we don't even need to cache them at all.
	 * 
	 * @param type
	 * 
	 * @return
	 */
	protected Object loadResourceBundle(Class<?> type) {
		ConfigBundle.Resource resource = type.getAnnotation(ConfigBundle.Resource.class);
		String baseName = (resource != null? resource.value(): null);
		
		InvocationHandler configHandler;
		if(Objects.isEmpty(baseName)) {
			if(MessageBundle.class.isAssignableFrom(type)) {
				configHandler = createSystemI18nHandler(type);
			} else {
				configHandler = createSystemHandler(type);
			}
		} else {
			if(MessageBundle.class.isAssignableFrom(type)) {
				configHandler = createI18nHandler(baseName, type);
			} else {
				configHandler = createConfigHandler(baseName, type);
			}
		}
		return Proxy.newProxyInstance(classLoader, new Class<?>[]{ type }, configHandler);
	}
	
	/**
	 * Using the system property as source to resolve value.
	 * 
	 * @param config
	 * @return
	 */
	protected InvocationHandler createSystemHandler(Class<?> config) {
		logger.info("Binding config bundle: {} to system properties.", config.getName());
		return	new ConfigBundleHandlerImpl();
	}
	
	/**
	 * Always append the .properties to load the configuration.
	 * 
	 * @param baseName
	 * @param config
	 * @return
	 */
	protected InvocationHandler createConfigHandler(String baseName, Class<?> type) {
		//ALWAYS APPEND .properties to load the resource
		final String resource = CONF_RESOURCES + baseName + CONF_EXTENSION;
		logger.info("Binding config bundle: {} to resource: {}", type.getName(), resource);
		try {
			Properties properties = AppeLoader.loadProperties(resource, classLoader);
			if(Objects.isEmpty(properties)) {
				return new ConfigBundleHandler();
			}
			
			//USING HANDLER AND ALWAYS FALL BACK TO SYSTEM?
			return	new ConfigBundleHandlerImpl(properties);
		}catch(IOException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * TODO: create system universal I18n handler
	 * 
	 * @param type
	 * @return
	 */
	protected InvocationHandler createSystemI18nHandler(Class<?> type) {
		logger.info("Binding I18n universal bundle: {}", type.getName());
		return new MessageBundleHandler(appeLocale) {
			@Override
			protected ResourceBundle resolveBundle() throws MissingResourceException {
				//USING UNIFY BUNDLE
				return	ResourceBundle.getBundle("", appeLocale.get(), classLoader, CONTROL);
			}
		};
	}
	
	/**
	 * Since JDK already cache the bundle so we just pass on and lookup as NEED. We need a better way to be able to using
	 * bundle local at RUNTIME UI? NEED LOCAL THREAD TO PASSING DOWN..SINCE Locale.getDefault() is SYSTEM LEVEL.
	 * 
	 * @param baseName
	 * @param type
	 * @return
	 */
	protected InvocationHandler createI18nHandler(final String baseName, Class<?> type) {
		final String resource = I18N_RESOURCES + baseName;
		logger.info("Binding I18n bundle: {} to resource: {}", type.getName(), resource);
		
		//TO BE CONSISTENT, FIRST CALLER WIN!!!
		return	new MessageBundleHandler(appeLocale) {
					@Override
					protected ResourceBundle resolveBundle() throws MissingResourceException {
						return	ResourceBundle.getBundle(resource, appeLocale.get(), classLoader, CONTROL);
					}
				};
	}
}
