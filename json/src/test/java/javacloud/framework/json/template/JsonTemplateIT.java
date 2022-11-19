package javacloud.framework.json.template;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javacloud.framework.cdi.internal.IntegrationTest;

public class JsonTemplateIT extends IntegrationTest {
	@Inject
	ObjectMapper mapper;
	
	@Test
	public void testResolve() {
		JsonTemplateFactory factory = new JsonTemplateFactory(mapper);
		
		JsonTemplate template = factory.getTemplate("templates/template.json");
		JsonNode input = factory.getNode("templates/input.json");
		JsonNode output = template.apply(input);
		System.out.println(factory.nodeToPrettyString(output));
		
		Assert.assertEquals("abcu123-60", output.at("/id").asText());
		Assert.assertTrue(JsonExpr.Constant.isNullOrMissing(output.at("/nullable")));
		Assert.assertTrue(output.at("/zlist").isArray());
		Assert.assertTrue(output.at("/vlist").isArray());
		Assert.assertTrue(output.at("/vobject").isObject());
		Assert.assertTrue(output.at("/zapp").isObject());
		Assert.assertTrue(output.at("/zinput").isTextual());
	}
	
	@Test
	public void testConversion() {
		JsonTemplateFactory factory = new JsonTemplateFactory(mapper);
		JsonNode input = factory.getNode("templates/input.json");
		byte[] s = factory.nodeToValue(input, byte[].class);
		input = factory.valueToNode(s);
	}
}
