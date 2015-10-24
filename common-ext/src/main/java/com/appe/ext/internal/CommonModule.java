package com.appe.ext.internal;

import javax.ws.rs.client.Client;

import com.appe.AppeNamespace;
import com.appe.jaxrs.client.HttpClientProvider;
import com.appe.jaxrs.json.JacksonMapper;
import com.appe.registry.internal.GuiceModule;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 * @author ho
 *
 */
public class CommonModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(AppeNamespace.class).to(ThreadLocalNamespace.class);
		bind(ObjectMapper.class).to(JacksonMapper.class);
		bind(Client.class).toProvider(HttpClientProvider.class);
	}
}
