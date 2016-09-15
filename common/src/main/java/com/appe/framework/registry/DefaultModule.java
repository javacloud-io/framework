package com.appe.framework.registry;

import com.appe.framework.AppeLocale;
import com.appe.framework.AppeNamespace;
import com.appe.framework.impl.AppeLocaleImpl;
import com.appe.framework.impl.AppeNamespaceImpl;
import com.appe.framework.internal.GuiceModule;
/**
 * This will bring in basic setup for common
 * 
 * @author ho
 *
 */
public class DefaultModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(AppeNamespace.class).to(AppeNamespaceImpl.class);
		bind(AppeLocale.class).to(AppeLocaleImpl.class);
	}
}
