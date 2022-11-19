package javacloud.framework.json.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javacloud.framework.io.BytesInputStream;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.ResourceLoader;

/**
 * Load and cache resources from class path
 *
 */
@Singleton
public class JsonTemplateFactory {
	private final ConcurrentMap<String, JsonTemplate> templates = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, JsonNode> nodes = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;
	
	@Inject
	public JsonTemplateFactory(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T nodeToValue(JsonNode node, Class<T> type) {
		try {
			if (JsonExpr.Constant.isNullOrMissing(node)) {
				return null;
			} else if (type.isAssignableFrom(String.class)) {
				return (T)objectMapper.writeValueAsString(node);
			} else if (type.isAssignableFrom(byte[].class)) {
				return (T)objectMapper.writeValueAsBytes(node);
			} else if (type.isAssignableFrom(InputStream.class)) {
				return (T)new BytesInputStream(objectMapper.writeValueAsBytes(node));
			} else if (type.isAssignableFrom(Reader.class)) {
				return (T)new StringReader(objectMapper.writeValueAsString(node));
			}
			return objectMapper.treeToValue(node, type);
		} catch (IOException ex) {
			throw InternalException.of(ex);
		}
	}
	
	public JsonNode valueToNode(Object value) {
		try {
			if (value instanceof String) {
				return objectMapper.readTree((String)value);
			} if (value instanceof byte[]) {
				return objectMapper.readTree((byte[])value);
			} else if (value instanceof InputStream) {
				return objectMapper.readTree((InputStream)value);
			} else if (value instanceof Reader) {
				return objectMapper.readTree((Reader)value);
			}
			return objectMapper.valueToTree(value);
		} catch (IOException ex) {
			throw InternalException.of(ex);
		}
	}
	
	public String valueToPrettyString(Object value) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(value);
		} catch (IOException ex) {
			throw InternalException.of(ex);
		}
	}
	
	public JsonNode getNode(String classpath) {
		return nodes.computeIfAbsent(classpath, (key) -> {
			try {
				InputStream ins = ResourceLoader.getClassLoader().getResourceAsStream(key);
				return objectMapper.readTree(ins);
			} catch (IOException ex) {
				throw InternalException.of(ex);
			}
		});
	}
	
	public JsonTemplate getTemplate(String classpath) {
		return templates.computeIfAbsent(classpath, (key) -> new JsonTemplate(getNode(key)));
	}
}
