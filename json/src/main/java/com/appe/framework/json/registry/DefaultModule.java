package com.appe.framework.json.registry;

import com.appe.framework.internal.GuiceModule;
import com.appe.framework.json.Externalizer;
import com.appe.framework.json.internal.JacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 * @author ho
 *
 */
public class DefaultModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(ObjectMapper.class).to(JacksonMapper.class);
		bind(Externalizer.class).to(JacksonMapper.class);
		bindNamed(Externalizer.class, Externalizer.JSON).to(JacksonMapper.class);
	}
}
