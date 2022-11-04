package javacloud.framework.json.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * 1. branch or loop through input using expression and template
 * 2. resolve only if valid context
 */
public class JsonBranch implements JsonExpr {
	private final JsonExpr expr;
	private final JsonTemplate template;
	
	public JsonBranch(JsonExpr expr, JsonTemplate template) {
		this.expr = expr;
		this.template = template;
	}
	
	@Override
	public JsonNode apply(JsonNode input) {
		JsonNode context = expr.apply(input);
		if (JsonExpr.Constant.isNullOrMissing(context)) {
			return context;
		} else if (context.isArray()) {
			ArrayNode out = JsonNodeFactory.instance.arrayNode();
			context.forEach(n -> {
				// using child node
				JsonNode o = template.apply(n);
				if (!JsonExpr.Constant.isNullOrMissing(o)) {
					out.add(o);
				}
			});
			return out;
		}
		return template.apply(context);
	}
}