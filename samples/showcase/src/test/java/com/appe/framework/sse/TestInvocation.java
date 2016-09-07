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
package com.appe.framework.sse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.sse.client.BroadcastReceiver;
import com.appe.framework.util.Dictionary;

/**
 * Caller make remote call to callee through SSE channel without knowing who the other IS.
 * 
 * @author ho
 *
 */
public class TestInvocation {
	private static final Logger logger = LoggerFactory.getLogger(TestInvocation.class);
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		WebTarget target = client.target("http://localhost:8080/v1/channels");
		
		final BroadcastReceiver receiver = new BroadcastReceiver(target);
		
		//CALLER
		final Channel caller = receiver.subscribe("caller", Dictionary.class);
		caller.addListener(new ChannelListener() {
			@Override
			public void onEvent(ChannelEvent event) {
				//ECHO THE RESOLT FOR NOW
				logger.info("{}: --> {}", event.getType(), event.getMessage());
			}
		});
		
		//CALLEE receive an invocation => perform and return result back to callee
		final Channel callee = receiver.subscribe("callee", Dictionary.class);
		callee.addListener(new ChannelListener() {
			@Override
			public void onEvent(ChannelEvent event) {
				//DO CALCULATION & SEND BACK RESULT
				receiver.publish("caller", event.getMessage());
			}
		});
		
		//
		Thread.currentThread().join();
		receiver.close();
	}
}
