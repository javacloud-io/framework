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
package come.appe.sse.impl;

import org.junit.Assert;
import org.junit.Test;

import com.appe.sse.Channel;
import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;
import com.appe.sse.impl.BroadcastLocal;
import com.appe.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class BroadcasterTest {
	@Test
	public void testListener() {
		BroadcastLocal broadcaster = new BroadcastLocal();
		//subscribe for
		Channel channel = broadcaster.subscribe("test", String.class);
		channel.register(new ChannelListener() {
			@Override
			public void onEvent(ChannelEvent event) {
				if(event.getType() == ChannelEvent.Type.RECEIVED) {
					Assert.assertEquals("message", event.getMessage());
				}
			}
		});
		
		broadcaster.publish("test", "message");
		Objects.close(broadcaster);
	}
}
