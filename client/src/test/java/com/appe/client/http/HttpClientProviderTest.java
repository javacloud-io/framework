package com.appe.client.http;

import org.junit.Test;

import com.appe.client.http.HttpClientProvider;

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
