package com.appe.framework.io;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.appe.framework.util.Codecs;
/**
 * 
 * @author aimee
 *
 */
public class BytesInputStream extends ByteArrayInputStream {
	/**
	 * 
	 * @param buf
	 */
	public BytesInputStream(ByteBuffer buf) {
		this(buf.array(), buf.position(), buf.remaining());
	}
	
	/**
	 * 
	 * @param buf
	 */
	public BytesInputStream(byte[] buf) {
		super(buf);
	}
	
	/**
	 * 
	 * @param buf
	 * @param offset
	 * @param length
	 */
	public BytesInputStream(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}
	
	/**
	 * 
	 * @param bos
	 */
	public BytesInputStream(BytesOutputStream bos) {
		super(bos.byteArray(), 0, bos.count());
	}
	
	/**
	 * Assuming UTF8
	 * @param buf
	 * @throws UnsupportedEncodingException
	 */
	public BytesInputStream(String buf) throws UnsupportedEncodingException {
		super(buf == null? new byte[0] : Codecs.toBytes(buf));
	}
}
