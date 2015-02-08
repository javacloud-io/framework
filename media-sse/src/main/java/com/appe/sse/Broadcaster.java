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
 * To be able to broadcast message to all channels under the management. There will be 2 kind of channels:
 * 1. Direct channel where message is directly send out
 * 2. Relay channel to be able to relay message to number of broadcasters
 * 
 * @author ho
 *
 */
public interface Broadcaster {
	/**
	 * Publish a message to all channel, represent by this broadcaster return number of channel which event actually sendout
	 * or -1 if unknown.
	 * 
	 * @param channel
	 * @param message
	 * @return
	 */
	public void publish(String channel, Object message);
	
	/**
	 * Subscribe to a channel with given name
	 * 
	 * @param name
	 * @param type of object will received, null to use default DICTIONARY
	 * 
	 * @return
	 */
	public Channel subscribe(String name, Class<?> type);
	
	/**
	 * Unsubscribe/close channel if it doesn't make sense any more
	 * 
	 * @param channel
	 * @return
	 */
	public boolean unsubscribe(Channel channel);
}
