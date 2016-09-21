package com.appe.framework.activity.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.appe.framework.activity.Event;
import com.appe.framework.activity.EventManager;

/**
 * Application will defer loading all the handler and process them ONE at the time. Event will passing to all handlers until
 * no more handler or the previous handle cancel them.
 * 
 * Due to deep dependency issue, EventManager not implement Configurable but do post configuration after most
 * of the handlers already bootstrapped.
 * 
 * @author tobi
 *
 */
@Singleton
public class EventManagerImpl implements EventManager {
	private final List<Handler> eventHandlers;
	@Inject
	public EventManagerImpl(List<Handler> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}
	
	/**
	 * Make sure the event context and everything else is right before handling off to QUEUE.
	 * Depends on Queue configuration to process, dispatch, persist...
	 */
	@Override
	public void postEvent(Event event) {
		Dispatcher dispatcher = new DispatcherImpl(eventHandlers);
		dispatcher.dispatch(event);
	}
	
	/**
	 * Default dispatcher implementation
	 */
	static class DispatcherImpl implements Dispatcher {
		private Iterator<Handler> iterator;
		public DispatcherImpl(Collection<Handler> handlers) {
			this(handlers.iterator());
		}
		
		/**
		 * 
		 * @param iterator
		 */
		public DispatcherImpl(Iterator<Handler> iterator) {
			this.iterator = iterator;
		}
		
		/**
		 * FORWARD TO NEXT HANDLE IF ANY.
		 */
		@Override
		public void dispatch(Event event) {
			if(iterator.hasNext()) {
				Handler handler = iterator.next();
				handler.handle(event, this);
			}
		}
	}
}
