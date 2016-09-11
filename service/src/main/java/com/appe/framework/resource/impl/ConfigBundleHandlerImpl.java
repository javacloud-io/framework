package com.appe.framework.resource.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import com.appe.framework.util.Dictionary;

/**
 * 
 * @author ho
 *
 */
public class ConfigBundleHandlerImpl extends ConfigBundleHandler {
	private final Dictionary config;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	/**
	 * 
	 * @param properties
	 */
	public ConfigBundleHandlerImpl(Properties properties) {
		this.config = new Dictionary((Map)Collections.unmodifiableMap(properties));
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//DYNAMIC values() for PASS THROUGH
		if(method.getReturnType().isAssignableFrom(Map.class)) {
			return config;
		}
		return super.invoke(proxy, method, args);
	}
	
	/**
	 * 
	 */
	@Override
	protected String resolveValue(String key, String defaultValue) {
		String value = System.getProperty(key);
		if(value == null) {
			value = config.getString(key, defaultValue);
		}
		return value;
	}
}
