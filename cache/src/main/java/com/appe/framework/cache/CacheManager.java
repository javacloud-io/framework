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
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> CacheRegion<T> bindCache(String name, Class<T> type);
}
