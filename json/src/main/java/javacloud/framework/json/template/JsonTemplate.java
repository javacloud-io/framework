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
		if (JsonExpr.Constant.isNullOrMissing(node)) {
			return node;
		} else if (node.isTextual()) {
			List<JsonExpr> segments = compileText(node.textValue());
			if (!segments.isEmpty()) {
				cache.put(node, segments.toArray(new JsonExpr[segments.size()]));
			}
		} else if (node.isObject() && node.hasNonNull("@")) {
			// loop context with @ element
			List<JsonExpr> segments = compileText(((ObjectNode)node).remove("@").textValue());
			JsonTemplate texpr = new JsonTemplate(node);
			cache.put(node, segments.stream()
									.map(e -> new JsonBranch(e, texpr))
									.toArray(JsonExpr[]::new));
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
		if (node.isArray()) {
			ArrayNode out = JsonNodeFactory.instance.arrayNode();
			node.forEach(n -> {
				JsonNode o = resolve(n, input);
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
		} else if (node.isObject()) {
			return resolveNode(node, input);
		}
		return resolveValue(node, input);
	}
	
	/**
	 * 
	 * @param node
	 * @param input
	 * @return
	 */
	protected JsonNode resolveNode(JsonNode node, JsonNode input) {
		JsonExpr[] segments = cache.get(node);
		if (segments == null || segments.length == 0) {
			ObjectNode out = JsonNodeFactory.instance.objectNode();
			node.fields().forEachRemaining(e -> {
				JsonNode o = resolve(e.getValue(), input);
				if (o != null && !o.isMissingNode()) {
					out.set(e.getKey(), o);
				}
			});
			return out;
		}
		// special loop
		return resolveExpr(0, segments, input);
	}
	
	// value -> value | object | array
	protected JsonNode resolveValue(JsonNode node, JsonNode input) {
		JsonExpr[] segments = cache.get(node);
		if (segments == null || segments.length == 0) {
			return node;
		}
		return resolveExpr(0, segments, input);
	}
	
	// evaluation simple expression {/123}{?|+}{/zzzz}
	protected JsonNode resolveExpr(int index, JsonExpr[] segments, JsonNode input) {
		if (index >= segments.length) {
			return null;
		}
		
		// just ignore operator
		JsonExpr expr = segments[index ++];
		if (expr instanceof JsonExpr.Operator) {
			return resolveExpr(index, segments, input);
		}
		
		// {left} {op} {right}
		JsonNode left = expr.apply(input);
		if (JsonExpr.Constant.isNullOrMissing(left)) {
			return resolveExpr(index, segments, input);
		}
		
		// look ahead & combine expression
		if (index < segments.length) {
			JsonExpr op = segments[index];
			if (op instanceof JsonExpr.OpOptional) {
				return left;
			}
			// recursive to remaining expr
			JsonNode right = resolveExpr(index, segments, input);
			if (JsonExpr.Constant.isNullOrMissing(right)) {
				return left;
			} else if (op instanceof JsonExpr.OpOptional) {
				return right;
			} else if (op instanceof JsonExpr.OpUnion) {
				// object/array/string/number union
				return unionExpr(left, right);
			} else {
				return concatExpr(left, right);
			}
		}
		// end of expression
		return left;
	}
	
	/**
	 * BREAK UP IN [....i j{ /a/b/c}k  ] to evaluate the expression
	 * BREAK UP IN [....i j{ $.a.b.c}k ] to evaluate the expression
	 * 
	 * @param text
	 * @return
	 */
	protected List<JsonExpr> compileText(String text) {
		List<JsonExpr> segments = new ArrayList<>();
		int len = text.length();
		int i = 0;
		while (i < len) {
			int j = i;
			for(; j < len; j ++) {
				char c = text.charAt(j);
				if(c == '{') {
					break;
				}
			}
			int k = j + 1;
			for (; k < len; k ++) {
				if(text.charAt(k) == '}') {
					break;
				}
			}
			
			// NOT FOUND {/} => LEAVE AS IS
			if (j >= len || k >= len) {
				segments.add(new JsonExpr.Constant(text.substring(i)));
				break;
			} else {
				// has padding
				if (j > i) {
					segments.add(new JsonExpr.Constant(text.substring(i, j)));
				}
				String expr = text.substring(j + 1, k).trim();
				segments.add(compileExpr(expr));
				
				//NEXT
				i = k + 1;
			}
		}
		return segments;
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	protected JsonExpr compileExpr(String expr) {
		if (expr.startsWith("/")) {
			return new JsonExpr.Pointer(expr);
		} else if (expr.startsWith("$")) {
			return new JsonPath(expr);
		} else if (expr.equals("+")) {
			return new JsonExpr.OpUnion();
		} else if (expr.equals("?")) {
			return new JsonExpr.OpOptional();
		} else {
			return new JsonExpr.Constant(expr);
		}
	}
	
	protected JsonNode concatExpr(JsonNode left, JsonNode right) {
		StringBuilder sb = new StringBuilder();
		if (left.isValueNode()) {
			sb.append(left.asText());
		} else {
			// JSON -> string
			sb.append(left.toString());
		}
		if (right.isValueNode()) {
			sb.append(right.asText());
		} else {
			// JSON -> string
			sb.append(right.toString());
		}
		return JsonNodeFactory.instance.textNode(sb.toString());
	}
	
	protected JsonNode unionExpr(JsonNode left, JsonNode right) {
		if (left.isArray() || right.isArray()) {
			ArrayNode out = JsonNodeFactory.instance.arrayNode();
			if (left.isArray()) {
				left.forEach(e -> out.add(e));
			} else {
				out.add(left);
			}
			if (right.isArray()) {
				right.forEach(e -> out.add(e));
			} else {
				out.add(right);
			}
			return out;
		} else if (left.isObject() && right.isObject()) {
			ObjectNode out = JsonNodeFactory.instance.objectNode();
			left.fields().forEachRemaining(e -> {
				out.set(e.getKey(), e.getValue());
			});
			right.fields().forEachRemaining(e -> {
				out.set(e.getKey(), e.getValue());
			});
			return out;
		} else if (left.isNumber() && right.isNumber()) {
			if (left.isIntegralNumber() && right.isIntegralNumber()) {
				return JsonNodeFactory.instance.numberNode(left.intValue() + right.intValue());
			}
			return JsonNodeFactory.instance.numberNode(left.doubleValue() + right.doubleValue());
		}
		return concatExpr(left, right);
	}
}
