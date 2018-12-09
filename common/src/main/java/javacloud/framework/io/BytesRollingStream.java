package javacloud.framework.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Keep the last size bytes in the stream only!
 * 
 * @author ho
 *
 */
public class BytesRollingStream extends BytesOutputStream {
	private boolean rolled = false;
	public BytesRollingStream(int size) {
		super(size);
	}
	
	@Override
	public synchronized void write(int b) {
		if(count >= buf.length) {
			count  = 0;
			rolled = true;
		}
		buf[count] = (byte) b;
        count += 1;
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		for(int i = 0; i < len; i ++) {
			write(b[i]);
		}
	}

	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		flush();
		super.writeTo(out);
	}
	
	//CORRECTLY SHIFT THE COUNT
	@Override
	public void flush() throws IOException {
		if(rolled && count < buf.length) {
			byte[] newbuf = new byte[buf.length];
			System.arraycopy(buf, count, newbuf, 0, buf.length - count);
			System.arraycopy(buf, 0, newbuf, buf.length - count, count);
			buf = newbuf;
			count = buf.length;
		}
	}
}
