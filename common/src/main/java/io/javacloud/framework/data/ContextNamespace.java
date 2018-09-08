package io.javacloud.framework.data;

/**
 * The NAMESPACE in which each APPE is running, to help isolation & SANDBOX.
 * 
 * @author tobi
 */
public interface ContextNamespace {
	/**
	 * Set new namespace to current context
	 * 
	 * @param namespace
	 */
	public void set(String namespace);
	
	/**
	 * return current NAMESPACE
	 * 
	 * @return
	 */
	public String get();
	
	/**
	 * return hash of the keys with namespace
	 * @param keys
	 * @return
	 */
	public String hash(Object... keys);
	
	/**
	 * Clean out any NAMESPACE currently set
	 * 
	 */
	public void clear();
}
