package io.javacloud.framework.cdi;

import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Helper class to invoke a method using CDI. Start/Stop managed service from: META-INF/javacloud.cdi.services.runlist
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
	 * Start managed services registered to runlist. It's safe to be call multiple times.
	 * 
	 * @throws Exception
	 */
	public abstract void startServices() throws Exception;
	
	/**
	 * Stop managed services registered to runlist
	 * 
	 * @throws Exception
	 */
	public abstract void stopServices() throws Exception;
	
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
			throw new InvocationTargetException(ex);
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
			throw new InvocationTargetException(ex);
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
