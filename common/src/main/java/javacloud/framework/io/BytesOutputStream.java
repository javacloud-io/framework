package javacloud.framework.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import javacloud.framework.util.Codecs;

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

	@Override
	public void close() throws IOException {
		flush();
		super.close();
	}
	
	/**
	 * Write string buffer
	 * 
	 * @param utf8
	 */
	public void write(String utf8) {
		byte[] buf = Codecs.toBytes(utf8);
		write(buf, 0, buf.length);
	}
}
