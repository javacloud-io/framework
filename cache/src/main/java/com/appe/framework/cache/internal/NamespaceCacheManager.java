package com.appe.framework.cache.internal;

import com.appe.framework.AppeNamespace;
import com.appe.framework.cache.CacheManager;
import com.appe.framework.cache.CacheRegion;
/**
 * Magically make the cache that aware of NAMESPACE to isolate them using hash(namespace,hashkey). This will guarantee
 * no collision what so ever!
 * 
 * @author ho
 *
 */
public class NamespaceCacheManager extends AbstractCacheManager {
	private CacheManager cacheManager;
	private AppeNamespace appeNamespace;
	
	/**
	 * 
	 * @param cacheManager
	 * @param appeNamespace
	 */
	public NamespaceCacheManager(CacheManager cacheManager, AppeNamespace appeNamespace) {
		this.cacheManager = cacheManager;
		this.appeNamespace= appeNamespace;
	}
	
	/**
	 * MAKE SPECIAL TREATMENT WHEN CREATE CACHE REGION FOR NAMESPACE.
	 */
	@Override
	protected <T> CacheRegion<T> createCache(String name, Class<T> type, int options) {
		final CacheRegion<T> cacheRegion = cacheManager.bindCache(name, type, options);
		if((options & CacheRegion.OPT_NONAMESPACE) != 0) {
			return cacheRegion;
		}
		
		//RETURN A WRAPPER WITH MODIFIED HASHKEY
		return new CacheRegion<T>() {
			@Override
			public void put(Object key, T value) {
				cacheRegion.put(namespaceKey(key), value);
			}
			
			@Override
			public T get(Object key) {
				return	cacheRegion.get(namespaceKey(key));
			}

			@Override
			public void remove(Object key) {
				cacheRegion.remove(namespaceKey(key));
			}
			
			/**
			 * MODIFY THE NAMESPACE KEY, HASH WILL NOT RETAIN AT ALL. SHOULD USING SAME HASH OF OTHERS.
			 * 
			 * @param key
			 * @return
			 */
			protected Object namespaceKey(Object key) {
				return appeNamespace.hash(key);
			}
		};
	}
}
