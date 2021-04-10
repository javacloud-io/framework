package javacloud.framework.ssl;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import javacloud.framework.util.Objects;
/**
 * Key chain is a simple PRIVATE + CERTIFICATE ASSOCIATED WITH.
 * 
 * @author aimee
 *
 */
public final class KeyChain {
	//DEFAULT ALIAS NAME
	public  static final String ALIAS_NAME	= "keychain";
	
	//DEFAULT KEY STORE TYPEs
	public static enum JKS {
		DEFAULT, PKCS12
	}
	
	//Certificate chain always start with user certificate & end with trusted/self-signed
	private	PrivateKey privateKey;
	private	X509Certificate[] x509Certificates;
	
	/**
	 * 
	 * @param privateKey
	 * @param x509Certificates
	 */
	public KeyChain(PrivateKey privateKey, X509Certificate... x509Certificates) {
		this.privateKey = privateKey;
		this.x509Certificates = x509Certificates;
	}
	
	/**
	 * 
	 * @return the private key
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	/**
	 * 
	 * @return all certificate chains.
	 */
	public X509Certificate[] getCertificates() {
		return x509Certificates;
	}
	
	/**
	 * @return the first certificate of THE CHAINs.
	 */
	public X509Certificate getCertificate() {
		return	(x509Certificates == null || x509Certificates.length == 0? null : x509Certificates[0]);
	}
	
	/**
	 * 
	 * @return certificate store for advance manipulation.
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public CertStore createCertStore() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		CollectionCertStoreParameters params = new CollectionCertStoreParameters(Objects.asList(x509Certificates));
		return CertStore.getInstance("Collection", params, "BC");
	}
	
	/**
	 * Export to any JAVA KEY STORE with password protected.
	 * 
	 * @param type JKS/PCKS12
	 * @param password
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public KeyStore createJKS(KeyChain.JKS type, String password)
		throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		char[] cpassword = password.toCharArray();
		
		//INITIALIZE KEY CHAIN
		KeyStore ks = KeyStore.getInstance(type == KeyChain.JKS.PKCS12? "PKCS12" : "JKS");
		ks.load(null, cpassword);
		
		//SAVE KEY/CERTIFICATE
		if(privateKey != null) {
			ks.setKeyEntry(ALIAS_NAME, privateKey, cpassword, (x509Certificates != null? x509Certificates : new X509Certificate[0]));
		} else if(x509Certificates != null){
			int index = 0;
			for(X509Certificate cert: x509Certificates) {
				ks.setCertificateEntry(ALIAS_NAME + "-" + (index ++), cert);
			}
		}
		
		//STORE THE KEY
		return ks;
	}
	
	/**
	 * Create a key manager[] from a key chain
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public KeyManager[] createKeyManagers()
		throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		KeyManagerFactory kmsf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmsf.init(createJKS(KeyChain.JKS.DEFAULT, ALIAS_NAME), ALIAS_NAME.toCharArray());
		return kmsf.getKeyManagers();
	}
	
	/**
	 * create a trust manager from key chain
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public TrustManager[] createTrustManagers()
		throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		TrustManagerFactory tmsf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmsf.init(createJKS(KeyChain.JKS.DEFAULT, ALIAS_NAME));
		return tmsf.getTrustManagers();
	}
	
	/**
	 * Resolve the certificate chains, find the longest as possible to form the complete chain.
	 * 
	 * Assuming the x509Certificates[0] always the leaf one!!!
	 * @param chains
	 * @return true if resolved does change the certificates.
	 */
	public boolean resolveCertificates(KeyChain...chains) {
		//DON'T RESOLVE ANYTHING
		if(x509Certificates == null || x509Certificates.length == 0) {
			return false;
		}
		
		//BUILDING SIGNERS MAP
		Map<X509Certificate, X509Certificate> signers = new LinkedHashMap<X509Certificate, X509Certificate>();
		for(X509Certificate c: x509Certificates) {
			signers.put(c, null);
		}
		
		//ADD TO CHAIN LIST
		if(chains != null && chains.length > 0) {
			for(KeyChain chain: chains) {
				if(chain.getCertificates() != null) {
					for(X509Certificate c: chain.getCertificates()) {
						signers.put(c, null);
					}
				}
			}
		}
		
		//OK, TRY COMPLETE SIGNER MAP
		for(Map.Entry<X509Certificate, X509Certificate> c : signers.entrySet()) {
			X509Certificate cert = c.getKey();
			for(Map.Entry<X509Certificate, X509Certificate> cc : signers.entrySet()) {
				X509Certificate signer = cc.getKey();
				
				//ALWAYS EXCLUDE MYSELF
				if(cert.equals(signer)) {
					continue;
				}
				
				//ASK IF SIGNED BY SIGNER
				try {
					cert.verify(signer.getPublicKey());
					c.setValue(signer);
					break;
				}catch(Exception ex) {
					//ASSUMING NOT
				}
			}
		}
		
		//NOW BUILDING THE LONGEST CHAIN
		ArrayList<X509Certificate> certs = new ArrayList<X509Certificate>();
		X509Certificate cert = x509Certificates[0];	//LEAF
		do {
			certs.add(cert);
			cert = signers.get(cert);
		} while(cert != null);
		
		//OK, MAKE NEW ONE.
		this.x509Certificates = certs.toArray(new X509Certificate[certs.size()]);
		return true;
	}
}
