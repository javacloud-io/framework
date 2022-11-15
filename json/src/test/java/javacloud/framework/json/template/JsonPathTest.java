package javacloud.framework.json.template;

import org.junit.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.TestCase;


public class JsonPathTest extends TestCase {
	public void testIndex() {
		JsonPath path = new JsonPath("$.list[0]");
		ObjectNode input = jsonObject();
		Assert.assertEquals("1", path.apply(input).textValue());
	}
	
	public void testIndexLast() {
		JsonPath path = new JsonPath("$.list[-1]");
		ObjectNode input = jsonObject();
		Assert.assertEquals("4", path.apply(input).textValue());
	}
	
	public void testRange() {
		JsonPath path = new JsonPath("$.list[0:]");
		ObjectNode input = jsonObject();
		Assert.assertEquals(4, path.apply(input).size());
	}
	
	public void testRangeLast() {
		JsonPath path = new JsonPath("$.list[-1:]");
		ObjectNode input = jsonObject();
		Assert.assertEquals(1, path.apply(input).size());
	}
	
	public void testMissingName() {
		JsonPath path = new JsonPath("$.[2]");
		JsonNode input = jsonArray("1", "2", "3", "4");
		Assert.assertEquals("3", path.apply(input).textValue());
	}
	
	public void testSelf() {
		JsonPath path = new JsonPath("$.list[]");
		ObjectNode input = jsonObject();
		Assert.assertEquals(4, path.apply(input).size());
	}
	
	public void testRangeObject() {
		JsonPath path = new JsonPath("$.olist[:].a.[1]");
		ObjectNode input = jsonObject();
		Assert.assertEquals("2", path.apply(input).textValue());
	}
	
	public void testFlatList() {
		JsonPath path = new JsonPath("$.llist.a");
		ObjectNode input = jsonObject();
		Assert.assertEquals(6, path.apply(input).size());
	}
	
	static JsonNode jsonArray(String...values) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode();
		for( String v: values) {
			node.add(v);
		}
		return node;
	}
	
	static ObjectNode jsonKv(String key, String value) {
		ObjectNode input = JsonNodeFactory.instance.objectNode();
		input.set(key, JsonNodeFactory.instance.textNode(value));
		return input;
	}
	
	static ObjectNode jsonObject() {
		ObjectNode input = JsonNodeFactory.instance.objectNode();
		input.set("list", jsonArray("1", "2", "3", "4"));
		
		ArrayNode olist = JsonNodeFactory.instance.arrayNode();
		olist.add(jsonKv("a", "1"));
		olist.add(jsonKv("a", "2"));
		olist.add(jsonKv("a", "3"));
		input.set("olist", olist);
		
		// [[]]
		ArrayNode llist = JsonNodeFactory.instance.arrayNode();
		llist.add(olist);
		llist.add(olist);
		input.set("llist", llist);
		return input;
	}
}

