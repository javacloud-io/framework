package com.appe.framework.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataType;
import com.appe.framework.data.internal.AbstractDataStore;
import com.appe.framework.data.util.DataProjection;
import com.appe.framework.data.util.DataResultList;
import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;
/**
 * Simple in memory implementation of DataStore. DataModel are hashed by HASH KEY and index by RANGE KEY.
 * 
 * TODO:
 * -Implement value copy for some of the complicate type such as ByteBuffer
 * -Faster index & search through range key as well as scan hash key
 * 
 * @author tobi
 *
 */
public class MemoryDataStoreImpl<T> extends AbstractDataStore<T> {
	//index key to data model
	private ConcurrentMap<DataKey, Dictionary> models = new ConcurrentHashMap<DataKey, Dictionary>();
	
	//Index hash key to range key set
	private ConcurrentMap<Object, NavigableSet<Object>>	indexes = new ConcurrentHashMap<Object, NavigableSet<Object>>();
	/**
	 * Simple passing the schema over.
	 * @param schema
	 */
	public MemoryDataStoreImpl(DataSchema<T> schema) {
		super(schema, 0);
	}
	
	/**
	 * ALWAYS DOING UPDATE IF ABSENT & BUILD INDEX.. AFTER THAT
	 */
	@Override
	public boolean create(T model, String... columns) {
		DataKey key = schema.createKey(model);
		
		//USE KEY TO LOOKUP, CREATE DEFAULT IF NOT FOUND
		Dictionary dict = new Dictionary(new ConcurrentHashMap<String, Object>());
		if(models.putIfAbsent(key, dict) != null) {
			return false;
		}
		
		//INDEX RANGE KEY FOR FAST LOOKUP
		if(key.hasRangeKey()) {
			Set<Object> ranges = indexes.get(key);
			if(ranges == null) {
				indexes.putIfAbsent(key.getHashKey(), new ConcurrentSkipListSet<Object>());
				ranges = indexes.get(key.getHashKey());
			}
			ranges.add(key.getRangeKey());
		}
		
		//ONLY FOCUS ON CORRECT COLUMNS
		DataProjection<T> projection = resolveColumns(columns);
		
		//MAKE SURE TO INCLUDE KEY COLUMNS FOR SCANNING
		for(Map.Entry<String, DataType> entry: projection.getIncludedKeys().entrySet()) {
			String column = entry.getKey();
			DataType type = entry.getValue();
			Object value = schema.getColumn(model, column);
			
			//COUNTER => JUST ADD THEM UP IF COUNTER
			if(type == DataType.COUNTER) {
				dict.put(column, ((Number)value).doubleValue());
			} else if(value != null) {//UPDATE
				dict.put(column, value);
			}
		}
		return true;
	}

	/**
	 * return model of specified columns.
	 */
	@Override
	public T get(DataKey key, String... columns) {
		return get(key, resolveColumns(columns));
	}
	
	/**
	 * NEW MODEL, always should have HASH KEY! WE DON'T GENERATE KEY AT ALL.
	 * TOOD: filter out columns specified.
	 */
	@Override
	public void put(T model, String... columns) {
		DataKey key = schema.createKey(model);
		
		//USE KEY TO LOOKUP, CREATE DEFAULT IF NOT FOUND
		Dictionary dict = models.get(key);
		if(dict == null) {
			models.putIfAbsent(key, new Dictionary(new ConcurrentHashMap<String, Object>()));
			dict = models.get(key);
			
			//INDEX RANGE KEY FOR FAST LOOKUP
			if(key.hasRangeKey()) {
				Set<Object> ranges = indexes.get(key);
				if(ranges == null) {
					indexes.putIfAbsent(key.getHashKey(), new ConcurrentSkipListSet<Object>());
					ranges = indexes.get(key.getHashKey());
				}
				ranges.add(key.getRangeKey());
			}
		}
		//ONLY FOCUS ON CORRECT COLUMNS
		DataProjection<T> projection = resolveColumns(columns);
		
		//MAKE SURE TO INCLUDE KEY COLUMNS FOR SCANNING
		for(Map.Entry<String, DataType> entry: projection.getIncludedKeys().entrySet()) {
			String column = entry.getKey();
			DataType type = entry.getValue();
			Object value = schema.getColumn(model, column);
			
			//COUNTER => JUST ADD THEM UP IF COUNTER
			if(type == DataType.COUNTER) {
				Double oval = dict.get(column);
				if(oval == null) {
					oval = new Double(0);
				}
				dict.put(column, oval + ((Number)value).doubleValue());
			} else if(value != null) {//UPDATE
				dict.put(column, value);
			} else if((value instanceof String) && ((String)value).isEmpty()) {
				//CONSISTENTLY FORCE EMPTY => NULL
				dict.remove(column);
			}
		}
	}
	
