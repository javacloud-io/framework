package com.appe.framework.impl;

import java.util.Locale;

import javax.inject.Singleton;

import com.appe.framework.AppeLocale;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class AppeLocaleImpl implements AppeLocale {
	private static final ThreadLocal<Locale> LOCAL = new ThreadLocal<Locale>();
	public AppeLocaleImpl() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void set(Locale locale) {
		LOCAL.set(locale);
	}
	
	/**
	 * 
	 */
	@Override
	public Locale get() {
		Locale locale = LOCAL.get();
		return (locale == null? Locale.getDefault() : locale);
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		LOCAL.remove();
	}
}
