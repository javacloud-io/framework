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
package com.appe.samples.showcase.restapi;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.sse.SseFeature;

import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;
import com.appe.framework.sse.Broadcaster;
import com.appe.framework.sse.Channel;
import com.appe.framework.sse.server.BroadcastServer;

/**
 * 1. Broadcast a message to all client on a given channel, to be able to do as fast as possible
 * 
 * @author ho
 *
 */
@Singleton
@Path("channels")
public class ChannelResource {
	private Broadcaster broadcaster = new BroadcastServer();
	
	/**
	 * Broadcast a message to all clients listen at a given channel
	 * 1. Do we need to make sure 0-1 got the message
	 * 2. What is no client is currently active, how long the message will stay
	 * 3. Slice/Parallel the event notification
	 * 
	 * @param message
	 * @return
	 */
	@POST @Path("{channel}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response publish(@PathParam("channel") String channel, Dictionary message) {
		broadcaster.publish(channel, message);
		return Response.accepted(Objects.asDict()).build();
	}
	
	/**
	 * Subscribe to event at given channel, will get SSE push when there is new event from channel
	 * 1. How do we identify client? what will be a unique
	 * 2. Always using Dictionary for all CHANNELs.
	 * 
	 * @param channel
	 * @param lastEventId
	 */
	@GET @Path("{channel}")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
    public Response subscribe(@PathParam("channel") String channel,
    		@HeaderParam(SseFeature.LAST_EVENT_ID_HEADER) String lastEventId) {
		//TODO: to support re-connect with Last-Event-ID to be able to relay missing events?
		Channel c = broadcaster.subscribe(channel, Dictionary.class);
		
		//TESTING BROWSER
		//header("Access-Control-Allow-Origin", "*")
		return Response.ok(c).build();
	}
}
