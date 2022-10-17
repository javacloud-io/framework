package javacloud.framework.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javacloud.framework.io.BytesInputStream;
import javacloud.framework.io.BytesOutputStream;
import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.InternalException;
import javacloud.framework.util.Objects;

/**
 * Wrapper around externalization to provide simple use of bytes/string.
 * 
 * @author ho
 *
 */
@Singleton
public final class JsonConverter implements Externalizer {
	private final Externalizer externalizer;
	
	@Inject
	public JsonConverter(@Named(Externalizer.JSON) Externalizer externalizer) {
		this.externalizer = externalizer;
	}
	
	/**
	 * Type of converter
	 */
	@Override
	public String type() {
		return externalizer.type();
	}
	
	/**
	 * serialize object to stream
	 */
	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		externalizer.marshal(v, dst);
	}
	
	/**
	 * @return object from stream 
	 */
	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		return externalizer.unmarshal(src, type);
	}
	
	/**
	 * 
	 * @param v
	 * @return byte from object
	 * @throws IOException
	 */
	public byte[] toBytes(Object v) throws IOException {
		BytesOutputStream buf = new BytesOutputStream();
		externalizer.marshal(v, buf);
		return buf.toByteArray();
	}
	
	/**
	 * 
	 * @param v
	 * @return string from object
	 * @throws IOException
	 */
	public String toUTF8(Object v) throws IOException {
		BytesOutputStream buf = new BytesOutputStream();
		externalizer.marshal(v, buf);
		return Codecs.toUTF8(buf.byteArray(), 0, buf.count());
	}
	
	/**
	 * 
	 * @param buf
	 * @param type
	 * @return object from byte buffer
	 * @throws IOException
	 */
	public <T> T toObject(byte[] buf, Class<?> type) throws IOException {
		if (buf == null) {
			return null;
		}
		//SUPPORT EMPTY ARRAY
		if (buf.length == 0 && type.isArray()) {
			return Objects.cast(Array.newInstance(type.getComponentType(), 0));
		}
		return unmarshal(new BytesInputStream(buf), type);
	}
	
	/**
	 * return object from string
	 * @param utf8
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T toObject(String utf8, Class<?> type) throws IOException {
		if (utf8 == null) {
			return null;
		}
		//SUPPORT EMPTY ARRAY
		if (utf8.isEmpty() && type.isArray()) {
			return Objects.cast(Array.newInstance(type.getComponentType(), 0));
		}
		return unmarshal(new BytesInputStream(utf8), type);
	}
	
	/**
	 * 
	 * @param type
	 * @return converter for TYPE
	 */
	public <T> Function<Object, T> to(final Class<T> type) {
		return new Function<Object, T>() {
			@Override
			public T apply(Object value) {
				try {
					if (type.isInstance(value)) {
						return Objects.cast(value);
					}
					if (value instanceof String) {
						return toObject((String)value, type);
					}
					if (value instanceof byte[]) {
						return toObject((byte[])value, type);
					}
					return (toObject(toBytes(value), type));
				} catch(IOException ex) {
					throw InternalException.of(ex);
				}
			}
		};
	}
}
