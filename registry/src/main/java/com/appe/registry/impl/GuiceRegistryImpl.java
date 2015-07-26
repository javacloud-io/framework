/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.registry.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.appe.registry.AppeRegistry;
import com.appe.registry.internal.AnnotatedName;
import com.appe.registry.internal.GuiceBuilder;
import com.appe.registry.internal.GuiceFactory;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;

/**
 * Basic implementation using google juice and service override at runtime level. By default it will load a file:
 * META-INF/registry-modules.guice => then will perform overriding with XXX.1. It then can be perform a special override
 * using only a current class loading XXX.2.
 * 
 * @author ho
 *
 */
public class GuiceRegistryImpl extends AppeRegistry {
	private final Injector injector;
	/**
	 * 
	 */
	public GuiceRegistryImpl() {
		injector = createInjector();
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
		return injector.getInstance(Key.get(type, new AnnotatedName(name)));
	}
	
	/**
	 * Find all instances of the service type.
	 */
	@Override
	public <T> List<T> getInstances(Class<T> type) {
		List<Binding<T>> bindings = injector.findBindingsByType(TypeLiteral.get(type));
		if(bindings == null || bindings.isEmpty()) {
			return Collections.emptyList();
		}
		List<T> instances = new ArrayList<>(bindings.size());
		for(Binding<T> b: bindings) {
			instances.add(b.getProvider().get());
		}
		return instances;
	}

	/**
	 * For internal use only, can be call using reflection. Might be use for integration purpose..
	 * 
	 * @return
	 */
	public final Injector getInjector() {
		return injector;
	}
	
	/**
	 * By default we always looking for service with default profile unless it's override at runtime.
	 * 
	 * @return
	 */
	protected Injector createInjector() {
		String profile = System.getProperty(PROFILE, "guice");
		String resource= "META-INF/registry-modules." + profile;
		
		return GuiceFactory.createInjector(new GuiceBuilder.StageBuilder(Stage.PRODUCTION), resource);
	}
}
