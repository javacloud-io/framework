package com.appe.jaxrs.http;

import org.junit.Test;

import com.appe.jaxrs.http.HttpClientProvider;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class HttpClientProviderTest extends TestCase {
	@Test
	public void testGet() {
		new HttpClientProvider().get();
	}
}
