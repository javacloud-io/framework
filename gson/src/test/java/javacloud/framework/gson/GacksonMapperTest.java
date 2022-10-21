package javacloud.framework.gson;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import io.grpc.examples.helloworld.HelloRequest;
import javacloud.framework.gson.impl.GacksonMapper;
import javacloud.framework.gson.impl.GsonMapper;
import javacloud.framework.gson.internal.JsonExternalizer;
import javacloud.framework.json.JsonConverter;

import org.junit.Assert;

public class GacksonMapperTest {
	@Test
	public void testJson() throws IOException {
		JsonExternalizer externalizer = new JsonExternalizer();
		String s = externalizer.toUTF8(HelloRequest.newBuilder().setName("xxxx"));
		HelloRequest request = externalizer.toMessage(new StringReader(s), HelloRequest.class);
		Assert.assertEquals("xxxx", request.getName());
	}
	
	@Test
	public void testGson() throws IOException {
		JsonConverter converter = new JsonConverter(new GsonMapper());
		String s = converter.toUTF8(HelloRequest.newBuilder().setName("xxxx"));
		HelloRequest request = converter.toObject(s, HelloRequest.class);
		Assert.assertEquals("xxxx", request.getName());
	}
	
	@Test
	public void testJackson() throws IOException {
		JsonConverter converter = new JsonConverter(new GacksonMapper());
		String s = converter.toUTF8(HelloRequest.newBuilder().setName("xxxx"));
		HelloRequest request = converter.toObject(s, HelloRequest.class);
		Assert.assertEquals("xxxx", request.getName());
	}
}
