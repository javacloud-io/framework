package javacloud.framework.cdi.impl;

import java.util.List;

import javacloud.framework.cdi.internal.GuiceBuilder;
import javacloud.framework.cdi.internal.GuiceRegistry;
import javacloud.framework.util.ResourceLoader;

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
	 * By default, registry is configure in Stage.PRODUCTION . This will take more load time but more realizable.
	 * In DEVELOPMENT or SERVERLESS environment, should use Stage.DEVELOPMENT.
	 * 
	 */
	@Override
	protected Injector createInjector() {
		return createInjector(Stage.PRODUCTION, ResourceLoader.getClassLoader());
	}
	
	/**
	 * Make sure to insert the default module
	 * 
	 * @param stage
	 * @param classLoader
	 * @return
	 */
	protected Injector createInjector(Stage stage, ClassLoader classLoader) {
		return new GuiceBuilder.StageBuilder(stage) {
			@Override
			public Injector build(List<Module> modules, List<Module> overrides) {
				modules.add(0, new GuiceModuleImpl());
				return super.build(modules, overrides);
			}
		}.build(CDI_SERVICES , classLoader);
	}
}
