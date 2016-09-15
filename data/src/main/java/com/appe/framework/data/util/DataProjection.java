package com.appe.framework.data.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataType;

/**
 * Make the columns filter more efficient => This will cache & lazy resolve the columns.
 * TODO: NEED TO OPTIMIZE TO AVOID WASTED MEMORY.
 * 
 * @author tobi
 * @param <T>
 */
public class DataProjection<T> {
	private DataSchema<T> schema;
	private String[] columns;
	
	//Cache of the columns with specific need.
	private Map<String, DataType> includedKeys;	
	private Map<String, DataType> excludedKeys;
	/**
	 * 
	 * @param schema
	 * @param columns
	 */
	public DataProjection(DataSchema<T> schema, String...columns) {
		this.schema = schema;
		this.columns= columns;
	}
	
	/**
	 * return the schema if need ONE.
	 * @return
	 */
	public DataSchema<T> schema() {
		return schema;
	}
	
	/**
	 * Doesn't matter that much if include keys or not. ALWAYS FAVOR TO ALL KEYS.
	 * @return
	 */
	public Map<String, DataType> getColumns() {
		if(includedKeys != null) {
			return includedKeys;
		} else if(excludedKeys != null) {
			return excludedKeys;
		}
		
		//FAVOR TO INCLUDED KEYs (LIKELY ALL OF THE COLUMNS)
		return getIncludedKeys();
	}
	
	/**
	 * Make sure to not clone the columns if not need to. The idea here is to NOT!
	 * @return
	 */
	public Map<String, DataType> getIncludedKeys() {
		if(includedKeys == null) {
			if(columns == null || columns.length == 0) {
				includedKeys = schema.getColumns();
			} else {
				if(excludedKeys != null) {
					includedKeys = new LinkedHashMap<String, DataType>(excludedKeys);
				} else {
					includedKeys = resolveColumns();
				}
				
				//INCLUDE THE HASH/RANG KEY
				includedKeys.put(schema.getHashKey(), schema.getColumns().get(schema.getHashKey()));
				if(schema.hasRangeKey()) {
					includedKeys.put(schema.getRangeKey(), schema.getColumns().get(schema.getRangeKey()));
				}
			}
		}
		return includedKeys;
	}
	
	/**
	 * return the columns without KEYs!
	 * @return
	 */
	public Map<String, DataType> getExcludedKeys() {
		if(excludedKeys == null) {
			//FASTER TO CLONE FROM INCLUDED KEYS?
			if(includedKeys != null) {
				excludedKeys = new LinkedHashMap<String, DataType>(includedKeys);
			} else {
				excludedKeys = resolveColumns();
			}
				
			//INCLUDE THE HASH/RANG KEY
			excludedKeys.remove(schema.getHashKey());
			if(schema.hasRangeKey()) {
				excludedKeys.remove(schema.getRangeKey());
			}
		}
		return excludedKeys;
	}
	
	/**
	 * ONLY INCLUDE THE VALID COLUMNS & CLONE!!!
	 * @return
	 */
	private Map<String, DataType> resolveColumns() {
		Map<String, DataType> keys;
		if(columns == null || columns.length == 0) {
			keys = new LinkedHashMap<String, DataType>(schema.getColumns());
		} else {
			keys = new LinkedHashMap<String, DataType>(schema.getColumns().size());
			for(String column: columns) {
				DataType type = schema.getColumns().get(column);
				if(type != null) {
					keys.put(column, type);
				}
			}
		}
		return keys;
	}
}
