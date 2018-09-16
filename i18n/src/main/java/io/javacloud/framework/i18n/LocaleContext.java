package io.javacloud.framework.i18n;

import java.util.Locale;

/**
 * Be able to capture the current locale
 * 
 * @author ho
 *
 */
public interface LocaleContext {
	/**
	 * Set list of prefer locale in order of important
	 * 
	 * @param locales
	 */
	public void set(Locale... locales);
	
	/**
	 * return the first locale
	 * 
	 * @return
	 */
	public Locale get();
	
	/**
	 * return the next one from the current or NULL if nothing else
	 * 
	 * @param local
	 * @return
	 */
	public Locale next(Locale locale);
	
	/**
	 * 
	 */
	public void clear();
}
