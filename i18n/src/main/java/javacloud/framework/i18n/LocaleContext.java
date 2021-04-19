package javacloud.framework.i18n;

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
	void set(Locale... locales);
	
	/**
	 * for well-formed BCP 47 language tag separated by comma
	 * 
	 * @param tags
	 */
	void set(String tags);
	
	/**
	 * return the first locale
	 * 
	 * @return
	 */
	Locale get();
	
	/**
	 * return the next one from the current or NULL if nothing else
	 * 
	 * @param local
	 * @return
	 */
	Locale next(Locale locale);
	
	/**
	 * 
	 */
	void clear();
}
