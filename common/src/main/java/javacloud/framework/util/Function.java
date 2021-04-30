package javacloud.framework.util;
/**
 * 
 * @author ho
 *
 * @param <T>
 * @param <R>
 */
public interface Function<T, R> extends java.util.function.Function<T, R> {
	/**
	 * Default implementation redirect to invoke
	 */
	@Override
	default public R apply(T input) {
		try {
			return invoke(input);
		}catch(Exception ex) {
			throw InternalException.of(ex);
		}
	}
	
	/**
	 * Using handle method to pass through exception
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public R invoke(T input) throws Exception;
}
