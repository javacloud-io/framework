package com.appe.framework.data.internal;

import javax.inject.Inject;

import com.appe.framework.AppeNamespace;
import com.appe.framework.data.DataManager;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;

/**
 * Implementation of DataManager which respect the namespace. Idea is very simple:
 * Using new __uuid = sha1(namespae, hashKey) and persit hashKey to table.
 * All lookup using (hashKey, rangeKey) will be translated to (__hashKey, rangeKey).
 * 
 * @author tobi
 *
 */
public class NamespaceDataManager extends AbstractDataManager {
	private DataManager dataManager;
	private AppeNamespace namespace;
	/**
	 * 
	 * @param dataManager
	 * @param namespace
	 */
	@Inject
	public NamespaceDataManager(DataManager dataManager, AppeNamespace namespace) {
		this.dataManager = dataManager;
		this.namespace   = namespace;
	}

	/**
	 * Table name are the same. No change at all.
	 */
	@Override
	public void dropTable(String tableName) {
		dataManager.dropTable(tableName);
		super.dropTable(tableName);
	}
	
	/**
	 * Make new data store by using new HASHKEY
	 */
	@Override
	protected <T> DataStore<T> createStore(final DataSchema<T> schema) {
		//1. NEW MAPPER TO INCLUDE NAMESPACE
		NamespaceDataMapper<T> mapper = new NamespaceDataMapper<T>(schema, namespace);
		
		//2. NEW SCHEMA USING __HASHKEY
		DataSchema<T> __schema = new NamespaceDataSchema<>(schema, mapper);
		
		//WRAPPING AROUND BY NAMESPACE STORE
		return new NamespaceDataStore<T>(dataManager.bindStore(__schema), schema, mapper);
	}
}
