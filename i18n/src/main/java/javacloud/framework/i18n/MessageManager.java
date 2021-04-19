package javacloud.framework.i18n;
/**
 * A message factory for translation using both type safe and on-fly
 * 
 * @author ho
 *
 */
public interface MessageManager {
	/**
	 * return a message for given key at current locale
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	String getString(String key, Object... args);
	
	/**
	 * return strong type bundle messages definition using ConfigProperty
	 * 
	 * @param type
	 * @return
	 */
	<T> T getBundle(Class<T> type);
}
