package com.appe.framework.data.internal;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appe.framework.data.DataException;
import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataType;
import com.appe.framework.io.BytesInputStream;
import com.appe.framework.io.BytesOutputStream;
import com.appe.framework.util.Codecs;
import com.appe.framework.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * For complex object type, trying to map all the complex field to JSON using CUSTOM TYPE. ANY CUSTOM FIELD WILL BE
 * STORE AS BYTEB THEN IT WILL AUTOMATICALLY HANDLE SERIALIZE & DESERIALIZE.
 * 
 * @author ho
 * @param <T>
 */
public abstract class ObjectDataMapper<T> extends DataMapper<T> {
	private DataMapper<T> mapper;
	private Map<String, Class<?>> types;
	/**
	 * 
	 * @param mapper
	 * @param type
	 * @param types
	 */
	public ObjectDataMapper(DataMapper<T> mapper, DataType type, Object...types) {
		this.mapper = mapper;
		this.types  = Objects.asMap(types);
		
		//CONSOLIDATE TYPES TO BYTE
		this.columns= new LinkedHashMap<String, DataType>(mapper.getColumns());
		for(String column: this.types.keySet()) {
			this.columns.put(column, type);
		}
	}
	
	/**
	 * ALWAYS USING THE SAME OBJECT MODEL
	 */
	@Override
	public T newModel() {
		return mapper.newModel();
	}
	
	/**
	 * BEFORE SET TO MODEL => CONVERT FROM BYTE TO OBJECT
	 */
	@Override
	public void setColumn(T model, String column, Object value) {
		Class<?> type = types.get(column);
		if(type == null || value == null) {
			mapper.setColumn(model, column, value);
			return;
		}
		
		//BYTES -> OBJECT
		mapper.setColumn(model, column, unwrapValue(value, type));
	}
	
	/**
	 * BEFORE PERSISTENCE => CONVERT FROM OBJECT TO BYTEB.
	 */
	@Override
	public Object getColumn(T model, String column) {
		Class<?> type = types.get(column);
		if(type == null) {
			return mapper.getColumn(model, column);
		}
		
		//OK, NEED TO JSONIZE BEFORE RETURN
		Object value = mapper.getColumn(model, column);
		if(value == null) {
			return null;
		}
		
		//CONVERT TO BYTES
		return wrapValue(value, type);
	}
	
	/**
	 * UNWRAP THE VALUE BEFORE GIVE BACK
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	protected abstract Object unwrapValue(Object value, Class<?> type);
	
	/**
	 * WRAP VALUE BEFORE SAVING TO DB
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	protected abstract Object wrapValue(Object value, Class<?> type);
	
	//CUSTOM OBJECT AS BYTEB
	public static class BYTEB<T> extends ObjectDataMapper<T> {
		private ObjectMapper objectMapper;
		public BYTEB(DataMapper<T> mapper, ObjectMapper objectMapper, Object... types) {
			super(mapper, DataType.BYTEB, types);
			this.objectMapper = objectMapper;
		}
		
		/**
		 * UNWRAP THE VALUE BEFORE GIVE BACK
		 * 
		 * @param value
		 * @param type
		 * @return
		 */
		@Override
		protected Object unwrapValue(Object value, Class<?> type) {
			//BYTES -> OBJECT
			try {
				BytesInputStream src = new BytesInputStream((byte[])value);
				return	objectMapper.readValue(src, type);
			}catch(IOException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		/**
		 * WRAP VALUE BEFORE SAVING TO DB
		 * 
		 * @param value
		 * @param type
		 * @return
		 */
		@Override
		protected Object wrapValue(Object value, Class<?> type) {
			//CONVERT TO BYTES
			try {
				BytesOutputStream dst = new BytesOutputStream();
				objectMapper.writeValue(dst, value);
				return dst.toByteArray();
			} catch(IOException ex) {
				throw DataException.wrap(ex);
			}
		}
	}
	
	//UTF8 ALL CUSTOME FIELD
	public static class UTF8<T> extends ObjectDataMapper<T> {
		private ObjectMapper objectMapper;
		public UTF8(DataMapper<T> mapper, ObjectMapper objectMapper, Object... types) {
			super(mapper, DataType.UTF8, types);
			this.objectMapper = objectMapper;
		}
		
		/**
		 * UNWRAP THE VALUE BEFORE GIVE BACK
		 * 
		 * @param value
		 * @param type
		 * @return
		 */
		@Override
		protected Object unwrapValue(Object value, Class<?> type) {
			//UTF8 -> OBJECT
			try {
				return	objectMapper.readValue((String)value, type);
			}catch(IOException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		/**
		 * WRAP VALUE BEFORE SAVING TO DB
		 * 
		 * @param value
		 * @param type
		 * @return
		 */
		@Override
		protected Object wrapValue(Object value, Class<?> type) {
			//CONVERT TO BYTES
			try {
				BytesOutputStream dst = new BytesOutputStream();
				objectMapper.writeValue(dst, value);
				return dst.toString(Codecs.UTF8);
			} catch(IOException ex) {
				throw DataException.wrap(ex);
			}
		}
	}
}
