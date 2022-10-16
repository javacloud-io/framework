package javacloud.framework.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

import javacloud.framework.json.impl.JacksonSerde;

public class CustomSerde implements JacksonSerde {
	@Override
	public void configure(SimpleModule module) {
		System.out.println("register something here!!!");
	}
}
