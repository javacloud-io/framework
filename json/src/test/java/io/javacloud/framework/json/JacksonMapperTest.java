package io.javacloud.framework.json;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javacloud.framework.cdi.test.ServiceTest;
import io.javacloud.framework.json.internal.JsonConverter;
import io.javacloud.framework.util.Dictionary;
import io.javacloud.framework.util.Externalizer;

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
		JsonConverter converter = new JsonConverter(externalizer);
		Dictionary dict = new Dictionary();
		Assert.assertEquals("{}", converter.toUTF8(dict));
		
		dict = (Dictionary)converter.toObject("{\"a\":123}", Map.class);
		Assert.assertEquals(123, (int)dict.get("a"));
		
		Object obj = converter.toObject("true", Object.class);
		Assert.assertEquals(Boolean.class, obj.getClass());
	}
	
	@Test
	public void testJson() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		JsonValue jvalue = converter.toObject("{\"a\":123}", JsonValue.class);
		Assert.assertEquals(JsonValue.Type.OBJECT, jvalue.type());
		Assert.assertEquals(Dictionary.class, jvalue.value().getClass());
	}
}
