package javacloud.framework.jaxrs.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import javacloud.framework.jaxrs.ClientApplication;

@Singleton
public class TestApplication implements ClientApplication {
	@Override
	public List<?> clientComponents() {
		return Arrays.asList(GuiceHK2Feature.class, JacksonFeature.class);
	}
}
