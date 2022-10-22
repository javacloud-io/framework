package javacloud.framework.json.template;

import java.util.function.Function;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public interface JsonExpr extends Function<JsonNode, JsonNode> {
	class Constant implements JsonExpr {
		private final JsonNode value;
		
		public Constant(String value) {
			this(JsonNodeFactory.instance.textNode(value));
		}
		
		public Constant(JsonNode value) {
			this.value = value;
		}
		
		@Override
		public JsonNode apply(JsonNode input) {
			return value;
		}
	}
	
	class Pointer implements JsonExpr {
		private final JsonPointer ptr;
		
		public Pointer(String expr) {
			this(JsonPointer.compile(expr));
		}
		
		public Pointer(JsonPointer ptr) {
			this.ptr = ptr;
		}
		
		@Override
		public JsonNode apply(JsonNode input) {
			return input.at(ptr);
		}
	}
}
