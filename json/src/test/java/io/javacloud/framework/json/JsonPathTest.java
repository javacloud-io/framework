package io.javacloud.framework.json;

import org.junit.Assert;
import org.junit.Test;

import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.json.internal.JsonTemplate;
import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import junit.framework.TestCase;

/**
 * 
 * @author ho
 *
 */
public class JsonPathTest extends TestCase {
	@Test
	public void testBasic() {
		Dictionary dict = Dictionaries.asDict("a", "b", "c", "d");
		JsonPath jsonPath = new JsonPath(dict);
		Assert.assertEquals("b", jsonPath.select("$.a"));
		Assert.assertEquals("d", jsonPath.select("$.c"));
		
		jsonPath.merge("$.x.y", Dictionaries.asDict("u", "v"));
		Assert.assertEquals("v", jsonPath.select("$.x.y.u"));
		
		dict = jsonPath.select("$.x");
		jsonPath = new JsonPath(dict);
		Assert.assertEquals("v", jsonPath.select("$.y.u"));
	}
	
	@Test
	public void testTemplate() {
		Dictionary dict = Dictionaries.asDict("a", "b", "c", "d");
		JsonTemplate template = new JsonTemplate(new JsonPath(dict));
		
		Object v = template.compile("{$.a}XYZ");
		Assert.assertEquals("bXYZ", v);
		
		v = template.compile("{$.a}{$.c}");
		Assert.assertEquals("bd", v);
	}
}