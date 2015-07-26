package com.appe.registry.internal;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * To hide the GUICE binding dependency, extend to bind module.
 * 
 * @author ho
 *
 */
public abstract class GuiceModule extends AbstractModule {
	/**
	 * Bind an instance with a string NAME to be able to lookup
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	protected <T> LinkedBindingBuilder<T> bindNamed(Class<T> clazz, String name) {
	    return bind(clazz).annotatedWith(new AnnotatedName(name));
	}
}
