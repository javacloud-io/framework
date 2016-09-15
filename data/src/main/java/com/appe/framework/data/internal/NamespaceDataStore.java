package com.appe.framework.data.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;

/**
 * Implementation of DataManager which respect the namespace. Idea is very simple:
 * Using new __hashkey = hash(namespace, hashKey) and persist hashKey to table.
 * All lookup using (hashKey, rangeKey) will be translated to (__hashkey, rangeKey).
 * 
 * TODO:
 * -Scan operation is extremely expensive, b/c it scan data for all of the Namespaces,
 * Need better ways to implement this stuff correctly.
 * 
 * AS TODAY, we support MSSQL & Dynamodb. The analytics stuff should go to dynamo while the rest just keep in MSSQL.
 * If thing work out correctly, we can swap at anytime in the future.
 * 
 * @author tobi
 *
 */
public class NamespaceDataStore<T> extends AbstractDataStore<T> {
	protected DataStore<T> 	dataStore;	//ORIGINAL WITH NO NAMESPACE
	protected NamespaceDataMapper<T> 	mapper;
	
	/**
	 * Passing original data store & new schema. New schema will allows the CORRECT DATA CONSTRUCTION.
	 * @param dataStore
	 * @param schema
	 * @param mapper
	 */
	public NamespaceDataStore(DataStore<T> dataStore, DataSchema<T> schema, NamespaceDataMapper<T> 	mapper) {
		super(schema, 0);
		this.dataStore = dataStore;
		this.mapper = mapper;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean create(T model, String... columns) {
		return dataStore.create(model, columns);
	}

	/**
	 * 1. Translate the DATA KEY to using new ONE with 
	 */
	@Override
	public T get(DataKey key, String... columns) {
		T model = dataStore.get(mapper.toKey(key), mapper.toColumns(columns));
		return model;
	}
	
	/**
	 * Always override the __hashkey if any collision.
	 */
	@Override
	public void put(T model, String... columns) {
		dataStore.put(model, mapper.toColumns(columns));
	}
	
	/**
	 * Just remove the KEY.
	 */
	@Override
	public boolean remove(DataKey key) {
		return dataStore.remove(mapper.toKey(key));
	}
	
	/**
	 * 1. Translate all the KEY before making call.
	 * 2. Make sure to include the other columns if specified.
	 */
	@Override
	public List<T> fetch(Collection<DataKey> keys, String... columns) {
		//MAP OF NEW KEY BACK TO OLD KEY (ONLY HASH KEY CHANGE)
		List<DataKey> mkeys = new ArrayList<DataKey>(keys.size());
		for(DataKey key: keys) {
			mkeys.add(mapper.toKey(key));
		}
		
		//MAP OF NEW KEY => NEED TO MAP BACK TO OLD KEY.
		return	dataStore.fetch(mkeys, mapper.toColumns(columns));
	}
	
	/**
	 * Just issue query using new HASHKEY.
	 * Don't need to fix last evaluated key b/c it can be use to pass over NEXT TIME.
	 * 
	 */
	@Override
	public DataResult<T> query(Object hashKey, Map<String, DataCondition> conditions, DataRange range, String... columns) {
		return	dataStore.query(mapper.toHashKey(hashKey), conditions, range, mapper.toColumns(columns));
	}
	
	/**
	 * MIGHT HAVE PERFORMANCE ISSUE HERE WHEN SCAN LARGE TABLE OF ALL NAMESPACE, DynamoDB for example is a bad example
	 * of using this scan operation.
	 * 
	 */
	@Override
	public DataResult<T> scan(Map<String, DataCondition> conditions, DataRange range, String... columns) {
		Map<String, DataCondition> namespaceConditions = new LinkedHashMap<String, DataCondition>();
		
		//MERGE WITH THE EXISTINNG CONDITIONS
		if(conditions != null) {
			namespaceConditions.putAll(conditions);
		}
		
		//ALWAYS FILTER FOR CURRENT NAMESPACE IF NOT PROVIDE ONE (BACK DOOR FOR SCAN SOME NAMESPACE)
		String namespace = mapper.getNamespace();
		namespaceConditions.put(DataSchema.__NAMESPACE, namespace == null? DataCondition.NULL() : DataCondition.EQ(namespace));
		
		//START SCANING PROCESS
		return	dataStore.scan(namespaceConditions, range, mapper.toColumns(columns));
	}
}
