package io.javacloud.framework.json;

/**
 * https://www.json.org
 * 
 * @author ho
 *
 */
public interface JsonValue {
	public enum Type {
		NULL,
		BOOLEAN,
		NUMBER,
		STRING,
		OBJECT,
		ARRAY
	}
	
	/**
	 * 
	 * @return
	 */
	public Type type();
	
	/**
	 * 
	 * @return
	 */
	public <T> T value();
}
