package com.appe.framework.internal;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * To hide the GUICE binding dependency, extend to bind module.
 * TODO:
 * 1. supports package scan with annotation
 * 2. bulk registration
 * 
 * @author ho
 *
 */
public abstract class GuiceModule extends AbstractModule {
	/**
	 * Bind class to an implementation
	 * 
	 * @param clazz
	 * @param clazzImpl
	 */
	protected <T> void bindTo(Class<T> clazz, Class<T> clazzImpl) {
	    bind(clazz).to(clazzImpl);
	}
	
	/**
	 * Bind a class using a NAME to an implementation
	 * 
	 * @param clazz
	 * @param name
	 * @param clazzImpl
	 */
	protected <T> void bindTo(Class<T> clazz, String name, Class<T> clazzImpl) {
	    bindToName(clazz, name).to(clazzImpl);
	}
	
	/**
	 * Bind an instance with a string NAME to be able to lookup
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	protected <T> LinkedBindingBuilder<T> bindToName(Class<T> clazz, String name) {
	    return bind(clazz).annotatedWith(new AnnotatedName(name));
	}
}
