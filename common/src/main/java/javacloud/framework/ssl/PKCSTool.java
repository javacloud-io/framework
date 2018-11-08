package javacloud.framework.ssl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javacloud.framework.io.BytesInputStream;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.PRNG;

/**
 * 
 * @author ho
 *
 */
public final class PKCSTool {
	//SIGNING ALGORITHM
	public static final String ALGO_SHA1withRSA		= "SHA1withRSA";
	public static final String ALGO_SHA2withRSA		= "SHA256withRSA";
	public static final String ALGO_RSA		   		= "RSA";
	
	//PKCS8
	public static final String PEM_PRIVATE_KEY		= "PRIVATE KEY";
	public static final String PEM_PUBLIC_KEY		= "PUBLIC KEY";
	
	//FIXME: SUPPORTING PKCS1
	public static final String PEM_RSA_PRIVATE_KEY	= "RSA PRIVATE KEY";
	
	//X509 certificate
	public static final String PEM_CERTIFICATE 		= "CERTIFICATE";
	
	//CERT REQUEST
	public static final String PEM_CERTIFICATE_REQ	= "CERTIFICATE REQUEST";
	private PKCSTool() {
	}
	
	/**
	 * Create the KEY CHAIN from PEM format, one private KEY & numbers of certificate
	 * 
	 * @param in
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws CertificateException 
	 */
	public final static KeyChain createKeyChain(InputStream in)
		throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
		//LOAD PEM ENCODED
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Map<String, byte[]> encodedMap = parsePEM(reader);
		
		//BUILD PRIVATE KEY/CERTS
		PrivateKey privateKey = null;
		ArrayList<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
		
		for(Map.Entry<String, byte[]> kentry: encodedMap.entrySet()) {
			//PROCESS PKCS8 KEY/CERT
			if(PEM_PRIVATE_KEY.equals(kentry.getKey())) {
				if(privateKey != null) {
					throw new InvalidKeySpecException("Too many private keys");
				}
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(kentry.getValue());
				privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
			} else if(PEM_CERTIFICATE.equals(kentry.getKey())) {
				X509Certificate x509Cert = (X509Certificate)CertificateFactory.getInstance("X.509")
												.generateCertificate(new BytesInputStream(kentry.getValue()));
				x509Certificates.add(x509Cert);
			} else {
				throw new InvalidKeySpecException("Invalid key spec: " + kentry.getKey());
			}
		}
		
		//VALIDATE THE KEY CHAIN?
		//ALL CERTIFICATE SHOULD HAVE THE SAME PUBLIC KEY WHICH PAIR WITH PRIVATE-KEY
		//CERT IN CHAIN END WITH SELF-SIGNED (subject==issuer)
		
