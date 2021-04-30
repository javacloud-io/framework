package javacloud.framework.util;

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

/**
 * Wrapping around AES/DES/3DES. Should use AES all the time if NEED TO.
 * 
 * @author tobi
 *
 */
public final class Cryptos {
	//ALGORITHM & KEY SIZE
	public static final String 	ALG_AES 	= "AES";
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
	 * 
	 * @param cipher
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(Cipher cipher, byte[] bytes) {
		cipher.update(bytes);
		try {
			return cipher.doFinal();
		}catch(BadPaddingException | IllegalBlockSizeException ex) {
			throw InternalException.of(ex);
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
			throw InternalException.of(ex);
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
		byte[] hash = Digests.sha2(Codecs.toBytes(keyseed));
		
		//PICK LAST 16 BYTEs of 32 BYTEs
		return getAES(opmode, hash, keylen);
	}
	
	/**
	 * CALCULATE THE SHA2 OF THE CONTENT 32 bytes
	 * 
	 * @param opmode
	 * @param key
	 * @param keylen
	 * @return
	 */
	public static Cipher getAES(int opmode, byte[] key, int keylen) {
		//KEY SIZE CAN BE DYNAMIC 128 bits
		SecretKeySpec keyspec = new SecretKeySpec(key, 0, keylen, ALG_AES);
		
		//PICK LAST 16 BYTEs of 32 BYTEs
		return get(AES_CBC_PKCS5Padding, opmode, keyspec, new IvParameterSpec(key, 16, 16));
	}
	
	/**
	 * Make sure to correct passing the initializing VECTOR.
	 * 
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
			throw InternalException.of(ex);
		}
	}
}
