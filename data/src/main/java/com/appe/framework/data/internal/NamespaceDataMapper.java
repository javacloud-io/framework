package com.appe.framework.data.internal;

import java.util.LinkedHashMap;
import java.util.Set;

import com.appe.framework.AppeNamespace;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataType;
import com.appe.framework.util.Objects;
/**
 * Range key is still reserved for any kind of QUERY, mean while hashKey is hashed with the NAMESPACE.
 * Scanning the table become extremely expensive for namespace, since it's need to scan through all the namespaces.
 * Obviously it's not good for a large collections set within a namespace.
 * 
 * Good thing about this is: NOT CREATE MORE HOT KEYs IF APPLICATION ALREADY TAKE CARE OF IT.
 * @author tobi
 *
 * @param <T>
 */
public class NamespaceDataMapper<T> extends DataMapper<T> {
	private DataSchema<T> schema;
	private AppeNamespace namespace;
	/**
	 * 
	 * @param schema
	 * @param namespace
	 */
	public NamespaceDataMapper(DataSchema<T> schema, AppeNamespace 	namespace) {
		this.columns = new LinkedHashMap<String, DataType>(schema.getColumns());
		
		//ADD ADDITIONAL COLUMNS
		this.columns.put(DataSchema.__HASHKEY, 	DataType.UTF8);	//NEW HASH KEY AS KEY
		this.columns.put(DataSchema.__NAMESPACE,DataType.UTF8);	//ORIGINAL NAMESPACE
		
		this.schema = schema;
		this.namespace = namespace;
	}
	
	/**
	 * return new MODEL.
	 */
	@Override
	public T newModel() {
		return schema.createModel(null);
	}
	
	/**
	 * NO NEED TO SET VALUE FOR __NAMESPACE COLUMN
	 */
	@Override
	public void setColumn(T model, String column, Object value) {
		if(DataSchema.__HASHKEY.equals(column)) {
			//NEW HASH KEY ?
		} else if(DataSchema.__NAMESPACE.equals(column)) {
			//NAMESPACE ?
		} else {
			schema.setColumn(model, column, value);
		}
	}
	
	/**
	 * Make sure to return correct version of the MODEL.
	 * 
	 * @param model
	 * @param column
	 * @return
	 */
	@Override
	public Object getColumn(T model, String column) {
		if(DataSchema.__HASHKEY.equals(column)) {
			return	toHashKey(schema.getColumn(model, schema.getHashKey()));
		} else if(DataSchema.__NAMESPACE.equals(column)) {
			return	getNamespace();
		}
		return schema.getColumn(model, column);
	}
	
	/**
	 * return NAMESPACE HASH instead of NAMESPACE itself!
	 * @return
	 */
	public String getNamespace() {
		return namespace.get();
	}
	
	/**
	 * return hash KEY of the original hash key.
	 * @param hashKey
	 * @return
	 */
	public String toHashKey(Object hashKey) {
		return namespace.hash(hashKey);
	}
	
	/**
	 * return namespace DATA KEY.
	 * @param key
	 * @return
	 */
	public DataKey toKey(DataKey key) {
		return new DataKey(toHashKey(key.getHashKey()), key.getRangeKey());
	}
	
	/**
	 * Make sure to always include the OLD hashKEY columns for re-construction!!!
	 */
	public String[] toColumns(String[] columns) {
		//OK, ALL COLUMNS?
		if(columns == null || columns.length == 0) {
			return columns;
		}
		
		//MAKE SURE TO INCLUDE OLD HASH KEY COLUMN & NAMESPACE
		Set<String> kcolumns = Objects.asSet(columns);
		kcolumns.add(schema.getHashKey());
		kcolumns.add(DataSchema.__NAMESPACE);
		return kcolumns.toArray(new String[kcolumns.size()]);
	}
}
