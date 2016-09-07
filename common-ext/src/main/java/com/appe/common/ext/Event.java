package com.appe.ext;

import java.util.Date;

import com.appe.util.Dictionary;

/**
 * By the end of day, event will be presented as a big ass JSON. In idea world:
 * 1. We would like to track events by related transaction
 * 2. It's great to know which event will cause others to happen.
 * 
 * By default event is sorted by timestamp (DESC), event also be index using level to be able to spot & notify the system.
 * @author tobi
 *
 */
public class Event extends Identifiable<String> {
	//BASIC KINDs
	public static enum Kind {
		INFO,
		SUCCESS,
		FAIL,
		ALERT,
		WARN,
		ERROR,
	}
	private	String 	name;			//name of the event using lower case(register.user, apns.push...)
	private Kind	kind;			//what kid is it (INFO, AUDIT SUCCESS/FAIL/ALERT, WARN, ERROR)
	private Date   	timestamp;		//when did it happen
	private String 	message;		//quick message description if ANY
	
	private	Dictionary details;		//details context event related if any (APPE, USER, ORDER...).
	public Event() {
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public Kind getKind() {
		return kind;
	}
	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	/**
	 * 
	 * @return
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 
	 * @return
	 */
	public Dictionary getDetails() {
		return details;
	}
	
	public void setDetails(Dictionary details) {
		this.details = details;
	}
	
	/**
	 * Set details VALUE associated with.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Event withDetails(String name, Object value) {
		if(details == null) {
			details = new Dictionary();
		}
		
		//SET DETAIL VALUE
		details.set(name, value);
		return this;
	}
}
