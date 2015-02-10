package com.appe.sse.impl;

import com.appe.sse.ChannelEvent;
import com.appe.sse.ChannelListener;
/**
 * 
 * @author ho
 *
 */
public class ChannelAdapter implements ChannelListener {
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
