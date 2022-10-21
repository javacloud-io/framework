package javacloud.framework.json;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class JacksonMapperIT extends IntegrationTest {
	@Inject
	private ObjectMapper objectMapper;
	
	@Inject
	private JsonConverter converter;
	
	@Test
	public void testNull() throws IOException {
		Map<String, Object> m = Objects.asMap("a", "1", "b", null, "c", "3", "t", new Date());
		objectMapper.writeValue(System.out, m);
	}
	
	@Test
	public void testDict() throws IOException {
		Map<String, Object> dict = Objects.asMap();
		Assert.assertEquals("{}", converter.toUTF8(dict));
		
		dict = converter.toObject("{\"a\":123}", Map.class);
		Assert.assertEquals(123, dict.get("a"));
		
		Object obj = converter.toObject("true", Object.class);
		Assert.assertEquals(Boolean.class, obj.getClass());
	}
	
	@Test
	public void testDate() throws IOException {
		converter.toObject("\"2022-10-16T06:56:50.515Z\"", Date.class);
		converter.toObject("\"2022-10-16T06:56:50Z\"", Date.class);
		converter.toObject("\"2022-10-16 06:56:50\"", Date.class);
	}
}
