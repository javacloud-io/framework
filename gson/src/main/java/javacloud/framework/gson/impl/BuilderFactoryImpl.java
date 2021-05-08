package javacloud.framework.gson.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import com.google.protobuf.Message;

import javacloud.framework.gson.internal.BuilderFactory;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.Objects;

@Singleton
public class BuilderFactoryImpl extends BuilderFactory {
	private final Map<Class<?>, Method> builderMethods = new ConcurrentHashMap<>();
	
	// type.newBuilder()
	@Override
	public Message.Builder newBuilder(Class<?> type) {
		try {
			Method method = builderMethods.get(type);
			if (method == null) {
				method = type.getMethod("newBuilder");
				builderMethods.putIfAbsent(type, method);
			}
			return Objects.cast(method.invoke(type));
		} catch (Exception ex) {
			throw InternalException.of(ex);
		}
	}
}
