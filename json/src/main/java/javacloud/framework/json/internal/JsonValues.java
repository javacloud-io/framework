package javacloud.framework.json.internal;

import javacloud.framework.json.JsonValue;
import javacloud.framework.util.Objects;

/**
 * Provide basic operation for JsonValue
 * 
 * @author ho
 *
 */
public final class JsonValues {
	private JsonValues() {
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static JsonValue asValue(final Object value) {
		if(value instanceof JsonValue) {
			return (JsonValue)value;
		}
		return new JsonValue() {
			@Override
			public <T> T value() {
				return Objects.cast(value);
			}
			@Override
			public String toString() {
				return String.valueOf(value);
			}
		};
	}
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isNull(JsonValue json) {
		return (json == null || json.value() == null);
	}
}
