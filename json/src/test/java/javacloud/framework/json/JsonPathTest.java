package javacloud.framework.json;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import javacloud.framework.json.internal.JsonPath;
import javacloud.framework.json.internal.JsonTemplate;
import javacloud.framework.util.Objects;
import junit.framework.TestCase;

/**
 * 
 * @author ho
 *
 */
public class JsonPathTest extends TestCase {
	@Test
	public void testBasic() {
		Map<String, Object> dict = Objects.asMap("a", "b", "c", "d", "r", Objects.asList(1, 2, 3 ,4), "ar", new Object[] {"a", "b", "c"});
		JsonPath jsonPath = new JsonPath(dict);
		Assert.assertEquals("b", jsonPath.select("$.a"));
		Assert.assertEquals("d", jsonPath.select("$.c"));
		
		Assert.assertEquals(1, (int)jsonPath.select("$.r[0]"));
		Assert.assertEquals(4, (int)jsonPath.select("$.r[-1]"));
		
		jsonPath.merge("$.x.y", Objects.asMap("u", "v"));
		Assert.assertEquals("v", jsonPath.select("$.x.y.u"));
		
		dict = jsonPath.select("$.x");
		jsonPath = new JsonPath(dict);
		Assert.assertEquals("v", jsonPath.select("$.y.u"));
	}
	
	@Test
	public void testTemplate() {
		Map<String, Object> dict = Objects.asMap("a", "b", "c", "d");
		JsonTemplate template = new JsonTemplate(new JsonPath(dict));
		
		Object v = template.compile("{$.a}XYZ");
		Assert.assertEquals("bXYZ", v);
		
		v = template.compile("{$.a}{$.c}");
		Assert.assertEquals("bd", v);
	}
}
