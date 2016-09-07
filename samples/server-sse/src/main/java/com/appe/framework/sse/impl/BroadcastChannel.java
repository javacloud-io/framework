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
package com.appe.framework.sse.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.sse.Broadcaster;
import com.appe.framework.sse.Channel;
import com.appe.framework.sse.ChannelEvent;
/**
 * A channel can broadcast message to all subscriber. In most case, subscribers are connected through HTTP protocol.
 * 
 * @author ho
 *
 */
public abstract class BroadcastChannel extends SimpleChannel implements Broadcaster {
	private static final Logger logger = LoggerFactory.getLogger(BroadcastChannel.class);
	private final ConcurrentMap<String, ChannelQueue> channels = new ConcurrentHashMap<String, ChannelQueue>();
	
	/**
	 * 
	 * @param name
	 */
	protected BroadcastChannel(String name) {
		super(name, null);
	}
	
	/**
	 * Make sure to be able to find a broadcaster for given channel, then flush the message out.
	 * Default implementation just to using ChannelOutput
	 */
	@Override
	public Channel subscribe(String name, Class<?> type) {
		Channel channel = createChannel(name, type);
		channelQueue(name, true)
			.add(channel);
		
		fireChannelEvent(channel, new ChannelEvent(ChannelEvent.Type.SUBSCRIBED));
		return channel;
	}
	
	/**
	 * return a broadcaster for given channel, create if not exist. It's might have multiple create but ONLY ONE WIN!!!
	 * 
	 * @param channel
	 * @param force
	 * @return
	 */
	protected ChannelQueue channelQueue(String channel, boolean force) {
		ChannelQueue queue = channels.get(channel);
		if(queue == null && force) {
			//FIXME: a lock free but guarantee only instance created is prefer
			synchronized(channels) {
				if((queue = channels.get(channel)) == null) {
					logger.debug("Create channel: {}", channel);
					channels.putIfAbsent(channel, new ChannelQueue());
					queue = channels.get(channel);
				}
			}
		}
		return queue;
	}
	
	/**
	 * Close all the channels and remove the listeners...
	 */
	@Override
	public void close() throws IOException {
		for(ChannelQueue queue: channels.values()) {
			for(Iterator<Channel> iterator = queue.iterator(); iterator.hasNext() ;) {
				Channel channel = iterator.next();
				try {
					channel.close();
				}catch(IOException ex) {
				}
				iterator.remove();
				fireChannelEvent(channel, new ChannelEvent(ChannelEvent.Type.CLOSED));
			}
		}
		
		//CLEAN UP
		channels.clear();
		super.close();
	}
	
	/**
	 * Should be override to forward event to the channels. ALL CHANNEL IS DELEGATEABLE
	 * 
	 * @param channel
	 */
	protected void fireChannelEvent(Channel channel, ChannelEvent event) {
		logger.debug("Fire event: {} on channel: {}", event, channel);
		((ChannelSource)channel).fireEvent(event);
	}
	
	/**
	 * Create a channel with name & expected to receive event of type
	 * 
	 * @param name
	 * @param type
	 * 
	 * @return
	 */
	protected abstract Channel createChannel(String name, Class<?> type);
}
