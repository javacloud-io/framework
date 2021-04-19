package javacloud.framework.util;

import java.util.function.Supplier;

/**
 * Helper to implement singleton Provider<T>
 * 
 * @author ho
 *
 * @param <T>
 */
public abstract class LazySupplier<T> implements Supplier<T> {
	private final Object lock = new Object();
	private volatile T instance;
	
	@Override
	public final T get() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = newInstance();
				}
			}
		}
		return instance;
	}
	
	public boolean isInstantiated() {
		synchronized (lock) {
			return (instance != null);
		}
	}
	
	/**
	 * 
	 * @return 
	 */
	protected abstract T newInstance();
}
