package com.appe.framework.data.impl;

import com.appe.framework.AppeNamespace;
import com.appe.framework.data.internal.NamespaceDataManager;
import com.appe.framework.impl.AppeNamespaceImpl;

/**
 * 
 * @author tobi
 *
 */
public class NamespaceDataStoreTest extends MemoryDataStoreTest {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		AppeNamespace namespace = new AppeNamespaceImpl();
		namespace.set("blah");
		this.dataManager = new NamespaceDataManager(dataManager, namespace);
	}
}
