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
package com.appe.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.appe.AppeException;

/**
 * Wrapping around AES/DES/3DES. Should use AES all the time if NEED TO.
 * @author tobi
 *
 */
public class Cryptos {
	//ALGORITHM & KEY SIZE
	public static final String 	AES 		= "AES";
	public static final int		AES_KEY128	= 16;
	public static final int 	AES_KEY192	= 24;
	public static final int 	AES_KEY256	= 32;
	
	//PADDING
	public static final String AES_CBC_NoPadding 	= "AES/CBC/NoPadding";
	public static final String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
	public static final String AES_EBC_NoPadding 	= "AES/EBC/NoPadding";
	public static final String AES_EBC_PKCS5Padding = "AES/EBC/PKCS5Padding";
	public static final String DES_CBC_NoPadding	= "DES/CBC/NoPadding";
	public static final String DES_CBC_PKCS5Padding	= "DES/CBC/PKCS5Padding";
	public static final String DES_EBC_NoPadding	= "DES/EBC/NoPadding";
	public static final String DES_EBC_PKCS5Padding	= "DES/EBC/PKCS5Padding";
	public static final String DES3_CBC_NoPadding	= "DESede/CBC/NoPadding";
	public static final String DES3_CBC_PKCS5Padding= "DESede/CBC/PKCS5Padding";
	public static final String DES3_EBC_NoPadding	= "DESede/EBC/NoPadding";
	public static final String DES3_EBC_PKCS5Padding= "DESede/EBC/PKCS5Padding";
	public static final String RSA_EBC_PKCS1Padding = "RSA/ECB/PKCS1Padding";
	public static final String RSA_EBC_OAEPWithSHA_1= "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
	public static final String RSA_EBC_OAEPWithSHA_2= "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	private Cryptos() {
	}
	
	/**
	 * Encrypt byte block using cipher
	 * @param cipher
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(Cipher cipher, byte[] bytes) {
		cipher.update(bytes);
		try {
			return cipher.doFinal();
		}catch(BadPaddingException | IllegalBlockSizeException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * Decrypt the block using a cipher
	 * 
	 * @param transformation
	 * @param cipher
	 * @param bytes
	 * @return
	 */
	public byte[] decrypt(Cipher cipher, byte[] bytes) {
		cipher.update(bytes);
		try {
			return cipher.doFinal();
		}catch(BadPaddingException | IllegalBlockSizeException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * Dynamically calculate the AES cipher using seed key and length in byte (16= 128, 24 = 192, 32/256)
	 * Need to change export policy to generate longer key encryption.
	 * 
	 * @param opmode
	 * @param keyseed
	 * @param keylen
	 * @return
	 */
	public static Cipher getAES(int opmode, String keyseed, int keylen) {
		//CALCULATE THE SHA2 OF THE CONTENT 32 bytes
		byte[] hash = Digests.sha2(Codecs.decodeUTF8(keyseed));
				
		//KEY SIZE CAN BE DYNAMIC 128 bits
		SecretKeySpec keyspec = new SecretKeySpec(hash, 0, keylen, Cryptos.AES);
		
		//PICK LAST 16 BYTEs of 32 BYTEs
		return get(Cryptos.AES_CBC_PKCS5Padding, opmode, keyspec, new IvParameterSpec(hash, 16, 16));
	}
	
	/**
	 * Make sure to correct passing the initializing VECTOR.
	 * @param transformation 
	 * @param opmode
	 * @param key
	 * @param ivSpec
	 * @return
	 */
	public static Cipher get(String transformation, int opmode, Key key, IvParameterSpec ivSpec) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(opmode, key, ivSpec);
			return cipher;
		} catch (InvalidKeyException |  NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
			throw AppeException.wrap(ex);
		}
	}
}
