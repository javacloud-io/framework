package io.javacloud.framework.cdi.impl;

import io.javacloud.framework.cdi.ServiceRunner;
import java.lang.reflect.Method;

/**
 * 
 * @author ho
 *
 */
public class GuiceRunnerImpl extends ServiceRunner {
	public GuiceRunnerImpl() {
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
			//MATCH NAME & NUMBER OF PARAMS
			if(m.getName().equals(methodName) && m.getParameterCount() == types.length) {
				//TODO: RETURN ONLY IF THIS IS THE BEST MATCHES
				return m;
			}
		}
		return null;
	}
}
