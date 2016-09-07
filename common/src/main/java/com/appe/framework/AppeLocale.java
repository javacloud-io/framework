package com.appe.framework;

import java.util.Locale;

/**
 * Be able to capture the current locale
 * 
 * @author ho
 *
 */
public interface AppeLocale {
	/**
	 * 
	 * @param locale
	 */
	public void set(Locale locale);
	/**
	 * 
	 * @return
	 */
	public Locale get();
	
	/**
	 * 
	 */
	public void clear();
}
