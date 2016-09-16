package com.appe.framework.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;
import com.appe.framework.util.Objects;

/**
 * Utilities class to do simple data manipulation, not a lot of magic but good enough trick to make a lot of stuff simple.
 * 
 * @author tobi
 *
 */
public final class Data {
	private Data() {
	}
	
	/**
	 * Just revert the list of data model to MAP.
	 * @param collection
	 * @param schema
	 * @return
	 */
	public static <T> Map<DataKey, T> map(Collection<T> collection, DataSchema<T> schema) {
		Map<DataKey, T> items = new LinkedHashMap<DataKey, T>();
		for(T model: collection) {
			items.put(schema.createKey(model), model);
		}
		return items;
	}
	
	/**
	 * Filter the collection of data to match the condition
	 * 
	 * @param collection
	 * @param condition
	 * @return
	 */
	public static <T> List<T> filter(Collection<T> collection, DataCondition condition) {
		List<T> items = new ArrayList<T>();
		for(T v: collection) {
			if(condition == null || condition.test(v)) {
				items.add(v);
			}
		}
		return items;
	}
	
	/**
	 * Filter out data match all the conditions, help to quickly scan out elements in memory.
	 * 
	 * @param collection
	 * @param schema
	 * @param conditions
	 * @return
	 */
	public static <T> List<T> filter(Collection<T> collection, DataSchema<T> schema, Map<String, DataCondition> conditions) {
		List<T> items = new ArrayList<T>();
		for(T v: collection) {
			if(conditions == null || conditions.isEmpty()) {
				items.add(v);
				continue;
			}
			
			//ALL CONDITION
			boolean matches = true;
			for(Map.Entry<String, DataCondition> cond: conditions.entrySet()) {
				Object vv = schema.getColumn(v, cond.getKey());
				if(!cond.getValue().test(vv)) {
					matches = false;
					break;
				}
			}
			if(matches) {
				items.add(v);
			}
		}
		return items;
	}
	
	/**
	 * Query ALL the DATA, no PAGING NEED!!!
	 * 
	 * @param store
	 * @param hashKey
	 * @param conditions
	 * @param columns
	 * @return
	 */
	public static <T> List<T> query(DataStore<T> store, Object hashKey, Map<String, DataCondition> conditions, String...columns) {
		List<T> items = new ArrayList<T>(DataRange.DEFAULT_LIMIT);
		DataRange range = new DataRange();
		do {
			DataResult<T> result = store.query(hashKey, conditions, range, columns);
			items.addAll(result.items());
			
			//NEXT TOKEN IF ANY
			range.setExclusiveStartKey(result.lastEvaluatedKey());
		} while(range.getExclusiveStartKey() != null);
		return items;
	}
	
	/**
	 * Scan for all data until run out, just in case you would like to get all limited value.
	 * 
	 * @param store
	 * @param conditions
	 * @param columns
	 * @return
	 */
	public static <T> List<T> scan(DataStore<T> store, Map<String, DataCondition> conditions, String...columns) {
		List<T> items = new ArrayList<T>(DataRange.DEFAULT_LIMIT);
		DataRange range = new DataRange();
		do {
			DataResult<T> result = store.scan(conditions, range, columns);
			items.addAll(result.items());
			
			//NEXT TOKEN IF ANY
			range.setExclusiveStartKey(result.lastEvaluatedKey());
		} while(range.getExclusiveStartKey() != null);
		return items;
	}
	
	/**
	 * 
	 * @param model
	 * @param schema
	 * @param store
	 * @param columns
	 * @return
	 */
	public static <T> T join(T model, DataSchema<T> schema, DataStore<?> store, String... columns) {
		join(Objects.asList(model), schema, store, columns);
		return model;
	}
	
	/**
	 * It's very costly to do JOIN this ways but we eventually doing it some how. The join just work for HASH KEY ONLY.
	 * Why? because we can't represent the columns with both HAS/RANGE VALUE.
	 * 
	 * @param items
	 * @param stores
	 * @return
	 */
	public static <T> List<T> join(List<T> items, DataSchema<T> schema, Map<String, DataStore<?>> stores, String... columns) {
		//LOOP THROUGH AND DO THE SAME THING FOR ALL.
		for(Map.Entry<String, DataStore<?>> entry: stores.entrySet()) {
			join(items, schema, entry.getValue(), columns);
		}
		return items;
	}
	
	/**
	 * Performing a memory JOIN list of data using hashKey to other data store. Assuming JOIN will change DATA
	 * on the left side object => it's structure should be something like Dictionary to be able store any.
	 * 
	 * @param items
	 * @param schema
	 * @param store
	 * @param columns
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> join(List<T> items, DataSchema<T> schema, DataStore store, String... columns) {
		//1. COLLECT ALL KEYS
		String joinKey = store.schema().getHashKey();
		Set<DataKey> keys = new LinkedHashSet<DataKey>();
		for(T m: items) {
			Object hkey = schema.getColumn(m, joinKey);	//GET KEY VALUE
			if(hkey != null) {
				keys.add(new DataKey(hkey));
			}
		}
		
		//NOTHING TO DO IF NOT FOUND KEY.
		if(keys.isEmpty()) {
			return	items;
		}
		
		//2. QUERY ALL THE DATA & BUILD A MAP BACK	
		Map<DataKey, ?> details;
		if(store.schema().hasRangeKey()) {
			HashMap<DataKey, List<?>> mdetails = new HashMap<DataKey, List<?>>();
			Map<String, DataCondition> conditions = Objects.asMap(store.schema().getRangeKey(), DataCondition.NOP());
			//TODO: NEED TO OPTIMIZE TO USE LESS CAPACITY
			for(DataKey k: keys) {
				mdetails.put(k, query(store, k.getHashKey(), conditions, columns));
			}
			
			//USING THIS ONE
			details = mdetails;
		} else {
			//1-1 MAP
			details = map(store.fetch(keys, columns), store.schema());
		}
		
		//3. FIX UP THE DETAILS JOIN KEY
		for(T m: items) {
			Object hkey = schema.getColumn(m, joinKey);
			if(hkey != null) {
				Object value = details.get(new DataKey(hkey));
				schema.setColumn(m, joinKey, value);
			}
		}
		return items;
	}
	
	/**
	 * Assuming condition are string base, this will make sure everything down to LOWER CASE.
	 * @param condition
	 * @return
	 */
	public static DataCondition lower(DataCondition condition) {
		if(condition == null) {
			return condition;
		}
		
		//MAKE ALL VALUE LOWER
		Object[] values = condition.getValues();
		if(values != null) {
			for(int i = 0; i < values.length; i ++) {
				if(values[i] != null) {
					values[i] = ((String)values[i]).toLowerCase();
				}
			}
		}
		return condition;
	}
	
	/**
	 * return NAME/VALUE conditions
	 * 
	 * @param conditions
	 * @return
	 */
	public static Map<String, DataCondition> conditions(Object...conditions) {
		return Objects.asMap(conditions);
	}
	
	/**
	 * Scan for first match only
	 * 
	 * @param store
	 * @param name
	 */
	public static <T> T scanOne(DataStore<T> store, String name, DataCondition condition, String...columns) {
		DataResult<T> result = store.scan(conditions(name, condition), new DataRange(1), columns);
		if(result.count() > 0) {
			return result.items().get(0);
		}
		return null;
	}
}
