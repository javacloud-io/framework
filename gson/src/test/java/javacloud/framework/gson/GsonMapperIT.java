package javacloud.framework.gson;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.io.Externalizer;
import javacloud.framework.json.JsonValue;
import javacloud.framework.json.internal.JsonConverter;
import javacloud.framework.util.Objects;

public class GsonMapperIT extends IntegrationTest {
	
	@Inject @Named(Externalizer.JSON)
	private Externalizer externalizer;
	
	@Test
	public void testDict() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		Map<String, Object> dict = Objects.asMap();
		Assert.assertEquals("{}", converter.toUTF8(dict));
		
		// GSON convert INT to double?
		dict = converter.toObject("{\"a\":123}", Map.class);
		Assert.assertEquals(123.0, dict.get("a"));
		
		Object obj = converter.toObject("true", Object.class);
		Assert.assertEquals(Boolean.class, obj.getClass());
	}
	
	@Test
	public void testJson() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		JsonValue jvalue = converter.toObject("{\"a\":123}", JsonValue.class);
		Assert.assertEquals(JsonValue.Type.OBJECT, jvalue.type());
	}
	
	@Test
	public void testTime() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		Date now = new Date();
		Date later = converter.toObject(converter.toUTF8(now), Date.class);
		Assert.assertEquals(now.getTime() / 1000, later.getTime() / 1000);
	}
}
