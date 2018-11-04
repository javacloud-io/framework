package javacloud.framework.cdi;

import javax.inject.Provider;
/**
 * 
 * @author ho
 *
 * @param <T>
 */
public abstract class SingletonProvider<T> implements Provider<T> {
	private Object lock = new Object();
	private volatile T instance;
	@Override
	public final T get() {
		if(instance == null) {
			synchronized(lock) {
				if(instance == null) {
					instance = create();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract T create();
}
