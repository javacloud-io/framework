package com.appe.framework.job;

import java.util.Set;

/**
 * Keep all execution related state in one place.
 * 
 * @author ho
 *
 */
public interface ExecutionState {
	/**
	 * Parameters to feed into JOB, T is simple type
	 */
	public interface Parameters {
		/**
		 * return READONLY keys
		 * 
		 * @return
		 */
		public Set<String> keys();
		
		/**
		 * 
		 * @param key
		 * @return
		 */
		public <T> T get(String key);
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		public boolean getBoolean(String key, boolean defaultValue);
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		public int getInteger(String key, int defaultValue);
		
		/**
		 * 
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		public String getString(String key, String defaultValue);
	}
	
	/**
	 * Attributes to persist state across RETRY
	 */
	public interface Attributes extends Parameters {
		/**
		 * 
		 * @param key
		 * @param value
		 */
		public <T> void set(String key, T value);
	}
	
	/**
	 * Status of execution
	 */
	public enum Status {
		SUCCESS,
		WARNING,
		FAIL,
		
		WAIT,
		RETRY;
		
		//return true if it's a completed status
		public static boolean isCompleted(Status status) {
			return (status == SUCCESS || status == WARNING || status == FAIL);
		}
	}
	
	/**
	 * JOB ID
	 * @return
	 */
	public String getId();
	/**
	 * return the current status
	 * 
	 * @return
	 */
	public Status getStatus();
	
	/**
	 * return parameters of the execution
	 * @return
	 */
	public Parameters getParameters();
	
	/**
	 * return attributes of the execution
	 * @return
	 */
	public Attributes getAttributes();
}
