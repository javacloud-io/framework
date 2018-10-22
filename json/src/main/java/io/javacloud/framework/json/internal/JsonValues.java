package io.javacloud.framework.json.internal;

import java.util.List;

import io.javacloud.framework.json.JsonValue;
import io.javacloud.framework.util.Objects;

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
			/**
			 * Default TYPE interpretation
			 * @return
			 */
			@Override
			public Type type() {
				Object value = value();
				if(value == null) {
					return Type.NULL;
				}
				if(value instanceof Boolean) {
					return Type.BOOLEAN;
				}
				if(value instanceof String) {
					return Type.STRING;
				}
				if(value instanceof List) {
					return Type.ARRAY;
				}
				return Type.OBJECT;
			}
			
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
