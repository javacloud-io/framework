package javacloud.framework.util;

import java.util.Map;

import javacloud.framework.io.PlistDocument;
import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public class PlistXmlTest extends TestCase {
	public void testPList() throws Exception {
		Map<String, Object> props = Objects.asMap();
		props.put("name1", "value1");
		props.put("name2", "value2");
		props.put("data", new byte[] {1, 2, 3, 4, 5, 6,7});
		props.put("null", null);	//ALOW NULL
		
		assertTrue(props.containsKey("null"));
		new PlistDocument(props).writeXml(System.out);
	}
}
