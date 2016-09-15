package com.appe.framework.cache;

/**
 * Simple cache manager interface to be able to create & destroy cache itself. AGAIN, THE IDEA IS CACHING FOR SOME
 * LOCAL VERY INTENSE OPERATION. FOR MOST OF USECASE, WE ASSUMING UNDER STORAGE LAYER ARE SUPPER FAST TO ENCORAGE
 * CLEAN CODE.
 * 
 * @author ho
 *
 */
public interface CacheManager {
	/**
	 * Bind with default options
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> CacheRegion<T> bindCache(String name, Class<T> type);
	
	/**
	 * Create the cache with same name if exist. TYPE will help the serialize purpose if cache go remote.
	 * 
	 * @param name
	 * @param type
	 * @param options
	 * @return
	 */
	public <T> CacheRegion<T> bindCache(String name, Class<T> type, int options);
}
