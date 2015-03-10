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
package com.appe.sse.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.sse.Channel;
import com.appe.sse.impl.BroadcastChannel;

/**
 * To be able to broadcast message for the client side. To listen to the sse channel, just subscribe to it, to publish back
 * event => just do push list to channel name.
 * 
 * @author ho
 *
 */
public class BroadcastReceiver extends BroadcastChannel {
	private static final Logger logger = LoggerFactory.getLogger(BroadcastReceiver.class);
	
	private WebTarget resource;
	/**
	 * With target to resource of API always point to {ROOT}/channels
	 * @param resource
	 */
	public BroadcastReceiver(WebTarget resource) {
		this(null, resource);
	}
	
	/**
	 * 
	 * @param name
	 * @param resource
	 */
	public BroadcastReceiver(String name, WebTarget resource) {
		super(name);
		this.resource = resource;
	}
	
	/**
	 * Publish a message to server upstream, return 1 if actually did it. Assuming the resource already handle
	 * authentication....
	 */
	@Override
	public void publish(String channel, Object message) {
		Response response = resource.path(channel)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE));
		
		//TODO: WARN IF STATUS CODE NOT 202 - Accepted?
		logger.debug("Publish channel: {}, response: {}", channel, response.getStatus());
	}
	
	/**
	 * return a client channel which is ready to poll event. By default it will not poll anything until has at least
	 * one listener.
	 * 
	 * TODO: NEED TYPE PROVIDER TO DESERIALIZE THE INPUT
	 */
	@Override
	protected Channel createChannel(String name, Class<?> type) {
		return	new ChannelInput(name, type, resource.path(name), 500);
	}
}
