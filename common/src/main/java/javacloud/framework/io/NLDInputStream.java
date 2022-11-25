package javacloud.framework.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class NLDInputStream extends BufferedInputStream {
	private int offset = 0;	// line offset
	private int total  = 0;	// total read
	
	public NLDInputStream(InputStream in) {
		super(in);
	}
	
	public ByteBuffer nextLine() throws IOException {
		return nextLine(true);
	}
	
	public ByteBuffer nextLine(boolean expectEOL) throws IOException {
		try (BytesOutputStream buf = new BytesOutputStream()) {
			boolean eol = false;
			while (!eol) {
				int b = read();
				if (b < 0) {
					total = offset + buf.count();
					break;
				} else if (b == '\n') {
					buf.flush();
					offset += buf.count() + 1;
					total = offset;
					eol = true;
				} else {
					buf.write(b);
				}
			}
			if (!expectEOL || eol) {
				return buf.byteBuffer();
			}
		}
		return null;
	}
	
	public int offset() {
		return offset;
	}
	
	public int total() {
		return total;
	}
}
