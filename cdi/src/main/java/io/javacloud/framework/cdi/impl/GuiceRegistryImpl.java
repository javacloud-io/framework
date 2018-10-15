package io.javacloud.framework.cdi.impl;

import java.util.List;

import io.javacloud.framework.cdi.internal.GuiceBuilder;
import io.javacloud.framework.cdi.internal.GuiceRegistry;
import io.javacloud.framework.util.ResourceLoader;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Basic implementation using google juice and service override at runtime level. By loading main cdi services at
 * META-INF/javacloud.cdi.services
 * 
 * @author ho
 *
 */
public class GuiceRegistryImpl extends GuiceRegistry {
	static final String CDI_SERVICES = ResourceLoader.META_INF + "javacloud.cdi.services";
	/**
	 * Injector only create at first time construction using current class loader.
	 * Make sure AppeRegistry just load at the correct time!!!
	 */
	public GuiceRegistryImpl() {
	}
	
	/**
	 * Make sure to insert the default module
	 */
	@Override
	protected Injector createInjector() {
		return new GuiceBuilder.StageBuilder(Stage.PRODUCTION) {
			@Override
			public Injector build(List<Module> modules, List<Module> overrides) {
				modules.add(0, new GuiceModuleImpl());
				return super.build(modules, overrides);
			}
		}.build(CDI_SERVICES , ResourceLoader.getClassLoader());
	}
}
