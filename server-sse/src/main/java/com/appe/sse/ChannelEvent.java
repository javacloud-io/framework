/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.sse;
/**
 * 
 * @author ho
 *
 */
public class ChannelEvent {
	public enum Type {
		SUBSCRIBED,
		
		SENT,
		RECEIVED,
		
		ERROR,
		CLOSED
	}
	
	private Type type;
	private Object message;
	/**
	 * 
	 * @param type
	 */
	public ChannelEvent(Type type) {
		this(type, null);
	}
	
	/**
	 * Event with message attached
	 * 
	 * @param type
	 * @param message
	 */
	public ChannelEvent(Type type, Object message) {
		this.type = type;
		this.message = message;
	}
	
	/**
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMessage() {
		return (T)message;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return String.valueOf(type);
	}
}
