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

import java.io.Closeable;

/**
 * A channel is something that both side can communicate, might be one/bind direction.
 * 
 * @author ho
 *
 */
public interface Channel extends Closeable {
	/**
	 * Unique identify channel
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Register an event listener
	 * 
	 * @param listener
	 * @return
	 */
	public int register(ChannelListener listener);
}
