package javacloud.framework.security.impl;

import javacloud.framework.security.NamespaceContext;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Digests;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import javax.inject.Singleton;

/**
 * Using thread local as implementation, be aware of not access directly from anywhere!
 * HOW MUCH OVER HEAD THIS CAN BE? IF WE USE NAMESPACE WITHOUT NAMESPACE?
 * 
 * @author tobi
 */
@Singleton
public class NamespaceContextImpl implements NamespaceContext {
	private static final ThreadLocal<String> NAMESPACE = new ThreadLocal<String>();	//NAMESPACE
	private static final byte SP = (byte)0x0A;
	
	/**
	 * 
	 */
	public NamespaceContextImpl() {
	}
	
	/**
	 * Make sure to RESET the NAMESPACE HASH WHEN CHANGING NAMESPACE.
	 * TODO: SHOULD NOT MAKE CHANGE IF THEY ARE THE SAME
	 */
	@Override
	public void set(String namespace) {
		NAMESPACE.set(namespace);
	}
	
	/**
	 * return current NAMESPACE.
	 */
	@Override
	public String get() {
		return NAMESPACE.get();
	}
	
	/**
	 * SHA1 with NAMESPACE included if NAMESPACE EXIST. ANY CHANGE TO THIS MIGHT JUST DESTROY EVERYTHING!!!
	 * 
	 * @param keys
	 * @return
	 */
	@Override
	public String hash(Object... keys) {
		//USING SHA1 TO KEEP STUFF SHORT ENOUGH (20 BYTES)
		MessageDigest md = Digests.get(Digests.SHA1);
		
		//HASHKEY IF ANY AVAILABLE
		String hashKey = get();
		if(hashKey != null) {
			md.update(Codecs.toBytes(hashKey));
		}
		
		//ADD ALL KEYS WITHIN
		if(keys != null && keys.length > 0) {
			for(Object key: keys) {
				md.update(SP);	//SP
				if(key instanceof byte[]) {
					md.update((byte[])key);
				} else if(key instanceof ByteBuffer) {
					md.update((ByteBuffer)key);
				} else if(key != null) {
					md.update(Codecs.toBytes(key.toString()));
				}
			}
		}
		
		//MAKE NEW KEY USING SAFE ENCODED
		return Codecs.Base64Encoder.apply(md.digest(), true);
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		NAMESPACE.remove();
	}
}
