package com.appe.framework.resource;

/**
 * 
 * @author ho
 *
 */
public interface MessageBundle extends ConfigBundle {
	/**
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public String getMessage(String key, Object... args);
}
