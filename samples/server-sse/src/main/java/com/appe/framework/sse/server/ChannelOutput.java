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
package com.appe.framework.sse.server;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;

import com.appe.framework.sse.ChannelEvent;
import com.appe.framework.sse.ChannelListener;
import com.appe.framework.sse.impl.ChannelSource;
import com.appe.framework.sse.impl.SimpleChannel;
/**
 * A channel is a presentation of a client connect to SSE for notification. Each channel will be identify
 * by just a NAME.
 * 
 * @author ho
 *
 */
public class ChannelOutput extends EventOutput implements ChannelSource {
	final SimpleChannel delegate;
	/**
	 * 
	 * @param name
	 * @param type
	 */
	public ChannelOutput(String name, Class<?>  type) {
		this.delegate = new SimpleChannel(name, type);
	}
	
	/**
	 * 
	 */
	@Override
	public String getName() {
		return delegate.getName();
	}
	
	/**
	 * 
	 */
	@Override
	public int addListener(ChannelListener listener) {
		return delegate.addListener(listener);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean removeListener(ChannelListener listener) {
		return delegate.removeListener(listener);
	}

	/**
	 * 
	 */
	@Override
	public void fireEvent(ChannelEvent event) {
		delegate.fireEvent(event);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}

	/**
	 * return event builder for a given message
	 * 
	 * @param message
	 * @return
	 */
	OutboundEvent.Builder builder(Object message) {
		//SEND DATA EVENT AS JSON
		OutboundEvent.Builder builder = new OutboundEvent.Builder()
            .mediaType(MediaType.APPLICATION_JSON_TYPE);
		
		//USING FIRST CHANNEL TYPE
		Class<?> type = delegate.getType();
		if(type != null) {
			builder.data(type, message);
		} else {
			builder.data(message);
		}
		return builder;
	}
}
