package javacloud.framework.cdi.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.cdi.ServiceRunlist;
import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public abstract class GuiceRunlist extends ServiceRunlist {
	protected GuiceRunlist() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public <T> T runMethod(Class<?> zclass, String methodName, Object... args) throws Exception {
		return runInstance(zclass, methodName, args);
	}
	
	/**
	 * Using IMPL class if requires dynamic lookup to ensure methods are available.
	 * 
	 * @param instance
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	protected <T> T runInstance(Object instance, String methodName, Object...args) throws Exception {
		Method method;
		if(instance instanceof Class<?>) {
			instance = ServiceRegistry.get().getInstance((Class<?>)instance);
			method = resolveMethod(instance.getClass(), methodName, args);
		} else {
			method = resolveMethod(instance.getClass(), methodName, args);
		}
		
		//STATIC METHOD
		if(Modifier.isStatic(method.getModifiers())) {
			return	Objects.cast(method.invoke(null, args));
		}
		return	Objects.cast(method.invoke(instance, args));
	}
	
	/**
	 * Resolve a method that best re-present the arguments
	 */
	protected Method resolveMethod(Class<?> zclass, String methodName, Object... args)
			throws NoSuchMethodException {
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
