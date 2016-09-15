package com.appe.framework.data.internal;

import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataSchema;
/**
 * Wrapping around the OLD SCHEMA TO PROVIDE EXTRA STUFF.
 * 
 * @author ho
 *
 * @param <T>
 */
public class NamespaceDataSchema<T> extends DataSchema<T> {
	private String _hashKey;
	/**
	 * We retain the same range KEY so most of the operation should be the SAME WITH HASH KEY ALONE.
	 * @param schema
	 * @param mapper
	 */
	public NamespaceDataSchema(DataSchema<T> schema, DataMapper<T> mapper) {
		super(schema.getTable(), DataSchema.__HASHKEY, schema.getRangeKey(), mapper);
		this._hashKey = schema.getHashKey();
	}
	
	/**
	 * 
	 * @return
	 */
	public String get_HashKey() {
		return _hashKey;
	}
}
