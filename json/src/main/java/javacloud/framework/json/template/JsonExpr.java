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
		
		public static final boolean isNullOrMissing(JsonNode node) {
			return node == null || node.isMissingNode() || node.isNull();
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
	
	// operator marker
	interface Operator extends JsonExpr {
		@Override
		default public JsonNode apply(JsonNode t) {
			return null;
		}
	}
	
	// a + b (number/string/object/array)
	class OpUnion implements Operator {
	}
	// a?b?c
	class OpOptional implements Operator {
	}
}
