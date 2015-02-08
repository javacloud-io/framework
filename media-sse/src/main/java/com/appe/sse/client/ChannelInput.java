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
package com.appe.sse.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.AppeException;
import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;
import com.appe.sse.impl.SimpleChannel;
/**
 * 1. A connection with dedicated thread to pull server
 * 2. Always re-connect with a handle full of event
 * 3. Not connect to server until have first listener
 * 
 * TODO: consider to throw event when subscribed, closed, error...
 * 
 * @author ho
 *
 */
public class ChannelInput extends SimpleChannel {
	private static final Logger logger = LoggerFactory.getLogger(ChannelInput.class);
	
	private final ScheduledExecutorService executor;
	private final CountDownLatch connectSignal = new CountDownLatch(1);
	
	private WebTarget channel;
	private boolean   disableKeepAlive = true;
	
	/**
	 * Dedicated thread to pull & re-connect if anything wrong
	 * 
	 * @param name
	 * @param type
	 * @param channel
	 * @param reconnectDelay
	 */
	public ChannelInput(final String name, Class<?> type, WebTarget channel, long reconnectDelay) {
		super(name, type);
		this.channel  = channel;
		
		//EXECUTOR & DEFAULT POLLER
		this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Sse-Poller-" + name);
			}
		});
		executor.submit(new EventPoller(reconnectDelay));
	}
	
	/**
	 * ONLY THE FIRST LISTENER WILL TRIGGER CONNECT
	 */
	@Override
	public int register(ChannelListener listener) {
		int count = super.register(listener);
		if(count == 1) {
			connectSignal.countDown();
		}
		return count;
	}
	
	/**
	 * JUST WAIT UPTO 5 SECONDS...
	 */
	@Override
	public void close() throws IOException {
		close(5, TimeUnit.SECONDS);
		super.close();
	}

	/**
	 * Perform thread shutdown and clean up resources and make sure there will be no LEAK as well as LOST
	 * TODO: considering fire CLOSED event?
	 */
	public final boolean close(long delay, TimeUnit unit) {
		executor.shutdown();
		
		//WAIT FOR TERMINATION
		try {
			return executor.awaitTermination(delay, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
	}
	
	/**
	 * return true if an exception is recoverable so it will be reconnect automatically.
	 * @param ex
	 * @return
	 */
	protected boolean isRecoverableEx(Exception ex) {
		return	AppeException.isCausedBy(ex, ConnectException.class);
	}
	
	/**
	 * Implement a long-polling event from server. Trying to stay as long as possible and re-connect if being disconnected.
	 * We only recover from a recoverable exception such as: Server is overloaded, temporary unavailable,...
	 */
	class EventPoller implements Runnable {
		private long 	reconnectDelay;	//MS
		private String 	lastEventId;	//
		private EventInput eventInput;
		/**
		 * 
		 * @param reconnectDelay
		 */
		public EventPoller(long reconnectDelay) {
			this.reconnectDelay = reconnectDelay;
		}
		
		/**
		 * Make a copy of existing poller
		 * @param poller
		 */
		public EventPoller(EventPoller poller) {
			this.lastEventId = poller.lastEventId;
			this.reconnectDelay = poller.reconnectDelay;
		}
		
		/**
		 * 
		 */
		@Override
		public void run() {
			Thread t = Thread.currentThread();
			try {
				//WAIT UNTIL AT A LISTENER TO CONNECT
				connectSignal.await();
				this.eventInput = connect();
				
				//TODO: WAIT UNTIL READY TO READ?
				while(!t.isInterrupted()) {
					if(eventInput == null || eventInput.isClosed()) {
						logger.warn("Caught event input closed");
						reconnect(reconnectDelay, this);
						break;
					}
					
					//A NULL EVENT IS BEGINING OF CHANNEL CLOSE?
					InboundEvent event = eventInput.read();
					if(event == null) {
						continue;
					}
					
					//CACHE LAST USAGE INFORMATION
					if(event.getId() != null) {
						lastEventId = event.getId();
					}
					if(event.isReconnectDelaySet()) {
						reconnectDelay = event.getReconnectDelay();
					}
					
					//DECODE MESSAGE & FIRE EVENT
					Object message = event.readData(type);
					fireEvent(new ChannelEvent(ChannelEvent.Type.RECEIVED, message));
				}
			} catch (ServiceUnavailableException ex) {
				logger.warn("Caught 503 - Service unavailable");
				//fireEvent(new ChannelEvent(ChannelEvent.Type.ERROR, ex));
				
				long delay = reconnectDelay;
                if (ex.hasRetryAfter()) {
                    final Date requestTime = new Date();
                    delay = ex.getRetryTime(requestTime).getTime() - requestTime.getTime();
                    delay = (delay > 0) ? delay : 0;
                }
                reconnect(delay, this);
			} catch(Exception ex) {
				//TODO: SHOULD PERFORM BACK OFF IF RECOVERABLE CONNECT ISSUE?
				//fireEvent(new ChannelEvent(ChannelEvent.Type.ERROR, ex));
				if(isRecoverableEx(ex)) {
					logger.warn("Caught a connection issue");
					reconnect(reconnectDelay, this);
				} else {
					long delay = (listeners.size() + 1) * reconnectDelay; 
					close(delay, TimeUnit.MILLISECONDS);
				}
			} finally {
				disconnect();
			}
		}
		
		/**
		 * Make sure totally disconnect with the server.
		 */
		private void disconnect() {
			if(eventInput != null && !eventInput.isClosed()) {
				eventInput.close();
				//fireEvent(new ChannelEvent(ChannelEvent.Type.CLOSED));
			}
		}
		
		/**
		 * Connect to server to receive chunk event input
		 * @return
		 */
		private EventInput connect() {
			logger.debug("Connect to server: {}", channel.getUri());
            final Invocation.Builder request = channel.request(SseFeature.SERVER_SENT_EVENTS_TYPE);
            if (lastEventId != null && !lastEventId.isEmpty()) {
                request.header(SseFeature.LAST_EVENT_ID_HEADER, lastEventId);
            }
            if (disableKeepAlive) {
                request.header("Connection", "close");
            }
            return request.get(EventInput.class);
        }
		
		/**
		 * Schedule to run poller with delay
		 * 
		 * @param delay
		 * @param poller
		 */
		private void reconnect(long delay, EventPoller poller) {
			logger.debug("Trying to reconnect in: {}(ms)", delay);
	        final EventPoller task = new EventPoller(poller);
	        if (delay > 0) {
	            executor.schedule(task, delay, TimeUnit.MILLISECONDS);
	        } else {
	            executor.submit(task);
	        }
		}
	}
}
