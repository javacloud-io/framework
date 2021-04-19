package javacloud.framework.json;

import java.util.List;

/**
 * https://www.json.org
 * 
 * @author ho
 *
 */
public interface JsonValue {
	enum Type {
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
	default Type type() {
		Object value = value();
		if(value == null) {
			return Type.NULL;
		}
		if(value instanceof Boolean) {
			return Type.BOOLEAN;
		}
		if(value instanceof Number) {
			return Type.NUMBER;
		}
		if(value instanceof String) {
			return Type.STRING;
		}
		if(value instanceof List || value.getClass().isArray()) {
			return Type.ARRAY;
		}
		return Type.OBJECT;
	}
	
	/**
	 * 
	 * @return
	 */
	<T> T value();
}
