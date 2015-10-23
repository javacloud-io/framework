package com.appe.jaxrs.internal;

import javax.ws.rs.client.Client;

import com.appe.jaxrs.http.HttpClientProvider;
import com.appe.registry.internal.GuiceModule;
/**
 * 
 * @author ho
 *
 */
public class ClientModule extends GuiceModule {
	@Override
	protected void configure() {
		bind(Client.class).toProvider(HttpClientProvider.class);
	}
}
