package io.javacloud.framework.json;

import org.junit.Assert;

import io.javacloud.framework.json.internal.JsonPath;
import io.javacloud.framework.util.Dictionaries;
import io.javacloud.framework.util.Dictionary;
import junit.framework.TestCase;

/**
 * 
 * @author ho
 *
 */
public class JsonPathTest extends TestCase {
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
}
