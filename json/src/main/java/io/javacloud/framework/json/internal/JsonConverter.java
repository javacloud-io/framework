package io.javacloud.framework.json.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.javacloud.framework.util.BytesInputStream;
import io.javacloud.framework.util.BytesOutputStream;
import io.javacloud.framework.util.Codecs;
import io.javacloud.framework.util.Externalizer;
import io.javacloud.framework.util.Objects;

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
	public <T> T unmarshal(InputStream src, Class<T> type) throws IOException {
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
	public <T> T toObject(byte[] buf, Class<T> type) throws IOException {
		return externalizer.unmarshal(new BytesInputStream(buf), type);
	}
	
	/**
	 * 
	 * @param utf8
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T toObject(String utf8, Class<T> type) throws IOException {
		return externalizer.unmarshal(new BytesInputStream(utf8), type);
	}
	
	/**
	 * Convert an object to other
	 * @param v
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T convert(Object v, Class<T> type) throws IOException {
		if(type.isInstance(v)) {
			return Objects.cast(v);
		}
		if(v instanceof String) {
			return toObject((String)v, type);
		}
		if(v instanceof byte[]) {
			return toObject((byte[])v, type);
		}
		return (toObject(toBytes(v), type));
	}
}
