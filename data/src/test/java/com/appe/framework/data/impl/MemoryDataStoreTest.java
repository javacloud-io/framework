package com.appe.framework.data.impl;


/**
 * 
 * @author tobi
 *
 */
public class MemoryDataStoreTest extends DataStoreImplTest {
	@Override
	protected void setUp() throws Exception {
		dataManager = new MemoryDataManagerImpl();
	}
}
