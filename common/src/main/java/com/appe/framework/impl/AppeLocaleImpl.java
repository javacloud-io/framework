package com.appe.framework.impl;

import java.util.Locale;

import javax.inject.Singleton;

import com.appe.framework.AppeLocale;
import com.appe.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class AppeLocaleImpl implements AppeLocale {
	private static final ThreadLocal<Locale[]> LOCALES = new ThreadLocal<Locale[]>();
	public AppeLocaleImpl() {
		
	}
	
	/**
	 * Set all to LOCAL CONTEXT
	 */
	@Override
	public void set(Locale... locales) {
		LOCALES.set(locales);
	}
	
	/**
	 * return the current or default ONE
	 */
	@Override
	public Locale get() {
		Locale[] locales = LOCALES.get();
		return (Objects.isEmpty(locales)? Locale.getDefault() : locales[0]);
	}
	
	/**
	 * return the next locale
	 */
	@Override
	public Locale next(Locale locale) {
		Locale[] locales = LOCALES.get();
		if(Objects.isEmpty(locales) || locale == null) {
			return null;
		}
		//return previous one if next match
		for(int i = 1; i < locales.length; i ++) {
			if(locales[i - 1].equals(locale)) {
				return locales[i];
			}
		}
		return null;
	}

	/**
	 * remove all the locale
	 */
	@Override
	public void clear() {
		LOCALES.remove();
	}
}
