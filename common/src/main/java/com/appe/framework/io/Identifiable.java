package com.appe.framework.io;
/**
 * Something unique identify the OBJECT, mostly is automatically generated OBJECT.
 * 
 * @author ho
 *
 * @param <T>
 */
public abstract class Identifiable<T> {
	private T id;
	protected Identifiable() {
	}
	
	/**
	 * 
	 * @param id
	 */
	protected Identifiable(T id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 */
	public T getId() {
		return id;
	}
	public void setId(T id) {
		this.id = id;
	}
}
