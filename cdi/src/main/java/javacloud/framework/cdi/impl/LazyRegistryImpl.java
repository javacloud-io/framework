package javacloud.framework.cdi.impl;

import javax.inject.Singleton;

import com.google.inject.Injector;
import com.google.inject.Stage;

import javacloud.framework.util.ResourceLoader;
/**
 * Fast startup times at the expense of runtime performance and some up front error checking.
 * Suitable for DEVELOPMENT or SERVERLESS environment to reduce boot code startup time.
 * 
 * @author ho
 *
 */
@Singleton
public class LazyRegistryImpl extends GuiceRegistryImpl {
	@Override
	protected Injector createInjector() {
		return createInjector(Stage.DEVELOPMENT, ResourceLoader.getClassLoader());
	}
}
