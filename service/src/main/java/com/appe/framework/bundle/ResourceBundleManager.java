package com.appe.framework.bundle;
/**
 * Manage the config and message bundle all together
 * 
 * @author ho
 *
 */
public interface ResourceBundleManager {
	/**
	 * return a config bundle
	 * 
	 * @param type
	 * @return
	 */
	public <T extends ConfigBundle> T getConfigBundle(Class<T> type);
	
	/**
	 * return i18n message bundle
	 * 
	 * @param type
	 * @return
	 */
	public <T extends MessageBundle> T getMessageBundle(Class<T> type);
}
