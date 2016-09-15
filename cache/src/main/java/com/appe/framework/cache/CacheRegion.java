package com.appe.framework.cache;

/**
 * Simple cache interface, object will be keep in cache for exactly expired seconds... FOR CACHE TO WORK CORRECTLY T
 * SHOULD BE SERIALIZABLE!!! MOST PREFERED WAY WILL BE JSONIZER.
 * 
 * @author ho
 *
 * @param <T>
 */
public interface CacheRegion<T> {
	//DEFAULT EXPIRY
	public static final int DEFAULT_EXPIRY		= 60 * 60;	//1 HOUR
	
	//SPECIAL FLAGS FOR OPTIMIZATION
	public static final int OPT_NONAMESPACE		= 0x00010000;	//no need NAMESPACE
	
	/**
	 * PUT ITEM TO CACHE
	 * 
	 * @param key
	 * @param value
	 */
	public void put(Object key, T value);
	
	/**
	 * SIMPLY PERFORM GET WITHOUT
	 * 
	 * @param key
	 * @return
	 */
	public T get(Object key);
	
	/**
	 * REMOVE given key if already exist... make sure it will be gone forever.
	 * 
	 * @param key
	 */
	public void remove(Object key);
}
