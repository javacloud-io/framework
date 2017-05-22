package com.appe.framework.impl;

import com.appe.framework.internal.GuiceRegistry;

/**
 * Basic implementation using google juice and service override at runtime level. By default it will load a file:
 * META-INF/registry-modules.guice => then will perform overriding with XXX.1. It then can be perform a special override
 * using only a current class loading XXX.2.
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
}