		//GOOD KEY CHAIN
		return new KeyChain(privateKey, x509Certificates.isEmpty()
				? null : x509Certificates.toArray(new X509Certificate[x509Certificates.size()]));
	}
	
	/**
	 * RETURN RSA KEY PAIR
	 * 
	 * @param keySize
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair createKeyPair(int keySize) throws NoSuchAlgorithmException {
		//INITIALIZE KEY GENERATOR 
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(PKCSTool.ALGO_RSA);
		keyGen.initialize(keySize, PRNG.get());
		 
		//GENERATE KEYPAIR
		return	keyGen.generateKeyPair();
	}
	
	/**
	 * Special conversion from javax to java certificate.
	 * 
	 * @param xcerts
	 * @return
	 * @throws javax.security.cert.CertificateEncodingException 
	 * @throws CertificateException 
	 */
	public static KeyChain createKeyChain(javax.security.cert.X509Certificate[] xcerts)
		throws CertificateException, javax.security.cert.CertificateEncodingException {
		X509Certificate[] certs = (xcerts == null? null : new X509Certificate[xcerts.length]);
		
		if(xcerts != null) {
			for(int i = 0; i < xcerts.length; i ++) {
				certs[i] = (X509Certificate)CertificateFactory.getInstance("X.509")
					.generateCertificate(new ByteArrayInputStream(xcerts[i].getEncoded()));
			}
		}
		return new KeyChain(null, certs);
	}
	
	/**
	 * Create from a JAVA KEY STORE, assuming using same password.
	 * @param keyStore 
	 * @param password
	 * 
	 * @return
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 */
	public static KeyChain createKeyChain(KeyStore keyStore, String aliasName, char[] password)
		throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		
		//MAKE SURE HAS AT LEAST ONE 
		if(aliasName == null) {
			Enumeration<String> aliasEnum = keyStore.aliases();
			if(!aliasEnum.hasMoreElements()) {
				return new KeyChain(null);
			} else {
				//ALWAYS READ FIRST ONE ONLY
				aliasName = aliasEnum.nextElement();
			}
		}
		
		//READ THE KEY & X509
		PrivateKey privateKey = (PrivateKey)(keyStore.getKey(aliasName, password));
		Certificate[] certificates = keyStore.getCertificateChain(aliasName);
		
		//CONVERT TO X505
		X509Certificate[] x509Certificates = null;
		if(certificates != null && certificates.length > 0) {
			x509Certificates = new X509Certificate[certificates.length];
			for(int i = 0; i < certificates.length; i ++) {
				x509Certificates[i] = (X509Certificate)certificates[i];
			}
		}
		return new KeyChain(privateKey, x509Certificates);
	}
	
	/**
	 * Create from JKS with type/password
	 * 
	 * @param in
	 * @param type
	 * @param keyAlias : null will take first one
	 * @param password
	 * @return
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 */
	public static KeyChain createKeyChain(InputStream in, KeyChain.JKS type, String keyAlias, char[] password)
		throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance(type == KeyChain.JKS.PKCS12? "PKCS12" : "JKS");
		keyStore.load(in, password);
		return createKeyChain(keyStore, keyAlias, password);
	}
	
	/**
	 * Create a JKS from key chain, output to stream...
	 * @param keyChain
	 * @param out
	 * @param type
	 * @param password
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static void storeJKS(KeyChain keyChain, OutputStream out, KeyChain.JKS type, String password)
		throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = keyChain.createJKS(type, password);
		keyStore.store(out, password.toCharArray());
	}
	
	/**
	 * Store the key chain as PEM ENCODED.
	 * @param out
	 * @throws IOException
	 * @throws CertificateEncodingException 
	 */
	public static void storePEM(KeyChain keyChain, OutputStream out)
		throws IOException, CertificateEncodingException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		
		//PRIVATE
		PrivateKey privateKey = keyChain.getPrivateKey();
		if(privateKey != null) {
			storePEM(PEM_PRIVATE_KEY, privateKey.getEncoded(), writer);
			writer.newLine();
		}
		
		//X509 CERTs
		X509Certificate[] x509Certificates = keyChain.getCertificates();
		if(x509Certificates != null && x509Certificates.length > 0) {
			for(X509Certificate cert: x509Certificates) {
				storePEM(PEM_CERTIFICATE, cert.getEncoded(), writer);
				writer.newLine();
			}
		}
		
		//MAKE SURE ALL GOOD.
		writer.flush();
	}
	
	/**
	 * Write the encoded DER in PEM format. Make sure to broken base64 encoded to 64 bytes / line
	 * @param type
	 * @param encoded
	 * @param writer
	 * @throws IOException
	 */
	public static void storePEM(String type, byte[] encoded, BufferedWriter writer)
		throws IOException {
		
		//ADD BEGIN LINE IF HAVE
		if(type != null && !type.isEmpty()) {
			writer.write("-----BEGIN ");
			writer.write(type);
			writer.write("-----");
			writer.newLine();
		}
		
		//BASE64 ENCODED BROKEN DOWN TO 64 / LINE
		String base64 = Codecs.Base64Encoder.apply(encoded, false, true);
		
		//MAKE SURE ADD END LINE
		if(type != null && !type.isEmpty()) {
			if(!base64.endsWith("\n")) {
				writer.newLine();
			}
			writer.write("-----END ");
			writer.write(type);
			writer.write("-----");
		}
	}
	
	/**
	 * shortcut to use OutputStream.
	 * @param type
	 * @param encoded
	 * @param out
	 * @throws IOException
	 */
	public static void storePEM(String type, byte[] encoded, OutputStream out)
		throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		storePEM(type, encoded, writer);
		writer.flush();
	}
	
	/**
	 * Load PEM encoded from a file reader.
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public static Map<String, byte[]> parsePEM(BufferedReader reader) throws IOException {
		Map<String, byte[]> encodedMap = new LinkedHashMap<String, byte[]>(); 
		StringBuilder buff = null;
		String sline = null;
		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			} else if(line.isEmpty()) {
				continue;
			}
			
			//ACTUALLY BEGIN FROM HERE
			if(line.startsWith("-----BEGIN ") && line.endsWith("-----")) {
				sline = line;
				buff = new StringBuilder();
			} else if(line.startsWith("-----END ") && line.endsWith("-----")) {
				//DECODE TYPE & BASE64
				encodedMap.put(sline.substring(11, sline.length() - 5), Codecs.Base64Decoder.apply(buff.toString(), false));
			} else if(buff != null) {
				//JUST APPEND TEXT LINE!
				buff.append(line);
			}
		}
		
		//ANY LEFT OVER WILL BE BAD?
		return encodedMap;
	}
}
