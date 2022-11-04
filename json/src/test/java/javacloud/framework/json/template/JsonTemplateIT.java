package javacloud.framework.json.template;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javacloud.framework.cdi.internal.IntegrationTest;
import javacloud.framework.util.ResourceLoader;

public class JsonTemplateIT extends IntegrationTest {
	@Inject
	ObjectMapper mapper;
	
	@Test
	public void testResolve() throws Exception {
		JsonNode template = jsonNode("template");
		JsonNode input = jsonNode("input");
		JsonNode output = new JsonTemplate(template).apply(input);
		System.out.println(mapper.writeValueAsString(output));
		
		Assert.assertEquals("abcu123-60", output.at("/id").asText());
		Assert.assertTrue(JsonExpr.Constant.isNullOrMissing(output.at("/nullable")));
		Assert.assertTrue(output.at("/zlist").isArray());
		Assert.assertTrue(output.at("/vlist").isArray());
		Assert.assertTrue(output.at("/vobject").isObject());
		Assert.assertTrue(output.at("/zapp").isObject());
	}
	
	JsonNode jsonNode(String name) throws IOException {
		return mapper.readTree(ResourceLoader.getClassLoader().getResourceAsStream("templates/" + name + ".json"));
	}
}
