package com.appe.framework.resource;

/**
 * All message bundle will locate under /i18n folder
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
