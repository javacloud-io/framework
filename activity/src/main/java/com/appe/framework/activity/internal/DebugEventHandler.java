package com.appe.framework.activity.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.activity.Event;
import com.appe.framework.activity.EventManager;

/**
 * Log the event in JSON format.
 * 
 * @author tobi
 *
 */
public class DebugEventHandler implements EventManager.Handler {
	private static final Logger logger = LoggerFactory.getLogger(DebugEventHandler.class);
	public DebugEventHandler() {
	}
	
	/**
	 * DEBUG EVENT IN JSON & dispatch to next chain if ANY.
	 */
	@Override
	public void handle(Event event, EventManager.Dispatcher dispatcher) {
		try {
			logger.debug("Event: {}", event);
		} finally {
			dispatcher.dispatch(event);
		}
	}
}
