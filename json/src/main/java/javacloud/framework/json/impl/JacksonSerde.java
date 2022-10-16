package javacloud.framework.json.impl;

import com.fasterxml.jackson.databind.module.SimpleModule;

public interface JacksonSerde {
	void configure(SimpleModule module);
}
