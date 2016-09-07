package com.appe.framework.activity;

import java.util.Date;

import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Identifiable;

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
	public static enum Type {
		SUCCESS,
		FAILED,
		
		INFO,
		WARNING,
		ERROR,
		
		ALERT
	}
	private	String 	name;			//name of the event using lower case(register.user, apns.push...)
	private Type	type;			//type of event
	private String 	message;		//quick message description if ANY
	
	private	Dictionary details;		//details context event related if any (APPE, USER, ORDER...).
	private Date   	timestamp;		//when did it happen
	public Event() {
	}
	public Event(String name, Type type, String message) {
		this.name = name;
		this.type = type;
		this.message = message;
		this.timestamp= new java.util.Date();
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
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
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
	 */
	@Override
	public String toString() {
		return "{" + name + ": " + name + ", type: " + type + ", message: " + message
				   + ", details: " + details + ", timestamp: " + timestamp + "}";
	}
}
