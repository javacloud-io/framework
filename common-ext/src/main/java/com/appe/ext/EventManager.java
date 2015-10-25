package com.appe.ext;

/**
 * 1. Eventually event manager will persist event somewhere
 * 2. Event will delegate & process at correct handler
 * 3. Handler might then forward to different handler depends on use case.
 * 
 * @author tobi
 *
 */
public interface EventManager {
	/**
	 * To forward to next chain if don't know what todo.
	 * 
	 */
	public interface Dispatcher {
		/**
		 * @param event
		 */
		public void dispatch(Event event);
	}
	
	/**
	 * Handle an event if applicable
	 *
	 */
	public interface Handler {
		/**
		 * Override to handle event or dispatch to next handler.
		 * @param event
		 * @param dispatcher
		 */
		public void handle(Event event, Dispatcher dispatcher);
	}
	
	/**
	 * Event will queue at the central location before dispatching to handlers.
	 * Implementation should try the best to fill in event information, and see if any context around the current event
	 * is available before dispatching to handlers.
	 * 
	 * @param event
	 */
	public void postEvent(Event event);
}
