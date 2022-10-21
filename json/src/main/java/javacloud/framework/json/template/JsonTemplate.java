package javacloud.framework.json.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Using JsonPointer /a/b... as selector:
 * https://www.rfc-editor.org/rfc/rfc6901.html
 * 
 * Usage:
 * Template -> JsonNode -> apply(root) -> JsonNode -> Object
 *
 */
public class JsonTemplate implements JsonExpr {
	private final JsonNode template;
	private final Map<JsonNode, JsonExpr[]> cache = new HashMap<>();
	
	public JsonTemplate(JsonNode template) {
		this.template = compile(template);
	}
	
	@Override
	public JsonNode apply(JsonNode input) {
		return resolve(template, input);
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	protected JsonNode compile(JsonNode node) {
		if (node.isTextual() && !node.isNull()) {
			List<JsonExpr> segments = compileText(node.textValue());
			if (!segments.isEmpty()) {
				cache.put(node, segments.toArray(new JsonExpr[segments.size()]));
			}
		} else {
			node.forEach(n -> compile(n));
		}
		return node;
	}
	
	/**
	 * 
	 * @param node
	 * @param input
	 * @return
	 */
	protected JsonNode resolve(JsonNode node, JsonNode input) {
		if (node.isObject()) {
			ObjectNode out = JsonNodeFactory.instance.objectNode();
			node.fields().forEachRemaining(e -> {
				JsonNode o = resolve(e.getValue(), input);
				if (o != null && !o.isMissingNode()) {
					out.set(e.getKey(), o);
				}
			});
			return out;
		} else if (node.isArray()) {
			ArrayNode out = JsonNodeFactory.instance.arrayNode();
			node.forEach(n -> {
				JsonNode o = resolve(n, input);
				if (o != null && !o.isMissingNode()) {
					out.add(o);
				}
			});
			return out;
		}
		
		// other types
		JsonExpr[] segments = cache.get(node);
		if (segments == null || segments.length == 0) {
			return node;
		} else if (segments.length == 1) {
			// single pointer
			return segments[0].apply(input);
		}
		
		// text node
		StringBuilder sb = new StringBuilder();
		for (JsonExpr segment: segments) {
			String text = segment.apply(input).textValue();
			if (text != null) {
				sb.append(text);
			}
		}
		return JsonNodeFactory.instance.textNode(sb.toString());
	}
	
	protected List<JsonExpr> compileText(String text) {
		List<JsonExpr> segments = new ArrayList<>();
		int len = text.length();
		int i = 0;
		while (i < len) {
			int s = -1;
			int j = i;
			for(; j < len; j ++) {
				char c = text.charAt(j);
				if(c == '{') {
					s = j;
				} else if(c == '/' || c == '$') {
					break;
				}
			}
			int k = j;
			for (; k < len; k ++) {
				if(text.charAt(k) == '}') {
					break;
				}
			}
			//NOT FOUND {/} => LEAVE AS IS
			if (s < 0 || j >= len || k >= len) {
				segments.add(new JsonExpr.Constant(text.substring(i)));
				break;
			} else {
				String expr = text.substring(j, k).trim();
				if (s > i) {	//padding
					segments.add(new JsonExpr.Constant(text.substring(i, s)));
				}
				segments.add(compileExpr(expr));
				
				//NEXT
				i = k + 1;
			}
		}
		return segments;
	}
	
	/**
	 * BREAK UP IN [....i s{ j/a/b/c}k  ] to evaluate the expression
	 * BREAK UP IN [....i s{ j$.a.b.c}k ] to evaluate the expression
	 * 
	 * @param expr
	 * @return
	 */
	protected JsonExpr compileExpr(String expr) {
		if (expr.startsWith("/")) {
			return new JsonExpr.Pointer(expr);
		} else if (expr.startsWith("$")) {
			return new JsonPath(expr);
		} else {
			return new JsonExpr.Constant(expr);
		}
	}
}
