package com.appe.framework.data;

import java.util.Map;

/**
 * Basic NoSQL data schema, which need a description of hash/range key names and some of simple data type.
 * USING FIXED COLUMN ONLY!!!
 * 
 * @author tobi
 * 
 * @param <T>
 */
public class DataSchema<T> {
	//SPECIAL FLAG PERFORMANCE & OPTIMIZATION
	public static final int OPT_FASTSCAN		= 0x00000001;	//interest in fast scan the data
	public static final int OPT_NONAMESPACE		= 0x00010000;	//no NAMESPACE apply
		
	//RESERVED COLUMN NAME
	public static final String __HASHKEY 	= "__hashkey";		//BACK UP HASHKEY
	public static final String __NAMESPACE	= "__namespace";	//HASH OF NAMESPACE	
		
	private	String table;			//table name
	private	String hashKey;			//hash key name
	private	String rangeKey;		//range key name
	private DataMapper<T> mapper;	//data mapper
	/**
	 * 
	 * @param table
	 * @param hashKey
	 * @param rangeKey
	 * @param mapper
	 */
	public DataSchema(String table, String hashKey, String rangeKey, DataMapper<T> mapper) {
		this.table 		= table;
		this.hashKey 	= hashKey;
		this.rangeKey 	= rangeKey;
		this.mapper		= mapper;
	}
	
	/**
	 * Passing with no range key at all.
	 * @param table
	 * @param hashKey
	 * @param mapper
	 */
	public DataSchema(String table, String hashKey, DataMapper<T> mapper) {
		this(table, hashKey, null, mapper);
	}
	
	/**
	 * 
	 * @param table
	 * @param hashKey
	 * @param rangeKey
	 * @param type
	 */
	public DataSchema(String table, String hashKey, String rangeKey, Class<T> type) {
		this(table, hashKey, rangeKey, new DataMapper.POJO<>(type));
	}
	
	/***
	 * 
	 * @param table
	 * @param hashKey
	 * @param type
	 */
	public DataSchema(String table, String hashKey, Class<T> type) {
		this(table, hashKey, new DataMapper.POJO<>(type));
	}
	
	/**
	 * return table name
	 * @return
	 */
	public String getTable() {
		return table;
	}
	
	/**
	 * return hashKey name
	 * @return
	 */
	public String getHashKey() {
		return hashKey;
	}
	
	/**
	 * return range key name if any
	 * @return
	 */
	public String getRangeKey() {
		return rangeKey;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasRangeKey() {
		return rangeKey != null;
	}
	
	/**
	 * Same table name will be consider as the same!!!
	 */
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof DataSchema)) {
			return false;
		}
		return table.equals(((DataSchema<?>)obj).table);
	}
	
	/**
	 * SAME TABLE => SAME HASH CODE
	 */
	@Override
	public int hashCode() {
		return table.hashCode();
	}
	
	/**
	 * FIXME: ALWAYS RETURN UNMODIFIED VERSION OF THE COLUMNS.
	 * @return
	 */
	public Map<String, DataType> getColumns() {
		return	mapper.getColumns();
	}
	
	/**
	 * Make data model with hashKey/rangeKey subclass can override for different model.
	 * @param key
	 * @return
	 */
	public T createModel(DataKey key) {
		T model = mapper.newModel();
		
		//INJECT KEYs IF ANY SPECIFIED
		if(key != null) {
			mapper.setColumn(model, this.hashKey, key.getHashKey());
			
			//ADD RANGE KEY IF SCHEMA HAS
			if(hasRangeKey()) {
				mapper.setColumn(model, this.rangeKey, key.getRangeKey());
			}
		}
		return model;
	}
	
	/**
	 * Create key from model, assuming using schema.
	 * @param model
	 * @return
	 */
	public DataKey createKey(T model) {
		DataKey key = new DataKey(mapper.getColumn(model, getHashKey()));
		if(hasRangeKey()) {
			Comparable<?> rangeKey = (Comparable<?>)mapper.getColumn(model, getRangeKey());
			key.setRangeKey(rangeKey);
		}
		return key;
	}
	
	/**
	 * Change column value of model
	 * @param model
	 * @param column
	 * @param value
	 */
	public void setColumn(T model, String column, Object value) {
		mapper.setColumn(model, column, value);
	}
	
	/**
	 * return the column value
	 * @param model
	 * @param column
	 * @return
	 */
	public Object getColumn(T model, String column) {
		return mapper.getColumn(model, column);
	}
}