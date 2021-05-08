package javacloud.framework.json.internal;

import javacloud.framework.json.JsonValue;
import javacloud.framework.util.Objects;

/**
 * Provide basic operation for JsonValue
 * 
 * @author ho
 *
 */
public final class JsonObject implements JsonValue {
	private final Object value;
	
	private JsonObject(Object value) {
		this.value = value;
	}
	
	@Override
	public <T> T value() {
		return Objects.cast(value);
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static JsonValue of(final Object value) {
		if (value instanceof JsonValue) {
			return (JsonValue)value;
		}
		return new JsonObject(value);
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
