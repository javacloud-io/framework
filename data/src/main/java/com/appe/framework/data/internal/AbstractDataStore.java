package com.appe.framework.data.internal;

import java.util.Map;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;
import com.appe.framework.data.util.DataProjection;
import com.appe.framework.util.Objects;
/**
 * 
 * @author tobi
 *
 */
public abstract class AbstractDataStore<T> implements DataStore<T> {
	protected DataSchema<T> schema;
	protected int options;
	/**
	 * Simple passing the schema over.
	 * @param schema
	 * @param options
	 */
	protected AbstractDataStore(DataSchema<T> schema, int options) {
		this.schema = schema;
		this.options= options;
	}
	
	/**
	 * The schema which is currently use.
	 */
	@Override
	public final DataSchema<T> schema() {
		return schema;
	}
	
	/**
	 * result the columns, return a cache projects of all columns.
	 * @param columns
	 * @return
	 */
	protected DataProjection<T> resolveColumns(String... columns) {
		return new DataProjection<T>(schema, columns);
	}
	
	/**
	 * QUICKLY TRANSLATE TO MAP OF CONDITIONS IF NEED TOO.
	 */
	@Override
	public DataResult<T> query(Object hashKey, DataCondition condition, DataRange range, String... columns) {
		//SIMPLE WRAP THE CONDITION
		Map<String, DataCondition> conditions = Objects.asMap();
		if(condition != null) {
			conditions.put(schema.getRangeKey(), condition);
		}
		
		//DELEGATE TO RIGHT STUFF
		return query(hashKey, conditions, range, columns);
	}
	
}
