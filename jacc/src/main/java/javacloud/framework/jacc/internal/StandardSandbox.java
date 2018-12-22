package javacloud.framework.jacc.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javacloud.framework.cdi.ServiceRegistry;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;

/**
 * ENSURE SANDBOX classes doesn't have access or damage its creator
 * 
 * 1. Executable class is java Function or has main()
 * 2. Limited environment variables will be provided
 * 3. Sandbox is only secure if class is loaded from ClassCollector with blacklisted
 * 
 * @author ho
 *
 */
public class StandardSandbox extends ClassLoader {
	//DEFAULT BLACKLISTED CLASS
	public static final Set<String> BLACKLISTED_CLASSES = Objects.asSet(
			StandardSandbox.class.getName(),
			Process.class.getName()
		);
	private static Map<String, String> originEnv;
	private Predicate<String> blacklistedClasses;
	/**
	 * 
	 * @param sharedClassLoader
	 */
	public StandardSandbox(ClassLoader sharedClassLoader) {
		super(sharedClassLoader);
	}
	
	/**
	 * return internal environment
	 * @return
	 */
	public static synchronized Map<String, String> getEnvironment() {
		return (originEnv == null? System.getenv() : originEnv);
	}
	
	/**
	 * Reset all environment variables to new seedEnv
	 * 
	 * @param seedEnv
	 */
	@SuppressWarnings("serial")
	public static synchronized void resetEnvironment(Map<String, String> seedEnv) {
		try {
			Map<String, String> env = System.getenv();
			
			//ACCESS TO BACKED MAP FIELD
			Class<?> envClass = env.getClass();
			Field fm = envClass.getDeclaredField("m");
			fm.setAccessible(true);
			
			//REPLACE BACKING MAP && REDIRECT
			Map<String, String> backedEnv = Objects.cast(fm.get(env));
			if(originEnv == null) {
				originEnv = backedEnv;
				//OVERRIDE PUT INCASE OF OTHER DID SIMILAR
				backedEnv = new HashMap<String, String>() {
					@Override
					public String put(String key, String value) {
						return originEnv.put(key, value);
					}
				};
				fm.set(env, backedEnv);
			}
			//SEED ALL ENTRIES
			if(!Objects.isEmpty(seedEnv)) {
				backedEnv.putAll(seedEnv);
			}
			fm.setAccessible(false);
		} catch(Exception ex) {
			throw Exceptions.asUnchecked(ex);
		}
	}
	
	/**
	 * Reset to a specific list of classes
	 * 
	 * @param blacklistedClasses
	 */
	public void resetBlacklistedClasses(Set<String> blacklistedClasses) {
		this.blacklistedClasses = (className) -> {return blacklistedClasses.contains(className);};
	}
	
	/**
	 * Accumulate blacklisted classes
	 * 
	 * @param blacklistedClasses
	 * @return
	 */
	public StandardSandbox withBlacklistedClasses(Set<String> blacklistedClasses) {
		if(this.blacklistedClasses == null) {
			resetBlacklistedClasses(blacklistedClasses);
			return this;
		}
		return withBlacklistedClasses((className) -> {return blacklistedClasses.contains(className);});
	}
	
	/**
	 * Allows complicated blacklisted
	 * @param blacklistedClasses
	 * @return
	 */
	public StandardSandbox withBlacklistedClasses(Predicate<String> blacklistedClasses) {
		this.blacklistedClasses = this.blacklistedClasses.or(blacklistedClasses);
		return this;
	}
	
	/**
	 * Execute main class from collector and return output
	 * 
	 * @param collector
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public <T, R> R invoke(StandardClassCollector collector, T input) throws Exception {
		ClassLoader classLoader = collector.asClassLoader(this);
		Class<?> mainClass = classLoader.loadClass(collector.getMainClass());
		return invoke(mainClass, input);
	}
	
	/**
	 * Execute a function or static main(String[])
	 * 
	 * @param mainClass
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public <T, R> R invoke(Class<?> mainClass, T input) throws Exception {
		//INVOCATED WITH FUNCTION
		if(Function.class.isAssignableFrom(mainClass)) {
			Object parameters  = convertParameters(input, getActualParametersType(mainClass));
			Function<Object, R> instance  = Objects.cast(ServiceRegistry.get().getInstance(mainClass));
			return instance.apply(parameters);
		}
		
		//DEFAULT TO MAIN(String[]) FUNCTION
		String[] parameters  = convertParameters(input, String[].class);
		Method mainMethod = mainClass.getMethod("main", String[].class);
		return Objects.cast(mainMethod.invoke(null, (Object)parameters));
	}
	
	/**
	 *  Blacklisted class that in the list.
	 *  
	 */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(blacklistedClasses != null && blacklistedClasses.test(name)) {
			throw new AccessControlException(name + " is blacklisted");
		}
		return super.loadClass(name, resolve);
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
