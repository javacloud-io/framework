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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.appe.sse.Channel;
/**
 * All client channel with the same name is GROUP together forming a queue. The idea is to be able to support a easier
 * way to control the broadcast. To be able to guarantee message delivery.
 * 
 * @author ho
 *
 */
public class ChannelQueue implements Iterable<Channel> {
	private Queue<Channel> queue;
	/**
	 * 
	 */
	public ChannelQueue() {
		this.queue = new ConcurrentLinkedQueue<Channel>();
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Iterator<Channel> iterator() {
		return queue.iterator();
	}
	
	/**
	 * 
	 * @param channel
	 */
	public void add(Channel channel) {
		queue.add(channel);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}
