package io.javacloud.framework.cdi.impl;

import io.javacloud.framework.cdi.ServiceRunner;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;

import java.lang.reflect.Method;
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
public class GuiceRunnerImpl extends ServiceRunner {
	private static final Logger logger = Logger.getLogger(GuiceRunnerImpl.class.getName());
	
	private static final String MANAGED_SERVICES = GuiceRegistryImpl.CDI_SERVICES + ".runlist";
	private final List<ResourceLoader.Binding> runlist = new ArrayList<>(); 
	public GuiceRunnerImpl() {
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
				
				runMethod(binding.typeClass(), "start");
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
					
					runMethod(binding.typeClass(), "stop");
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

	/**
	 * Resolve a method that best re-present the arguments
	 */
	@Override
	protected Method resolveMethod(Class<?> zclass, String methodName, Object... args) throws NoSuchMethodException {
		if(args == null || args.length == 0) {
			return zclass.getMethod(methodName);
		}
		
		//INPUT PARAMS
		Class<?>[] types = new Class<?>[args.length];
		for(int i = 0; i < args.length; i ++) {
			types[i] = args[i].getClass();
		}
		
		//DIRECT LOOKUP OR SEARCH FOR MATCHING
		try {
			return zclass.getMethod(methodName, types);
		} catch(NoSuchMethodException ex) {
			Method method = findMethod(zclass, methodName, types);
			if(method == null) {
				throw ex;
			}
			return method;
		}
	}
	
	/**
	 * Find best method matchings number of arguments and assignable parameter types
	 * 
	 * @param zclass
	 * @param methodName
	 * @param types
	 * @return
	 */
	private Method findMethod(Class<?> zclass, String methodName, Class<?>[] types) {
		for(Method m: zclass.getMethods()) {
			//MATCH NAME
			if(!m.getName().equals(methodName)) {
				continue;
			}
			
			//FIXME: BEST MATCHES IS NOT EXACT
			if(m.getParameterCount() == types.length) {
				return m;
			}
		}
		return null;
	}
}
