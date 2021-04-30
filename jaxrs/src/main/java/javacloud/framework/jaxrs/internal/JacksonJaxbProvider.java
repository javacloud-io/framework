package javacloud.framework.jaxrs.internal;

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
	@jakarta.inject.Inject
	public JacksonJaxbProvider(ObjectMapper objectMapper) {
		super(objectMapper, DEFAULT_ANNOTATIONS);
	}
}
