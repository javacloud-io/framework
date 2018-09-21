package io.javacloud.framework.i18n.impl;

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.i18n.LocaleContext;
import io.javacloud.framework.util.Objects;

import java.util.Locale;

import javax.inject.Singleton;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class LocaleContextImpl implements LocaleContext {
	private static final ThreadLocal<Locale[]> LOCALES = new ThreadLocal<Locale[]>();
	public LocaleContextImpl() {
		
	}
	
	/**
	 * Set all to LOCAL CONTEXT
	 */
	@Override
	public void set(Locale... locales) {
		LOCALES.set(locales);
	}
	
	/**
	 * Support list of language tags such as: en-US_xxx,
	 */
	@Override
	public void set(String tags) {
		Locale[] locales = null;
		if(!Objects.isEmpty(tags)) {
			String[] ltags = Converters.toArray(tags, ",", true);
			locales = new Locale[ltags.length];
			for(int i = 0; i < ltags.length; i ++) {
				locales[i] = Locale.forLanguageTag(ltags[i]);
			}
		}
		set(locales);
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
		if(locale == null || Objects.isEmpty(locales)) {
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
