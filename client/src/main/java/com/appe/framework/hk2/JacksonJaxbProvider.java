package com.appe.framework.hk2;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Make sure jackson using custom object mapper.
 * 
 * @author tobi
 *
 */
public class JacksonJaxbProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider {
	/**
	 * Inject from registry
	 * 
	 * @param objectMapper
	 */
	@Inject
	public JacksonJaxbProvider(ObjectMapper objectMapper) {
		super(objectMapper, DEFAULT_ANNOTATIONS);
	}
}
