package javacloud.framework.ssl;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
/**
 * Just for testing purpose, should not be use in production at all.
 * @author ho
 *
 */
public final class BlindTrustProvider extends java.security.Provider {
	private static final long serialVersionUID = 5405265117557569492L;
	
	private static final String TRUST_ALG = "BlindTrustX509";
	private final static String TRUST_NAME= "BlindTrustProvider";
		
	//BLIND HOST VERIFICATION
	public static final	HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	//TRUST EVERY BODY FOR NOW
	public static final	TrustManager[] 	 TRUST_MANAGERS = new TrustManager[] {new BlindTrustManager()};
	
	//PROTECTED
	private BlindTrustProvider() {
		super(TRUST_NAME, "1.0.0", TRUST_NAME);
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				put("TrustManagerFactory." + TRUST_ALG, BlindTrustManagerFactorySpi.class.getName());
				return null;
			}
		});
	}
	
	/**
	 * Register and activate the BLIND TRUST. MAKE SURE CALL ONLY ONE TIME
	 */
	public static void register() {
		if(Security.getProvider(TRUST_NAME) == null) {
			Security.insertProviderAt(new BlindTrustProvider(), 2);
			Security.setProperty("ssl.TrustManagerFactory.algorithm", TRUST_ALG);
			
			//DISABLE HOST VERIFICATION
			HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFIER);
		}
	}
	
	//DEFAUL TRUST FACTORY
	public final static class BlindTrustManagerFactorySpi extends TrustManagerFactorySpi {
		@Override
		protected TrustManager[] engineGetTrustManagers() {
			return TRUST_MANAGERS;
		}

		@Override
		protected void engineInit(KeyStore keystore) throws KeyStoreException {		
		}

		@Override
		protected void engineInit(ManagerFactoryParameters params)
				throws InvalidAlgorithmParameterException {
		}
	}
	
	//BLIND TRUST MANAGER
	static final class BlindTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
}
