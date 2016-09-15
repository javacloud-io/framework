package com.appe.framework.cache.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.cache.CacheManager;
import com.appe.framework.cache.CacheRegion;
/**
 * Any good implementation of the cache should able to translate the exception of Loader to caller. That is the must
 * in most cases!!!
 * 
 * @author ho
 *
 */
public abstract class AbstractCacheManager implements CacheManager {
	private static final Logger logger = LoggerFactory.getLogger(AbstractCacheManager.class);
	private ConcurrentMap<String, CacheRegion<?>> cacheRegions = new ConcurrentHashMap<String, CacheRegion<?>>();
	
	/**
	 * ALWAYS PASSING DEFAULT OPTIONS
	 */
	@Override
	public <T> CacheRegion<T> bindCache(String name, Class<T> type) {
		return bindCache(name, type, 0);
	}

	/**
	 * Make a modification version of schema
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> CacheRegion<T> bindCache(String name, Class<T> type, int options) {
		if(!cacheRegions.containsKey(name)) {
			logger.debug("Create cache region: {} =>{}", name, type);
			
			//THEN ADD TO CACHE.
			cacheRegions.putIfAbsent(name, createCache(name, type, options));
		}
		return (CacheRegion<T>)cacheRegions.get(name);
	}
	
	/**
	 * FIGURE OUT HOW TO CREATE A CACHE FOR GIVEN CONDITION.
	 * 
	 * @param name
	 * @param type
	 * @param options
	 * @return
	 */
	protected abstract <T> CacheRegion<T> createCache(String name, Class<T> type, int options);
}