	/**
	 * Just remove entry from MAP and all indexes.
	 */
	@Override
	public boolean remove(DataKey key) {
		boolean exists = (models.remove(key) != null);
		
		//HAVE RANGE KEY => REMOVE THE INDEX AS WELL
		if(key.hasRangeKey()) {
			Set<Object> ranges = indexes.get(key.getHashKey());
			if(ranges != null) {
				ranges.remove(key.getRangeKey());
			}
		}
		return exists;
	}
	
	/**
	 * Walk through all keys and add model if exist.
	 */
	@Override
	public List<T> fetch(Collection<DataKey> keys, String...columns) {
		List<T> list = new ArrayList<T>(keys.size());
		
		DataProjection<T> projection = resolveColumns(columns);
		for(DataKey key: keys) {
			T model = get(key, projection);
			if(model != null) {
				list.add(model);
			}
		}
		return list;
	}
	
	/**
	 * More complicated operation, need to filter out the KEYs.
	 * 
	 * TODO: CAN OPTIMIZE FOR EQ, LT, GT... QUEUE
	 */
	@Override
	public DataResult<T> query(Object hashKey, Map<String, DataCondition> conditions, DataRange range, String... columns) {
		ArrayList<T> items = new ArrayList<T>();
		Collection<Object> rangeKeys = indexes.get(hashKey);
		
		//SCAN RANGE KEY FOR NOW
		int limit = (range != null? range.getLimit() : DataRange.DEFAULT_LIMIT);
		if(rangeKeys == null || rangeKeys.isEmpty() || limit <= 0) {
			return new DataResultList<T>(items, null);
		}
		
		//EXCLUSIVE START KEY CONDITION (IGNORE HASH COMPARE AT THE MOMENT)
		DataCondition exclusiveCondition;
		if(range != null && range.getExclusiveStartKey() != null) {
			DataKey key = range.getExclusiveStartKey();
			exclusiveCondition = range.isReversed()? DataCondition.LT(key) : DataCondition.GT(key);
		} else {
			exclusiveCondition = DataCondition.NOT_NULL();
		}
		
		//MAKE SURE TO REVERSE THE LIST
		if(range != null && range.isReversed()) {
			List<Object> reverseKeys = new ArrayList<Object>(rangeKeys);
			Collections.reverse(reverseKeys);
			rangeKeys = reverseKeys;
		}
				
		//FIXME: SCAN RANGE KEY FOR NOW
		DataProjection<T> projection = resolveColumns(columns);
		DataKey lastEvaluatedKey = null;
		for(Object r: rangeKeys) {
			
			//MAKE SURE KEY IS IN RANGE
			DataKey key = new DataKey(hashKey, (Comparable<?>)r);
			if(!exclusiveCondition.test(key)) {
				continue;
			}
			
			//JUST INCASE NEED TO EXIST
			Dictionary dict = models.get(key);
			if(dict == null || !testConditions(conditions, dict)) {
				continue;
			}
			
			//ADD IF STILL EXIST
			T model = get(key, projection);
			if(model != null) {
				items.add(model);
				
				//REACH THE LIMIT
				if(items.size() >= limit) {
					lastEvaluatedKey = key;
					break;
				}
			}
		}
		//TODO: SORT BY RANGE KEY
		return new DataResultList<T>(sort(items, (range != null && range.isReversed())), lastEvaluatedKey);
	}
	
