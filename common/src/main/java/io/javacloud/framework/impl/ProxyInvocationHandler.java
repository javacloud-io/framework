package io.javacloud.framework.impl;

import io.javacloud.framework.util.Objects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Default proxy invocation handle to redirect native methods to handler
 * 
 * @author ho
 *
 */
public abstract class ProxyInvocationHandler implements InvocationHandler {
	//DEFAULT IMPLEMENTATION FOR THESE NATIVE METHODS WILL BE DELEGATED 
	private static final Set<String> NATIVE_METHODS = Objects.asSet();
	static {
		for(Method m: Object.class.getMethods()) {
			NATIVE_METHODS.add(m.getName());
		}
	}
	protected ProxyInvocationHandler() {
	}
	
	/**
	 * 1. Look for the annotation to figure KEY/DEFAULT value....
	 * 2. Convert the string value to the correct target one.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//ENSURE CORRECT DEFAULT METHOD IMPLEMENTATION
		if(NATIVE_METHODS.contains(method.getName())) {
			return method.invoke(this, args);
		}
		return invoke(method, args);
	}
	
	/**
	 * 
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	protected abstract Object invoke(Method method, Object[] args) throws Throwable;
}
