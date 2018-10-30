package io.javacloud.framework.json.internal;

import java.util.List;
import java.util.Map;

import io.javacloud.framework.util.Converters;
import io.javacloud.framework.util.Objects;

/**
 * Compile the json template value using jsonPath substitution
 * 
 * @author ho
 *
 */
public class JsonTemplate {
	private final JsonPath jsonPath;
	
	/**
	 * Template from root object
	 * @param root
	 */
	public JsonTemplate(Object root) {
		if(root instanceof JsonPath) {
			this.jsonPath = (JsonPath)root;
		} else {
			this.jsonPath = new JsonPath(root);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public <V, T> T compile(V value) {
		if(value == null) {
			return null;
		}
		return Objects.cast(eval(value));
	}
	
	/**
	 * FIXME: Add support for POJO type
	 * 
	 * @param expr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object eval(Object expr) {
		if(expr instanceof String) {
			return evalString((String)expr);
		} else if(expr instanceof Map) {
			return evalMap((Map<String, Object>)expr);
		} else if(expr instanceof List) {
			return evalList((List<Object>)expr);
		}
		return expr;
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	protected Map<?, ?> evalMap(Map<String, Object> expr) {
		for(String key: expr.keySet()) {
			expr.put(key, eval(expr.get(key)));
		}
		return expr;
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	protected List<?> evalList(List<Object> expr) {
		for(int i = 0; i < expr.size(); i ++) {
			expr.set(i, eval(expr.get(i)));
		}
		return expr;
	}
	
	/**
	 * Evaluate the expression using {$.path}ZZZ
	 * 
	 * @param value
	 * @return
	 */
	protected Object evalString(String expr) {
		if(Objects.isEmpty(expr)) {
			return expr;
		} else if(JsonPath.is(expr)) {
			return jsonPath.select(expr);
		}
		
		//BREAK UP IN [....i s{ j$ }k  ] to evaluate the expression
		StringBuilder sb = new StringBuilder();
		int len = expr.length();
		int i = 0;
		while(i < len) {
			int s = -1;
			int j = i;
			for(; j < len; j ++) {
				char c = expr.charAt(j);
				if(c == '{') {
					s = j;
				} else if(c == '$') {
					break;
				}
			}
			int k = j;
			for(; k < len; k ++) {
				if(expr.charAt(k) == '}') {
					break;
				}
			}
			//NOT FOUND {$} => LEAVE AS IS
			if(s < 0 || j >= len || k >= len) {
				sb.append(expr.substring(i));
				break;
			} else {
				String path = expr.substring(j, k).trim();
				sb.append(expr.substring(i, s));
				
				Object value = jsonPath.select(path);
				sb.append(Converters.toString(value));
				
				//NEXT
				i = k + 1;
			}
		}
		return sb.toString();
	}
}
