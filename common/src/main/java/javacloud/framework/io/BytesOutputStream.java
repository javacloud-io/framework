package javacloud.framework.io;

import java.nio.ByteBuffer;

/**
 * Extended to allow direct access to the buffer.
 * @author aimee
 *
 */
public class BytesOutputStream extends java.io.ByteArrayOutputStream {
	public BytesOutputStream() {
		this(64);
	}
	
	/**
	 * 
	 * @param size
	 */
	public BytesOutputStream(int size) {
		super(size);
	}
	
	/**
	 * return current byte count.
	 * @return
	 */
	public int count() {
		return	count;
	}
	
	/**
	 * return the current internal buffer.
	 * @return
	 */
	public byte[] byteArray() {
		return buf;
	}
	
	/**
	 * Return the byte buffer!
	 * @return
	 */
	public ByteBuffer byteBuffer() {
		return ByteBuffer.wrap(buf, 0, count);
	}
}
