package com.appe.framework.data.registry;

import com.appe.framework.data.DataManager;
import com.appe.framework.data.impl.InMemoryDataManagerImpl;
import com.appe.framework.internal.GuiceModule;
/**
 * 
 * @author ho
 *
 */
public class InMemoryModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(DataManager.class).to(InMemoryDataManagerImpl.class);
	}
}
