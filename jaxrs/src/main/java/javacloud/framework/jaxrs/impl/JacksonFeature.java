package javacloud.framework.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.CommonProperties;

import javacloud.framework.jaxrs.internal.JacksonJaxbProvider;
/**
 * Make sure to always using customized version of JACKSON.
 * 
 * @author ho
 *
 */
public class JacksonFeature implements Feature {
	public JacksonFeature() {
		
	}
	@Override
	public boolean configure(FeatureContext context) {
		context.property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true)
			   .register(JacksonJaxbProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
		return true;
	}
}
