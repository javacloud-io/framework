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
package com.appe.sse.impl;

import com.appe.sse.Channel;
import com.appe.sse.ChannelEvent;
/**
 * Local broadcast channel where it always broadcast to listener locally. Can be use to unit test SSE stuff.
 * 
 * @author ho
 *
 */
public class BroadcastLocal extends BroadcastChannel {
	public BroadcastLocal() {
		super(null);
	}
	
	/**
	 * Broadcast message to all listeners on channel.
	 */
	@Override
	public void publish(String channel, Object message) {
		ChannelQueue queue = channelQueue(channel, false);
		if(queue == null || queue.isEmpty()) {
			return;
		}
		
		//DELIVERY LOCALLY 
		for(Channel c: queue) {
			fireChannelEvent(c, new ChannelEvent(ChannelEvent.Type.RECEIVED, message));
		}
	}
	
	/**
	 * JUST A NOOP CHANNEL
	 */
	@Override
	protected Channel createChannel(String name, Class<?> type) {
		return new SimpleChannel(name, type);
	}
}
