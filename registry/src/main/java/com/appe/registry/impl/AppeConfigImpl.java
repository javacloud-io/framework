/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.registry.impl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.inject.Singleton;

import com.appe.registry.AppeConfig;
import com.appe.registry.AppeLoader;

/**
 * Simple implementation of configuration using resource property. Resource property can be overload at runtime by
 * include the system file to classpath at loading time.
 * 
 * @author ho
 *
 */
@Singleton
public class AppeConfigImpl implements AppeConfig {
	private static final Logger logger = Logger.getLogger(AppeConfigImpl.class.getName());
	private ConcurrentMap<Class<?>, Object> configCache = new ConcurrentHashMap<Class<?>, Object>();
	/**
	 * 
	 */
	public AppeConfigImpl() {
	}
	
	/**
	 * It's OK to load multiple of them and throw away. We just to make sure always keep the LAST ONE!
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> config) {
		Object cfg = configCache.get(config);
		if(cfg == null) {
			cfg = loadConfig(config);
			configCache.put(config, cfg);
		}
		return (T)cfg;
	}
	
	/**
	 * Always using the annotation to identity the resource to be loaded. In theory we assuming just load them UP ONE.
	 * So we don't even need to cache them at all.
	 * 
	 * @param config
	 * @return
	 */
	protected Object loadConfig(Class<?> config) {
		AppeConfig.Bundle bundle = config.getAnnotation(AppeConfig.Bundle.class);
		String baseName;
		
		//RESOLVE BUNDLE BASE NAME
		if(bundle == null) {
			baseName = config.getName();
		} else {
			baseName = bundle.name();
		}
		
		//EMPTY BUNDLE => USING SYSTEM HANDLER
		InvocationHandler configHandler;
		if(baseName == null || baseName.isEmpty()) {
			configHandler = createSystemHandler(config);
		} else {
			if(bundle != null && bundle.i18n()) {
				configHandler = createI18nHandler(baseName, config);
			} else {
				configHandler = createPropertiesHandler(baseName, config);
			}
		}
		return Proxy.newProxyInstance(AppeLoader.getClassLoader(), new Class<?>[]{ config }, configHandler);
	}
	
	/**
	 * 
	 * @return
	 */
	protected Locale getThreadLocale() {
		return Locale.getDefault();
	}
	
	/**
	 * Using the system property as source to resolve value.
	 * 
	 * @param config
	 * @return
	 */
	protected InvocationHandler createSystemHandler(Class<?> config) {
		logger.info("Bind config class: " + config.getName() + " to system properties.");
		return new ConfigHandlerImpl() {
					@Override
					protected String resolveValue(String name, String defaultValue) {
						return System.getProperty(name, defaultValue);
					}
				};
	}
	
	/**
	 * Always append the .properties to load the configuration.
	 * 
	 * @param baseName
	 * @param config
	 * @return
	 */
	protected InvocationHandler createPropertiesHandler(String baseName, Class<?> config) {
		//ALWAYS APPEND .properties to load the resource
		String resource = baseName + ".properties";
		logger.info("Bind the config class: " + config.getName() + " to resource: " + resource);
		
		try {
			final Properties properties = AppeLoader.loadProperties(resource, false);
			if(properties == null || properties.isEmpty()) {
				return new ConfigHandlerImpl();
			}
			
			//USING HANDLER AND ALWAYS FALL BACK TO SYSTEM?
			return	new ConfigHandlerImpl() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							//ALWAYS RETURN ALL PROPERTIES
							if(method.getReturnType().isAssignableFrom(Properties.class)) {
								return properties;
							}
							return super.invoke(proxy, method, args);
						}
						
						@Override
						protected String resolveValue(String name, String defaultValue) {
							String value = System.getProperty(name);
							if(value == null) {
								value = properties.getProperty(name, defaultValue);
							}
							return value;
						}
					};
		}catch(IOException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	/**
	 * Since JDK already cache the bundle so we just pass on and lookup as NEED. We need a better way to be able to using
	 * bundle local at RUNTIME UI? NEED LOCAL THREAD TO PASSING DOWN..SINCE Locale.getDefault() is SYSTEM LEVEL.
	 * 
	 * @param resource
	 * @param config
	 * @return
	 */
	protected InvocationHandler createI18nHandler(final String baseName, Class<?> config) {
		logger.info("Bind I18n config class: " + config.getName() + " to resource: " + baseName);
		
		//TODO: Dynamic loading bundle to support user locale
		final ResourceBundle bundle = ResourceBundle.getBundle(baseName, Locale.getDefault(), AppeLoader.getClassLoader());
		return	new ConfigHandlerImpl() {
					@Override
					protected String resolveValue(String name, String defaultValue) {
						try {
							return	bundle.getString(name);
						}catch(MissingResourceException ex) {
							logger.warning("I18n bundle key: " + name + " not found, details: " + ex.getMessage());
						}
						return (defaultValue == null || defaultValue.isEmpty()? name: defaultValue);
					}
				};
	}
}
