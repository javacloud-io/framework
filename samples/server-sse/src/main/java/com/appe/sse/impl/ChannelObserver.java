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

import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;
/**
 * Sample of how to filter out event & process them accordingly
 * 
 * @author ho
 *
 */
public class ChannelObserver implements ChannelListener {
	@Override
	public void onEvent(ChannelEvent event) {
		ChannelEvent.Type type = event.getType();
		if(type == ChannelEvent.Type.SUBSCRIBED) {
			onSubscribed(event);
		} else if(type == ChannelEvent.Type.SENT) {
			onSent(event);
		} else if(type == ChannelEvent.Type.RECEIVED) {
			onReceived(event);
		} else if(type == ChannelEvent.Type.ERROR) {
			onError(event, (Exception)event.getMessage());
		} else if(type == ChannelEvent.Type.CLOSED) {
			onClosed(event);
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void onSubscribed(ChannelEvent event) {
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void onSent(ChannelEvent event) {
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void onReceived(ChannelEvent event) {
	}
	
	/**
	 * 
	 * @param event
	 * @param ex
	 */
	protected void onError(ChannelEvent event, Exception ex) {
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void onClosed(ChannelEvent event) {
	}
}
