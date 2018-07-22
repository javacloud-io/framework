package com.appe.framework.hk2;

import javax.inject.Inject;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Make sure jackson using custom object mapper.
 * 
 * @author tobi
 *
 */
public class JacksonJaxbProvider extends JacksonJaxbJsonProvider {
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
