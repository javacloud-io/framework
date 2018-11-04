package javacloud.framework.cdi.impl;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.cdi.internal.GuiceRunlist;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * META-INF/javacloud.cdi.services.runlist
 * 
 * @author ho
 *
 */
public class GuiceRunlistImpl extends GuiceRunlist {
	private static final Logger logger = Logger.getLogger(GuiceRunlistImpl.class.getName());
	
	private static final String MANAGED_SERVICES = GuiceRegistryImpl.CDI_SERVICES + ".runlist";
	private final List<ResourceLoader.Binding> runlist = new ArrayList<>(); 
	public GuiceRunlistImpl() {
	}
	
	/**
	 * FAIL FAST IF CAN'T START A SERVICE
	 */
	@Override
	public void startServices() throws Exception {
		synchronized(runlist) {
			if(!runlist.isEmpty()) {
				logger.fine("Runlist services already started!");
				return;
			}
			
			logger.fine("Start services runlist resource: " + MANAGED_SERVICES);
			List<ResourceLoader.Binding> bindings = ResourceLoader.loadBindings(MANAGED_SERVICES, ResourceLoader.getClassLoader());
			if(Objects.isEmpty(bindings)) {
				return;
			}
			
			//START & ADD TO RUNING LIST
			for(ResourceLoader.Binding binding: bindings) {
				logger.fine("Starting service: " + binding.typeClass());
				
				Object instance = ServiceRegistry.get().getInstance(binding.typeClass(),  binding.name());
				runInstance(instance, "start");
				runlist.add(binding);
			}
		}
	}
	
	/**
	 * TRY TO GIVE ALL SERVICES A CHANCE TO STOP GRACEFULLY
	 */
	@Override
	public void stopServices() throws Exception {
		synchronized(runlist) {
			//STOP IN REVERSED ORDER AS STARTED
			Exception lastException = null;
			for(int i = runlist.size() - 1; i >= 0; i --) {
				try {
					ResourceLoader.Binding binding = runlist.get(i);
					logger.fine("Stopping service: " + binding.typeClass());
					
					Object instance = ServiceRegistry.get().getInstance(binding.typeClass(),  binding.name());
					runInstance(instance, "stop");
				} catch(Exception ex) {
					lastException = ex;
				}
			}
			//IF THERE IS ERROR => JUST THROW LAST ONE!!!
			runlist.clear();
			if(lastException != null) {
				throw lastException;
			}
		}
	}
}
