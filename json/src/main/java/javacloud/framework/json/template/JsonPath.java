package javacloud.framework.json.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Simple implements JsonPath specification : https://goessner.net/articles/JsonPath
 * 
 * -DOT access: $.books
 * -INDEX access: $.books[0]
 * -RANGE access: $.books[0:5]
 *
 */
public class JsonPath implements JsonExpr {
	private final Segment[] segments;
	/**
	 * 
	 * @param path
	 */
	public JsonPath(String path) {
		String[] tokens = path.split("\\.");	//segments[0] = $
		this.segments = new Segment[tokens.length];
		for (int i = 0; i < tokens.length; i ++) {
			this.segments[i] = compileExpr(tokens[i]);
		}
	}
	
	@Override
	public JsonNode apply(JsonNode t) {
		if (segments.length <= 1) {
			return t;
		}
		
		JsonNode out = t;
		for (int i = 1; i < segments.length; i ++) {
			out = resolve(out, segments[i]);
			if (out == null) {
				break;
			}
		}
		return (out == null? JsonNodeFactory.instance.missingNode() : out);
	}
	
	protected JsonNode resolve(JsonNode node, Segment segment) {
		JsonNode v = node.get(segment.name);
		if (segment.index == null) {
			return v;
		} else if (segment.end == null) {
			return segment.indexAt(v);
		}
		return segment.subList(v);
	}
	
	
	protected Segment compileExpr(String segment) {
		int s = segment.indexOf('[');
		if (s < 0) {
			return new Segment(segment.trim(), null, null);
		} else {
			String name = segment.substring(0, s).trim();
			int e = segment.indexOf(']', s);
			if (e < 0) {
				throw new IllegalArgumentException("index missing ]");
			}
			
			String index = segment.substring(s + 1, e).trim();
			int d = index.indexOf(':');
			if (d == 0 && index.length() <= 1) {
				return new Segment(name, null, null);
			}
			// single index
			if (d < 0) {
				return new Segment(name, Integer.valueOf(index), null);
			} else if (d == 0) {	// range index
				// sub[:e]
				return new Segment(name, 0, Integer.valueOf(index.substring(1).trim()));
			} else if (d == index.length() - 1) {
				// sub[s:]
				return new Segment(name, Integer.valueOf(index.substring(0, d).trim()), Integer.MAX_VALUE);
			} else {
				// sub[s:e]
				return new Segment(name, Integer.valueOf(index.substring(0, d).trim()),
										 Integer.valueOf(index.substring(d + 1).trim()));
			}
		}
	}
	
	// object segment + range index
	static class Segment {
		final String name;
		final Integer index, end;
		
		Segment(String name, Integer index, Integer end) {
			this.name = name;
			this.index = index;
			this.end = end;
		}
		
		JsonNode indexAt(JsonNode node) {
			int i = (index < 0 ? index + node.size() : index);
			return node.get(i);
		}
		
		JsonNode subList(JsonNode node) {
			int len = node.size();
			int i = (index < 0 ? index + len : index);
			int j = (end < 0 ? end + len : (end > len ? len: end));
			if (i >= j) {
				return JsonNodeFactory.instance.missingNode();
			} else if (i == 0 && j == len) {
				return node;
			}
			
			ArrayNode subs = JsonNodeFactory.instance.arrayNode();
			while (i < j) {
				subs.add(node.get(i ++));
			}
			return subs;
		}
	}
}
