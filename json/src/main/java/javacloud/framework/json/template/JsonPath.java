package javacloud.framework.json.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Convenience implements JsonPath specification : https://goessner.net/articles/JsonPath
 * 
 * - DOT access: $.books
 * - INDEX access: $.books[0]
 * - RANGE access: $.books[0:5]
 * - AUTO flat out nested array
 */
public class JsonPath implements JsonExpr {
	private final Segment[] segments;
	/**
	 * 
	 * @param path
	 */
	public JsonPath(String path) {
		String[] tokens = path.split("\\.");	// segments[0] = $
		this.segments = new Segment[tokens.length];
		for (int i = 0; i < tokens.length; i ++) {
			this.segments[i] = compileExpr(tokens[i]);
		}
	}
	
	@Override
	public JsonNode apply(JsonNode input) {
		if (segments.length <= 1) {
			return input;
		}
		// descending from ROOT
		JsonNode out = input;
		for (int i = 1; i < segments.length; i ++) {
			out = resolve(out, segments[i]);
			if (out == null) {
				break;
			}
		}
		return (out == null? JsonNodeFactory.instance.missingNode() : out);
	}
	
	// $.[index] | $.name[index] | $.name[index:].property
	protected JsonNode resolve(JsonNode node, Segment segment) {
		// $.name[index:].property
		if (node.isArray()) {
			if (!segment.isEmpty()) {
				ArrayNode out = JsonNodeFactory.instance.arrayNode();
				node.forEach(n -> {
					JsonNode o = resolve(n, segment);
					if (!JsonExpr.Constant.isNullOrMissing(o)) {
						// flat out the array
						if (o.isArray() && (out.isEmpty() || !out.get(0).isArray())) {
							o.forEach(e -> out.add(e));
						} else {
							out.add(o);
						}
					}
				});
				return out;
			}
			// $.[index]
			return resolveSub(node, segment);
		} else if (node.isObject()) {
			// object.name[index] | object.[index]
			JsonNode v = segment.isEmpty() ? node : node.get(segment.name);
			return resolveSub(v, segment);
		}
		return null;
	}
	
	protected JsonNode resolveSub(JsonNode node, Segment segment) {
		if (node == null || segment.index == null) {
			return node;
		} else if (segment.end == null) {
			return segment.indexAt(node);
		}
		return segment.subList(node);
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
			} else if (d < 0) {
				// same as [:] if missing index
				return new Segment(name, index.isEmpty()? null : Integer.valueOf(index), null);
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
		
		boolean isEmpty() {
			return name.isEmpty();
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
