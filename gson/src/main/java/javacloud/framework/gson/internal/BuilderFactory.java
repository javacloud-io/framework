package javacloud.framework.gson.internal;

import java.lang.reflect.Method;

import com.google.protobuf.Message;

import javacloud.framework.util.InternalException;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;

public abstract class BuilderFactory {
	private static final BuilderFactory FACTORY = ResourceLoader.loadService(BuilderFactory.class);
	
	public static final BuilderFactory get() {
		return FACTORY;
	}
	
	public Message.Builder newBuilder(Class<?> type) {
		try {
			Method method = type.getMethod("newBuilder");
			return Objects.cast(method.invoke(type));
		} catch (Exception ex) {
			throw InternalException.of(ex);
		}
	}
}
