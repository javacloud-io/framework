package com.appe.framework.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.cache.CacheManager;
import com.appe.framework.cache.CacheRegion;

/**
 * It will add a little overhead to the usage... because of the calculation of cache....
 * BY DEFAULT DOESN'T DO ANYTHING.
 * 
 * @author ho
 *
 */
public class NoCacheManagerImpl implements CacheManager {
	private static final Logger logger = LoggerFactory.getLogger(NoCacheManagerImpl.class);
	
	//ZOMBI NO CACHE & NO WHAT SO EVER
	private static CacheRegion<Object> NOCACHE = new CacheRegion<Object> () {
		@Override
		public void put(Object key, Object value) {
		}
		@Override
		public Object get(Object key) {
			return null;
		}
		@Override
		public void remove(Object key) {
		}
	};
	public NoCacheManagerImpl() {
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> CacheRegion<T> bindCache(String name, Class<T> type) {
		logger.debug("Create nocache region: {} -> {}", name, type);
		return (CacheRegion<T>)NOCACHE;
	}
}
