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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.sse.Channel;
import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;
import com.appe.sse.client.BroadcastReceiver;
import com.appe.util.Dictionary;

/**
 * 
 * @author ho
 *
 */
public class TestPollEvent {
	private static final Logger logger = LoggerFactory.getLogger(TestPollEvent.class);
	
	public static void main(String[] args) throws Exception {
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		WebTarget target = client.target("http://localhost:8080/v1/channels");
		
		BroadcastReceiver receiver = new BroadcastReceiver(target);
		Channel channel = receiver.subscribe("xxxx", Dictionary.class);
		channel.addListener(new ChannelListener() {
			@Override
			public void onEvent(ChannelEvent event) {
				logger.info("{}: --> {}", event.getType(), event.getMessage());
			}
		});
		Thread.currentThread().join();
		receiver.close();
	}
}
