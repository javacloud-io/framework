package com.appe.framework.impl;

import javax.ws.rs.client.Client;

import com.appe.framework.client.HttpClientProvider;
import com.appe.framework.databind.JacksonMapper;
import com.appe.framework.internal.GuiceModule;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Register all the basic module
 * 
 * @author ho
 *
 */
public class ClientModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(ObjectMapper.class).to(JacksonMapper.class);
		bind(Client.class).toProvider(HttpClientProvider.class);
	}
}
