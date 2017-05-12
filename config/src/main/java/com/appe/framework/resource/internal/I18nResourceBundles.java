package com.appe.framework.resource.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * 
 * @author ho
 *
 */
public class I18nResourceBundles extends ResourceBundle {
	private Map<String, Object> messages;
	public I18nResourceBundles(Map<String, Object> messages) {
		this.messages = messages;
	}
	public I18nResourceBundles(Collection<ResourceBundle> bundles) {
		this.messages = new LinkedHashMap<>(8192);
		for(ResourceBundle bundle: bundles) {
			for(Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements(); ) {
				String key = keys.nextElement();
				messages.put(key, bundle.getObject(key));
			}
		}
	}
	
	@Override
	protected Object handleGetObject(String key) {
		return messages.get(key);
	}
	
	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(messages.keySet());
	}
}
