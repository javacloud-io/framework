package javacloud.framework.jacc.internal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javacloud.framework.cdi.ServiceRunlist;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

/**
 * ENSURE SANDBOX classes doesn't have access or damage its creator
 * 
 * 1. Executable class is java Function or has main()
 * 2. Limited environment variables will be provided
 * 
 * @author ho
 *
 */
public class StandardSandbox {
	public static final Set<String> BLACKLISTED_CLASSES = Objects.asSet(
			StandardSandbox.class.getName(),
			Process.class.getName()
		);
	private static Map<String, String> originEnv;
	private Set<String> blacklistedClasses;
	public StandardSandbox() {
	}
	
	/**
	 * return internal environment
	 * @return
	 */
	public static synchronized Map<String, String> getEnvironment() {
		return (originEnv == null? System.getenv() : originEnv);
	}
	
	/**
	 * Reset all environment variables
	 * 
	 */
	@SuppressWarnings("serial")
	public static synchronized void resetEnvironment() {
		if(originEnv == null) {
			try {
				Map<String, String> env = System.getenv();
				
				//ACCESS TO BACKED MAP FIELD
				Class<?> envClass = env.getClass();
				Field fm = envClass.getDeclaredField("m");
				fm.setAccessible(true);
				
				//REPLACE BACKING MAP && REDIRECT
				originEnv = Objects.cast(fm.get(env));
				fm.set(env, new HashMap<String, String>() {
					@Override
					public String put(String key, String value) {
						return originEnv.put(key, value);
					}
				});
				fm.setAccessible(false);
			} catch(Exception ex) {
				throw Exceptions.asUnchecked(ex);
			}
		}
	}
	
	/**
	 * Reset to a specific list of classes
	 * 
	 * @param blacklistedClasses
	 */
	public void resetBlacklistedClasses(Set<String> blacklistedClasses) {
		this.blacklistedClasses = blacklistedClasses;
	}
	
	/**
	 * Accumulate blacklisted classes
	 * 
	 * @param blacklistedClasses
	 * @return
	 */
	public StandardSandbox withBlacklistedClasses(Set<String> blacklistedClasses) {
		if(this.blacklistedClasses == null) {
			this.blacklistedClasses = new HashSet<>();
		}
		if(! Objects.isEmpty(blacklistedClasses)) {
			this.blacklistedClasses.addAll(blacklistedClasses);
		}
		return this;
	}
	
	/**
	 * 
	 * @param collector
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public <R> R execute(StandardClassCollector collector, Object input) throws Exception {
		ClassLoader classLoader = collector.asClassLoader(ResourceLoader.getClassLoader(),
				(name) -> (blacklistedClasses == null || blacklistedClasses.contains(name)));
		Class<?> mainClass = classLoader.loadClass(collector.getMainClass());
		return execute(mainClass, input);
	}
	
	/**
	 * 
	 * @param mainClass
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public <R> R execute(Class<?> mainClass, Object input) throws Exception {
		if(Function.class.isAssignableFrom(mainClass)) {
			Object parameters  = convertParameters(input, getActualParametersType(mainClass));
			return execute(mainClass, "apply", parameters);
		}
		
		//ASSSUMING MAIN
		String[] parameters  = convertParameters(input, String[].class);
		return execute(mainClass, "main", parameters);
	}
	
	/**
	 * Execute class using GUICE is dangerous, but we would love to allows INJECTION of basic object
	 * 
	 * @param mainClass
	 * @param methodName
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public <R> R execute(Class<?> mainClass, String methodName, Object input) throws Exception {
		return ServiceRunlist.get().runMethod(mainClass, methodName, input);
	}
	
	/**
	 * Convert input parameters to desire type
	 * 
	 * @param input
	 * @param type
	 * @return
	 */
	protected <T> T convertParameters(Object input, Class<T> type) {
		return Objects.cast(input);
	}
	
	/**
	 * return input type of function class
	 * 
	 * @param functionClass
	 * @return
	 */
	protected Class<?> getActualParametersType(Class<?> functionClass) {
		Type type = ((ParameterizedType)functionClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
		if(type instanceof Class) {
			return (Class<?>)type;
		}
		return (Class<?>)((ParameterizedType)type).getRawType();
	}
}
