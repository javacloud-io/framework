package io.javacloud.framework.cdi.impl;

import io.javacloud.framework.cdi.internal.GuiceBuilder;
import io.javacloud.framework.cdi.internal.GuiceRegistry;
import io.javacloud.framework.util.ResourceLoader;

import com.google.inject.Injector;
import com.google.inject.Stage;

/**
 * Basic implementation using google juice and service override at runtime level. By loading main cdi services at
 * META-INF/javacloud.cdi.services
 * 
 * @author ho
 *
 */
public class GuiceRegistryImpl extends GuiceRegistry {
	/**
	 * Injector only create at first time construction using current class loader.
	 * Make sure AppeRegistry just load at the correct time!!!
	 */
	public GuiceRegistryImpl() {
	}
	
	/**
	 * 
	 */
	@Override
	protected Injector createInjector() {
		return new GuiceBuilder.StageBuilder(Stage.PRODUCTION)
						.build(ResourceLoader.META_INF + "javacloud.cdi.services" , ResourceLoader.getClassLoader());
	}
}
