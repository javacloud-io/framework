package io.javacloud.framework.cdi;

import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;
import io.javacloud.framework.util.UncheckedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 
 * @author ho
 *
 */
public abstract class ServiceRunner {
	private static final ServiceRunner RUNNER = ResourceLoader.loadService(ServiceRunner.class);
	protected ServiceRunner() {
	}
	
	/**
	 * return the only instance available
	 * 
	 * @return
	 */
	public static final ServiceRunner get() {
		return RUNNER;
	}
	
	/**
	 * 
	 * @param zclass
	 * @param method
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 */
	public <T> T runMethod(Class<?> zclass, Method method, Object...args) throws InvocationTargetException {
		try {
			if(Modifier.isStatic(method.getModifiers())) {
				return	Objects.cast(method.invoke(null, args));
			}
			//USING CDI INSTANCE
			Object instance = ServiceRegistry.get().getInstance(zclass);
			return	Objects.cast(method.invoke(instance, args));
		} catch(IllegalAccessException ex) {
			throw UncheckedException.wrap(ex);
		}
	}
	
	/**
	 * 
	 * @param zclass
	 * @param methodName
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 */
	public <T> T runMethod(Class<?> zclass, String methodName, Object...args) throws InvocationTargetException {
		try {
			Method method = resolveMethod(zclass, methodName, args);
			return runMethod(zclass, method, args);
		} catch(NoSuchMethodException ex) {
			throw UncheckedException.wrap(ex);
		}
	}
	
	/**
	 * 
	 * @param zclass
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 */
	protected abstract Method resolveMethod(Class<?> zclass, String methodName, Object...args) throws NoSuchMethodException;
}
