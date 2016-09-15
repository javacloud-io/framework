package com.appe.framework.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstraction of a very basic key/value store. Each data store should always have 2:
 * 
 * 1. Store key/value using hash key only.
 * 2. Index using key/value using both hash/range key.
 * 
 * @author tobi
 * 
 * @param <T>
 */
public interface DataStore<T> {
	/**
	 * return current schema definition of store
	 * @return
	 */
	public DataSchema<T> schema();
	
	/**
	 * Will do the creation only if the model not existing yet. This will present the need of doing get() & put().
	 * 
	 * @param model
	 * @param columns
	 * @return
	 */
	public boolean create(T model, String... columns);
	
	/**
	 * return data for key with optional columns.
	 * 
	 * @param key
	 * @param columns
	 * @return
	 */
	public T get(DataKey key, String...columns);
	
	/**
	 * Insert of update the model, ONLY WORK FOR NOT NULL VALUE. Why not support storing NULL?
	 * It's very dangerous if working out.
	 * 
	 * @param model
	 * @param columns
	 */
	public void put(T model, String... columns);
	
	/**
	 * DO WE NEED API to remove some of the columns value? instead of whole?
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(DataKey key);
	
	/**
	 * Fetch of all the items with keys specified
	 * 
	 * @param keys
	 * @param columns
	 * @return
	 */
	public List<T> fetch(Collection<DataKey> keys, String...columns);
	
	/**
	 * Quick query out using HASH/RANGE without any extra filter, USING DYNAMO STANDARD ONLY:
	 * EQ | LE | LT | GE | GT | BEGINS_WITH | BETWEEN is supported
	 * 
	 * @param hashKey
	 * @param condition
	 * @param range
	 * @param columns
	 * @return
	 */
	public DataResult<T> query(Object hashKey, DataCondition condition, DataRange range, String...columns);
	
	/**
	 * Query using hash + range key condition. NULL condition will imply all RANGKEYs. The conditions should contain RANGE
	 * condition to scan over hash KEY.
	 * 
	 * @param hashKey
	 * @param conditions
	 * @param range
	 * @param columns
	 * 
	 * @return
	 */
	public DataResult<T> query(Object hashKey, Map<String, DataCondition> conditions, DataRange range, String...columns);
	
	/**
	 * Scan the whole table for anything match conditions, if conditions not provided or empty everything will be sending back.
	 * With DYNAMODB: EQ | NE | LE | LT | GE | GT | NOT_NULL | NULL | CONTAINS | NOT_CONTAINS | BEGINS_WITH | IN | BETWEEN
	 * 
	 * @param conditions
	 * @param range
	 * @param columns
	 * 
	 * @return
	 */
	public DataResult<T> scan(Map<String, DataCondition> conditions, DataRange range, String...columns);
}
