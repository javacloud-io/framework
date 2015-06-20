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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;

/**
 * We do not expect large amounts of broadcaster listeners additions/removals, but large amounts of traversals.
 * 
 * @author ho
 *
 */
public class SimpleChannel implements ChannelSource {
    protected final List<ChannelListener> listeners = new CopyOnWriteArrayList<ChannelListener>();
    protected final Class<?> type;
    private   final String   name;
    /**
     * Channel with it data type of event
     * @param name
     * @param type
     */
    public SimpleChannel(String name, Class<?> type) {
    	this.name = name;
    	this.type = type;
    }
    
    /**
	 * 
	 * @return
	 */
    @Override
	public String getName() {
		return name;
	}
	
    /**
     * 
     * @param listener
     * @return
     */
    @Override
    public int addListener(ChannelListener listener) {
    	listeners.add(listener);
    	return listeners.size();
    }
    
    /**
     * 
     */
    @Override
	public boolean removeListener(ChannelListener listener) {
		return	listeners.remove(listener);
	}

	/**
     * Notify to all listeners
     * 
     * @param event
     */
    @Override
    public void fireEvent(ChannelEvent event) {
    	for(ChannelListener l: listeners) {
    		l.onEvent(event);
    	}
    }
    
    /**
     * 
     */
    @Override
	public void close() throws IOException {
    	listeners.clear();
	}

	/**
     * return internal channel type
     * 
     * @return
     */
    public Class<?> getType() {
    	return type;
    }
    
    /**
	 * return internal ID of the channel using hex(identity).
	 */
	@Override
	public String toString() {
		return Integer.toHexString(hashCode()) + "@" + name;
	}
}
