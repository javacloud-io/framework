package javacloud.framework.json.template;

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
	public void testNullable() throws Exception {
		JsonNode template = mapper.readTree(ResourceLoader.getClassLoader().getResourceAsStream("template.json"));
		JsonNode input = mapper.readTree(ResourceLoader.getClassLoader().getResourceAsStream("input.json"));
		JsonNode output = new JsonTemplate(template).apply(input);
		
		System.out.println(mapper.writeValueAsString(output));
		Assert.assertEquals("abcu123", output.at("/id").asText());
		Assert.assertTrue(output.at("/nullable").isMissingNode());
	}
}