	/**
	 * Scan the table with attributes matched.
	 */
	@Override
	public DataResult<T> scan(Map<String, DataCondition> conditions, DataRange range, String... columns) {
		ArrayList<T> items = new ArrayList<T>();
		
		//SCAN RANGE KEY FOR NOW
		int limit = (range != null? range.getLimit() : DataRange.DEFAULT_LIMIT);
		if(limit <= 0) {
			return new DataResultList<T>(items, null);
		}
				
		//EXCLUSIVE START KEY CONDITION (IGNORE HASH COMPARE AT THE MOMENT)
		DataCondition exclusiveCondition;
		if(range != null && range.getExclusiveStartKey() != null) {
			DataKey key = range.getExclusiveStartKey();
			exclusiveCondition = range.isReversed()? DataCondition.LT(key) : DataCondition.GT(key);
		} else {
			exclusiveCondition = DataCondition.NOT_NULL();
		}
		
		//MAKE SURE TO REVERSE THE LIST
		Collection<Map.Entry<DataKey, Dictionary>> entrySet = models.entrySet();
		if(range != null && range.isReversed()) {
			List<Map.Entry<DataKey, Dictionary>> reverseSet = new ArrayList<Map.Entry<DataKey, Dictionary>>(entrySet);
			Collections.reverse(reverseSet);
			entrySet = reverseSet;
		}
				
		//FIXME: SCAN THROUGH WHOLE THING
		DataProjection<T> projection = resolveColumns(columns);
		DataKey lastEvaluatedKey = null;
		for(Map.Entry<DataKey, Dictionary> entry: entrySet) {
			if(!exclusiveCondition.test(entry.getKey())) {
				continue;
			}
			
			//SCAN THROUGH ALL THE ATTRIBUTES
			if(!testConditions(conditions, entry.getValue())) {
				continue;
			}
			
			//ADD TO LIST
			T model = get(entry.getKey(), projection);
			if(model != null) {
				items.add(model);
				
				//REACH THE LIMIT
				if(items.size() >= limit) {
					lastEvaluatedKey = entry.getKey();
					break;
				}
			}
		}
		//SORT THE ITEMS BY KEYS
		return new DataResultList<T>(sort(items, (range != null && range.isReversed())), lastEvaluatedKey);
	}
	
	/**
	 * 
	 * @param conditions
	 * @param v
	 * @return
	 */
	protected boolean testConditions(Map<String, DataCondition> conditions, Dictionary dict) {
		//NOPE CONDITIONS
		if(conditions == null || conditions.isEmpty()) {
			return true;
		}
		
		//HAVE TO MATCH ALL
		for(Map.Entry<String, DataCondition> cond: conditions.entrySet()) {
			Object v = dict.get(cond.getKey());
			if(!cond.getValue().test(v)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ONLY RETURN COLUMNS FROM DICTIONARY, THE REST WILL BE REMOVED
	 * FIXME: MAKE SURE TO JUST RETURN COPY OF DATA INCASE OF NONE PREMITIVE
	 * 
	 * @param key
	 * @param projection
	 * @return
	 */
	protected T get(DataKey key, DataProjection<T> projection) {
		Dictionary dict = models.get(key);
		if(dict == null) {
			return null;
		}
		
		//CONVERT THE MODEL
		T model = schema.createModel(key);
		for(Map.Entry<String, DataType> entry: projection.getExcludedKeys().entrySet()) {
			String column = entry.getKey();
			schema.setColumn(model, column, dict.get(column));
		}
		return model;
	}
	
	/**
	 * Figure out to sort the ITEMs by range KEY.
	 * @param items
	 * @param reversed
	 * @return
	 */
	protected List<T> sort(List<T> items, final boolean reversed) {
		final String rangeKey = schema.getRangeKey();
		if(rangeKey == null) {
			return items;
		}
		//NOW SORT BY RANGE KEY
		Collections.sort(items, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				Object v1 = schema.getColumn(o1, rangeKey);
				Object v2 = schema.getColumn(o2, rangeKey);
				return reversed? Objects.compare(v2, v1) : Objects.compare(v1, v2);
			}
		});
		return items;
	}
}
