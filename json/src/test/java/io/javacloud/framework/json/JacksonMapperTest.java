package io.javacloud.framework.json;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javacloud.framework.cdi.ServiceTest;
import io.javacloud.framework.data.Dictionary;
import io.javacloud.framework.data.Externalizer;
import io.javacloud.framework.json.internal.JacksonConverter;

/**
 * 
 * @author ho
 *
 */
public class JacksonMapperTest extends ServiceTest {
	@Inject
	private ObjectMapper objectMapper;
	
	@Inject @Named(Externalizer.JSON)
	private Externalizer externalizer;
	
	@Test
	public void testDict() throws IOException {
		JacksonConverter converter = new JacksonConverter(externalizer);
		Dictionary dict = new Dictionary();
		Assert.assertEquals("{}", converter.toUTF8(dict));
	}
}
