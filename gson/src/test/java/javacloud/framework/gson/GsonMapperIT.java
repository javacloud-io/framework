package javacloud.framework.gson;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import io.grpc.examples.helloworld.HelloRequest;
import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.io.Externalizer;
import javacloud.framework.json.JsonConverter;
import javacloud.framework.util.Objects;

public class GsonMapperIT extends IntegrationTest {
	
	@Inject @Named(Externalizer.JSON)
	private Externalizer externalizer;
	
	@Inject @Named("proto")
	private Externalizer protonizer;
	
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
	public void testTime() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		Date now = new Date();
		Date later = converter.toObject(converter.toUTF8(now), Date.class);
		Assert.assertEquals(now.getTime() / 1000, later.getTime() / 1000);
	}
	
	@Test
	public void testGson() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		String s = converter.toUTF8(HelloRequest.newBuilder().setName("xxxx"));
		HelloRequest request = converter.toObject(s, HelloRequest.class);
		Assert.assertEquals("xxxx", request.getName());
	}
	
	@Test
	public void testProto() throws IOException {
		JsonConverter converter = new JsonConverter(protonizer);
		byte[] b = converter.toBytes(HelloRequest.newBuilder().setName("xxxx"));
		HelloRequest request = converter.toObject(b, HelloRequest.class);
		Assert.assertEquals("xxxx", request.getName());
	}
	
	@Test
	public void testDate() throws IOException {
		JsonConverter converter = new JsonConverter(externalizer);
		converter.toObject("\"2022-10-16T06:56:50.515Z\"", Date.class);
		converter.toObject("\"2022-10-16T06:56:50Z\"", Date.class);
		converter.toObject("\"2022-10-16 06:56:50\"", Date.class);
	}
}
