package com.appe.framework.data;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.appe.framework.util.Objects;
import com.appe.framework.util.Pair;

/**
 * Data schema interface to be able to support mapping data from underline source. By default, using MAP or POJO mapping.
 * Custom mapping can be done by extends the MAPPER and override get/setColumn.
 * 
 * NOTES:
 * You can achieve 10x faster by using direct allocation & manually set the value!!!
 * 
 * @author tobi
 *
 * @param <T>
 */
public abstract class DataMapper<T> {
	protected Map<String, DataType> columns;	//simple columns type mapping
	/**
	 * Type and column definitions, it will do auto discover if columns is not SET.
	 * @param type
	 * @param columns
	 */
	protected DataMapper() {
	}
	
	/**
	 * return map of types if not yet initialize, make sure to be able to initialize only one time!
	 * @return
	 */
	public Map<String, DataType> getColumns() {
		return columns;
	}
	
	/**
	 * return new data model
	 * @return
	 */
	public abstract T newModel();
	
	/**
	 * set column value of model
	 * @param model
	 * @param column
	 * @param value
	 */
	public abstract void setColumn(T model, String column, Object value);
	
	/**
	 * return column value of model
	 * @param model
	 * @param column
	 * @return
	 */
	public abstract Object getColumn(T model, String column);
	
	
	/**
	 * SIMPLE MAP IMPLEMENTATION
	 * @param <T>
	 */
	public static class MAP<T extends Map<String, Object>> extends DataMapper<T> {
		private Class<T> type;
		public MAP(Class<T> type, Object...columns) {
			this.type = type;
			this.columns = Objects.asMap(columns);
		}
		/**
		 * Make new instance of using reflection
		 * @return
		 */
		public T newModel() {
			try {
				return (T) type.newInstance();
			} catch(InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		@Override
		public void setColumn(T model, String column, Object value) {
			model.put(column, value);
		}

		@Override
		public Object getColumn(T model, String column) {
			return model.get(column);
		}
	}
	
	/**
	 * SIMPLE POJO IMPLEMENTATION (kind of slower than direct MAP).
	 * @param <T>
	 */
	public static class POJO<T> extends DataMapper<T> {
		private Class<T> type;
		private Map<String, Pair<Method, Method>> descriptors;
		public POJO(Class<T> type, Object...columns) {
			this.type = type;
			
			//AUTO DISCOVER READ/WRITE PROPERTIES IF NONE SPECIFIED
			if(columns != null && columns.length > 1) {
				this.columns = Objects.asMap(columns);
				introspectColumns(false);
			} else {
				this.columns = Objects.asMap();
				this.descriptors = Objects.asMap();
				introspectColumns(true);
			}
		}
		
		/**
		 * Make new instance of using reflection
		 * @return
		 */
		public T newModel() {
			try {
				return (T) type.newInstance();
			} catch(InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		//TODO: SHOULD THROW EXCEPTION IF NOT FOUND?
		@Override
		public void setColumn(T model, String column, Object value) {
			try {
				Pair<Method, Method> descr = descriptors.get(column);
				if(descr != null) {
					descr.getKey().invoke(model, value);
				}
			} catch(IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		//TODO: SHOULD THROW EXCEPTION IF NOT FOUND?
		@Override
		public Object getColumn(T model, String column) {
			try {
				Pair<Method, Method> descr = descriptors.get(column);
				return	(descr == null? null : descr.getValue().invoke(model));
			} catch(IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException ex) {
				throw DataException.wrap(ex);
			}
		}
		
		/**
		 * Inspect more columns from the class & cache getter/setter method.
		 * Subclass override to exclude unwanted field.
		 */
		protected void introspectColumns(boolean discover) {
			try {
				PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
				for(PropertyDescriptor descr: descriptors) {
					//FILTER OUT NON QUALIFY NAME, HAVE TO BE BOTH READ/WRITE
					if(descr instanceof IndexedPropertyDescriptor
						|| descr.getReadMethod() == null || descr.getWriteMethod() == null) {
						continue;
					}
					
					//FIND THE CORRECT TYPE MAPPING
					if(discover) {
						DataType dt = DataType.get(descr.getPropertyType());
						
						//ADD DATA TYPE IF FOUND
						if(dt != null) {
							this.columns.put(descr.getName(), dt);
						}
					}
					
					//ALWAYS CACHE GET/SET METHOD
					this.descriptors.put(descr.getName(),
								new Pair<Method, Method>(descr.getWriteMethod(), descr.getReadMethod()));
				}
			} catch (IntrospectionException | SecurityException ex) {
				throw DataException.wrap(ex);
			}
		}
	}
}
