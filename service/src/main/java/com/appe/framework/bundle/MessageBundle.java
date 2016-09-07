package com.appe.framework.bundle;

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
	public String getString(String key, Object... args);
}
