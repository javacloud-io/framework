package com.appe.framework.resource.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import com.appe.framework.resource.internal.ConfigBundleHandler;
import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class ConfigBundleHandlerImpl extends ConfigBundleHandler {
	private final Dictionary config;
	private final boolean useSystemProperties;
	/**
	 * 
	 */
	public ConfigBundleHandlerImpl() {
		this(System.getProperties(), false);
	}
	
	/**
	 * 
	 * @param properties
	 */
	public ConfigBundleHandlerImpl(Properties properties) {
		this(properties, true);
	}
	
	/**
	 * 
	 * @param properties
	 * @param useSystemProperties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConfigBundleHandlerImpl(Properties properties, boolean useSystemProperties) {
		this.config = new Dictionary((Map)Collections.unmodifiableMap(properties));
		this.useSystemProperties = useSystemProperties;
	}
	
	/**
	 * 
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//DYNAMIC values() for PASS THROUGH
		if(method.getReturnType().isAssignableFrom(Map.class)) {
			return config;
		}
		return super.invoke(proxy, method, args);
	}
	
	/**
	 * Resource bundle value is always overrides by system one if exist
	 */
	@Override
	protected String resolveValue(String key, String defaultValue) {
		String value;
		if(useSystemProperties) {
			value = System.getProperty(key);
			if(Objects.isEmpty(value)) {
				value = config.getString(key, defaultValue);
			}
		} else {
			value = config.getString(key, defaultValue);
		}
		return value;
	}
}
