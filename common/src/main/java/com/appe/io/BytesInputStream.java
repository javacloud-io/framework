/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.io;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.appe.security.Codecs;
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
		super(buf == null? new byte[0] : Codecs.decodeUTF8(buf));
	}
}
