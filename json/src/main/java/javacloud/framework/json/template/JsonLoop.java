package javacloud.framework.json.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * loop through input using expression and child template
 */
public class JsonLoop implements JsonExpr {
	private final JsonExpr expr;
	private final JsonTemplate template;
	
	public JsonLoop(JsonExpr expr, JsonTemplate template) {
		this.expr = expr;
		this.template = template;
	}
	
	@Override
	public JsonNode apply(JsonNode input) {
		JsonNode context = expr.apply(input);
		if (context.isArray()) {
			ArrayNode out = JsonNodeFactory.instance.arrayNode();
			context.forEach(n -> {
				// using child node
				JsonNode o = template.apply(n);
				if (!JsonPath.isNullOrMissing(o)) {
					out.add(o);
				}
			});
			return out;
		}
		return template.apply(context);
	}
}