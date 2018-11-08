package javacloud.framework.json.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import javacloud.framework.io.BytesInputStream;
import javacloud.framework.io.BytesOutputStream;
import javacloud.framework.io.Externalizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.Objects;

/**
 * Wrapper around externalization to provide simple use of bytes/string.
 * 
 * @author ho
 *
 */
public final class JsonConverter implements Externalizer {
	private Externalizer externalizer;
	public JsonConverter(Externalizer externalizer) {
		this.externalizer = externalizer;
	}
	
	/**
	 * 
	 */
	@Override
	public String type() {
		return externalizer.type();
	}
	
	/**
	 * 
	 */
	@Override
	public void marshal(Object v, OutputStream dst) throws IOException {
		externalizer.marshal(v, dst);
	}
	
	/**
	 * 
	 */
	@Override
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException {
		return externalizer.unmarshal(src, type);
	}
	
	/**
	 * 
	 * @param v
	 * @return
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
	 * @return
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
	 * @return
	 * @throws IOException
	 */
	public <T> T toObject(byte[] buf, Class<?> type) throws IOException {
		return unmarshal(new BytesInputStream(buf), type);
	}
	
	/**
	 * 
	 * @param utf8
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T toObject(String utf8, Class<?> type) throws IOException {
		return unmarshal(new BytesInputStream(utf8), type);
	}
	
	/**
	 * return converter for TYPE
	 * @param type
	 * @return
	 */
	public <T> Function<Object, T> toConverter(final Class<T> type) {
		return new Function<Object, T>() {
			@Override
			public T apply(Object value) {
				try {
					if(type.isInstance(value)) {
						return Objects.cast(value);
					}
					if(value instanceof String) {
						return toObject((String)value, type);
					}
					if(value instanceof byte[]) {
						return toObject((byte[])value, type);
					}
					return (toObject(toBytes(value), type));
				} catch(IOException ex) {
					throw Exceptions.asUnchecked(ex);
				}
			}
		};
	}
}
