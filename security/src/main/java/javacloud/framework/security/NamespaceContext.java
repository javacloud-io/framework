package javacloud.framework.security;

/**
 * The NAMESPACE in which each APPE is running, to help isolation & SANDBOX.
 * 
 * @author tobi
 */
public interface NamespaceContext {
	/**
	 * Set new namespace to current context
	 * 
	 * @param namespace
	 */
	void set(String namespace);
	
	/**
	 * 
	 * @return current NAMESPACE
	 */
	String get();
	
	/**
	 * 
	 * @param keys
	 * @return hash of the keys with namespace
	 */ 
	String hash(Object... keys);
	
	/**
	 * Clean out any NAMESPACE currently set
	 * 
	 */
	void clear();
}
