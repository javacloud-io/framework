package com.appe.framework.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataManager;
import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;
import com.appe.framework.util.Identifiable;
import com.appe.framework.util.Objects;

/**
 * It's not really a full indexes, but supper easy to build custom one, just extends TAG and add any custom DATA.
 * This same structure can be use to manage counter and other similar use case. We might face hashKEY hot issue but for that
 * note shouldn't be a big DEAL. TAG THE KEY AS WELL.
 * 
 * PROBLEM WITH THIS STUFF IS CONSISTENTCY, IF IT NOT WORKS. OR TOO MUCH READ/WRITE ON THE SERVER...
 * @author ho
 *
 */
public final class DataTagger<T extends DataTagger.Tag> {
	//KEY -> ID MAPPING WITH CUSTOM DATA
	public static class Tag extends Identifiable<String> {
		private String key;
		public Tag() {
		}
		public Tag(String key, String id) {
			super(id);
			this.key = key;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
	}
	
	//MAKE DATA STORE
	private DataStore<T> dataStore;
	
	/**
	 * 
	 * @param dataManager
	 * @param type
	 * @param table
	 */
	public DataTagger(DataManager dataManager, Class<T> type, String table) {
		this(dataManager, new DataMapper.POJO<>(type), table, 0);
	}
	
	/**
	 * 
	 * @param dataManager
	 * @param mapper
	 * @param table
	 * @param options
	 */
	public DataTagger(DataManager dataManager, DataMapper<T> mapper, String table, int options) {
		this.dataStore = dataManager.bindStore(new DataSchema<T>(table, "key", "id", mapper), options);
	}
	
	/**
	 * Add ANY TAGS USING LABEL & ID
	 * 
	 * @param id
	 * @param keys
	 */
	public DataTagger<T> add(String id, String key) {
		DataSchema<T> schema = dataStore.schema();
		dataStore.put(schema.createModel(new DataKey(key, id)));
		return this;
	}
	
	/**
	 * Add an id to numbers of tags
	 * 
	 * @param id
	 * @param keys
	 * @return
	 */
	public DataTagger<T> add(String id, Collection<String> keys) {
		DataSchema<T> schema = dataStore.schema();
		for(String key: keys) {
			dataStore.put(schema.createModel(new DataKey(key, id)));
		}
		return this;
	}
	
	/**
	 * Add tag to the index list, tag can include extra fields.
	 * 
	 * @param tag
	 */
	public DataTagger<T> add(T tag) {
		dataStore.put(tag);
		return this;
	}
	
	/**
	 * Assuming everything in sync which is WRONG => MAKE SURE STUFF IS UPDATED. THERE IS A LOT OF ISSUE HERE
	 * SO USE AT THR RIGHT CONTEXT.
	 * 
	 * @param id
	 * @param okey
	 * @param key
	 */
	public boolean update(String id, String okey, String key) {
		boolean success = false;
		if(Objects.compare(okey, key) != 0) {
			if(!Objects.isEmpty(okey)) {
				dataStore.remove(new DataKey(key, id));
				success = true;
			}
			if(!Objects.isEmpty(key)) {
				add(id, key);
				success = true;
			}
		}
		return success;
	}
	
	/**
	 * Update 2 set of indexes, make sure remove old and replace new if ANY.
	 * 
	 * @param id
	 * @param okeys
	 * @param keys
	 * @return
	 */
	public boolean update(String id, Set<String> okeys, Set<String> keys) {
		//0. FIND INTERESTION IF ANY
		Set<String> interkeys = new HashSet<String>();
		if(okeys != null) {
			interkeys.addAll(okeys);
		}
		interkeys.retainAll(keys);
		
		//1. REMOVE OLD ONEs
		boolean success = false;
		if(okeys != null) {
			for(String key: okeys) {
				if(!interkeys.contains(key)) {
					dataStore.remove(new DataKey(key, id));
					success = true;
				}
			}
		}
		
		//2. ADD NEW ONEs
		for(String key: keys) {
			if(!interkeys.contains(key)) {
				add(id, key);
				success = true;
			}
		}
		
		//FLAG
		return success;
	}
	
	/**
	 * Remove an and of associated KEY
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	public boolean remove(String id, String key) {
		return	dataStore.remove(new DataKey(key, id));
	}
	
	/**
	 * remove all keys with same id
	 * 
	 * @param id
	 * @param keys
	 */
	public DataTagger<T> remove(String id, Collection<String> keys) {
		for(String key: keys) {
			dataStore.remove(new DataKey(key, id));
		}
		return this;
	}
	
	/**
	 * Query all the TAGs of a given name. PAGING IF NEED TO...
	 * 
	 * @param key
	 * @param id
	 * @param range
	 * @return
	 */
	public DataResult<T> query(String key, DataCondition id, DataRange range) {
		return dataStore.query(key, id, range);
	}
	
	/**
	 * return TAG of the id, key
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	public T get(String id, String key) {
		return dataStore.get(new DataKey(key, id));
	}
	
	/**
	 * Query everything for a given label.
	 * 
	 * @param key
	 * @param range
	 * @return
	 */
	public DataResult<T> query(String key, DataRange range) {
		return dataStore.query(key, DataCondition.NOP(), range);
	}
	
	/**
	 * Query all tag for given hash key
	 * 
	 * @param tags
	 * @return
	 */
	public static final List<DataKey> asKeys(List<? extends Tag> tags) {
		List<DataKey> result = new ArrayList<DataKey>(tags.size());
		for(Tag tag : tags) {
			result.add(new DataKey(tag.getId()));
		}
		return result;
	}
}
