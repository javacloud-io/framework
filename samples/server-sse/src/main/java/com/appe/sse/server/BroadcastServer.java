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
package com.appe.sse.server;

import java.util.Iterator;

import org.glassfish.jersey.media.sse.OutboundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.ext.Identifiable;
import com.appe.sse.Channel;
import com.appe.sse.ChannelEvent;
import com.appe.sse.impl.BroadcastChannel;
import com.appe.sse.impl.ChannelQueue;
/**
 * 1. Channel will be automatically organize into GROUP.
 * 2. Each group will managed it own broadcaster
 * 3. How to keep track of client & clean up (broadcast ping command?)
 * 
 * TODO:
 * -To be able to support guarantee EXACT ONE delivery
 * -To be able to support guarantee at LEAST ONE delivery
 * 
 * @author ho
 *
 * @param <T>
 */
public class BroadcastServer extends BroadcastChannel {
	private static final Logger logger = LoggerFactory.getLogger(BroadcastServer.class);
	/**
	 * Default to ROOT channel
	 */
	public BroadcastServer() {
		this(null);
	}
	
	/**
	 * 
	 * @param name
	 */
	public BroadcastServer(String name) {
		super(name);
	}
	
	/**
	 * 1. Find the channels which message is applicable of
	 * 2. Flush them out in chunk
	 */
	@Override
	public void publish(String channel, Object message) {
		ChannelQueue queue = channelQueue(channel, false);
		if(queue == null || queue.isEmpty()) {
			//TODO: should queue event for awhile?
			logger.debug("Channel: {} is empty", channel);
		} else {
		
			//USING FIRST CHANNEL TO BUILD MESSAGE
			ChannelOutput channelOutput = (ChannelOutput)queue.iterator().next();
			OutboundEvent.Builder builder = channelOutput.builder(message);
			
			if(message instanceof Identifiable) {
	        	builder.id(String.valueOf(((Identifiable<?>)message).getId()));
	        }
			broadcast(builder.build(), queue);
		}
	}
	
	/**
	 * Make sure to be able to find a broadcaster for given channel, then flush the message out.
	 * Default implementation just to using ChannelOutput
	 */
	@Override
	protected Channel createChannel(String name, Class<?> type) {
		return new ChannelOutput(name, type);
	}
	
	/**
	 * Broadcast event to all binded out bound. It's can be done in chunk, parallel....or just notify only ONE.
	 * randomly or leader...depends on the strategy message might be queue, schedule for delivery...
	 *  
	 * @param event
	 * @param queue
	 */
	protected int broadcast(OutboundEvent event, Iterable<Channel> queue) {
		int success = 0;
		for (Iterator<Channel> iterator = queue.iterator(); iterator.hasNext(); ) {
			ChannelOutput channel = (ChannelOutput)iterator.next();
			try {
				if(!channel.isClosed()) {
					channel.write(event);
					fireChannelEvent(channel, new ChannelEvent(ChannelEvent.Type.SENT, event.getData()));
					success ++;
				}
            } catch (Exception ex) {
            	fireChannelEvent(channel, new ChannelEvent(ChannelEvent.Type.ERROR, ex));
            }
			
			//CLEAN UP IF ISSUE
			if(channel.isClosed()) {
				iterator.remove();
				fireChannelEvent(channel, new ChannelEvent(ChannelEvent.Type.CLOSED));
			}
        }
		return success;
	}
}
