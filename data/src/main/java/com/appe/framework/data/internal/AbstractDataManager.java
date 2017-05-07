package com.appe.framework.data.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.data.DataManager;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;

/**
 * 
 * @author tobi
 *
 */
public abstract class AbstractDataManager implements DataManager {
	private static final Logger logger = LoggerFactory.getLogger(AbstractDataManager.class);
	private ConcurrentMap<String, DataStore<?>> dataStores = new ConcurrentHashMap<String, DataStore<?>>();
	
	/**
	 * BY DEFAULT WILL BE DEFAULT AS IT IS...
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> DataStore<T> bindStore(DataSchema<T> schema) {
		if(!dataStores.containsKey(schema.getTable())) {
			logger.debug("Create data store: {} =>{}", schema.getTable(), schema.getColumns());
			
			//THEN ADD TO CACHE.
			dataStores.putIfAbsent(schema.getTable(), createStore(schema));
		}
		return (DataStore<T>)dataStores.get(schema.getTable());
	}
	
	/**
	 * Table name are the same. No change at all.
	 */
	@Override
	public void dropTable(String tableName) {
		dataStores.remove(tableName);
	}
	
	/**
	 * Create a new data store if caching version if not exist.
	 * 
	 * @param schema
	 * @return
	 */
	protected abstract <T> DataStore<T> createStore(DataSchema<T> schema);
}
