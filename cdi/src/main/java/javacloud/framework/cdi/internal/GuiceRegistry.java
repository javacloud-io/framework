package javacloud.framework.cdi.internal;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.util.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Basic implementation using google juice and service override at runtime level. By default it will load a file:
 * META-INF/registry-modules.guice => then will perform overriding with XXX.1. It then can be perform a special override
 * using only a current class loading XXX.2.
 * 
 * @author ho
 *
 */
public abstract class GuiceRegistry extends ServiceRegistry {
	private final Injector injector;
	/**
	 * Injector only create at first time construction using current class loader.
	 * Make sure AppeRegistry just load at the correct time!!!
	 */
	public GuiceRegistry() {
		this.injector = createInjector();
	}
	
	/**
	 * return an instance of any service interface
	 * 
	 */
	@Override
	public final <T> T getInstance(Class<T> type) {
		return injector.getInstance(type);
	}
	
	/**
	 * The name always need to bind using Guice @Named
	 */
	@Override
	public <T> T getInstance(Class<T> type, String name) {
		if(Objects.isEmpty(name)) {
			return getInstance(type);
		}
		return injector.getInstance(Key.get(type, Names.named(name)));
	}
	
	/**
	 * Find all instances of the service type.
	 */
	@Override
	public <T> List<T> getInstances(Class<T> type, String...names) {
		if(names == null || names.length == 0) {
			List<Binding<T>> bindings = injector.findBindingsByType(TypeLiteral.get(type));
			if(bindings == null || bindings.isEmpty()) {
				return Collections.emptyList();
			}
			List<T> instances = new ArrayList<>(bindings.size());
			for(Binding<T> b: bindings) {
				instances.add(b.getProvider().get());
			}
			return instances;
		} else {
			List<T> instances = new ArrayList<>();
			for(String name: names) {
				instances.add(getInstance(type, name));
			}
			return instances;
		}
	}
	
	/**
	 * Return new on fly object with dependency injection.
	 * FIXME: return instance not managed by GUICE !!!
	 */
	@Override
	public <T> T getUnmanagedInstance(Class<T> type) {
		return getInstance(type);
	}

	/**
	 * return internal injector of guice registry
	 * @return
	 */
	public static final Injector getInjector() {
		return ((GuiceRegistry)ServiceRegistry.get()).injector;
	}
	
	/**
	 * By default we always looking for service with default profile unless it's override at runtime.
	 * 
	 * @return
	 */
	protected abstract Injector createInjector();
}
