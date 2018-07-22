package com.appe.framework.ssl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.appe.framework.io.Dictionary;
import com.appe.framework.ssl.KeyChain;
import com.appe.framework.ssl.PKCSTool;
import com.appe.framework.util.Codecs;
import com.appe.framework.util.Digests;
import com.appe.framework.util.PRNG;

import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs.PKCS9Attributes;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.SignerInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs10.PKCS10Attribute;
import sun.security.pkcs10.PKCS10;
import sun.security.pkcs10.PKCS10Attributes;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.X500Name;
import sun.security.x509.RFC822Name;
import sun.security.x509.GeneralName;
import sun.security.x509.URIName;
import sun.security.x509.DNSName;
import sun.security.x509.IPAddressName;
import sun.security.x509.OtherName;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
/**
 * Example of subject DN format: E = jecdis@gmail.com, CN = *.jecdis.com, OU = Jappe, O = Jecdis Inc, L = San Jose, ST = CA, C = US.
 * CN is the most importance piece, if using SSL it will be validated again server domain. alternateSubjectName also a good one which we haven't
 * address yet.
 * 
 * //PKCS8 PRIVATE KEY
 *	PrivateKey privateKey = KeyFactory.getInstance(ALGO_RSA)
 *				.generatePrivate(new PKCS8EncodedKeySpec(Codecs.decodeBase64(pkcs8Key, false)));
 * 
 * //SAMPLE CERT FROM DER
 * X509Certificate x509Cert = (X509Certificate)CertificateFactory.getInstance("X.509")
 *						.generateCertificate(new BytesInputStream(Codecs.decodeBase64(scert, false)));
 * @author aimee
 *
 */
@SuppressWarnings("restriction")
public final class SunPKCSTool {
	private SunPKCSTool() {
	}
	
	/**
	 * Create certificate signed request in PEM format. TODO: add more attributes
	 * 
	 * @param keyChain
	 * @param crt
	 * 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws SignatureException 
	 * @throws CertificateException 
	 *  
	 */
	public static byte[] createCSR(KeyChain keyChain, CSR crt)
		throws IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateException, SignatureException {
		//SIGNER
		X500Name 	subjectName = new X500Name(crt.getSubjectDN());
		Signature 	signature = Signature.getInstance(PKCSTool.ALGO_SHA2withRSA);
		signature.initSign(keyChain.getPrivateKey());
		
		//ANY EXTENSIONS V3?
		CertificateExtensions exts = new CertificateExtensions();
		buildX509Extensions(crt, exts);
		
		//ADD ATTRIBUTES
		PKCS10Attribute[] attrs = new PKCS10Attribute[] {new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, exts)};
		PKCS10 certReq = new PKCS10(keyChain.getCertificate().getPublicKey(), new PKCS10Attributes(attrs));
		certReq.encodeAndSign(subjectName, signature);
		
		//RETURN PKCS10 ENCODED
		return	certReq.getEncoded();
	}
	
	/**
	 * TODO: Need to make sure don't add KEYUSAGE if NO VALUE SET.
	 * @param crt
	 * @param exts
	 * @throws IOException
	 */
	private static void buildX509Extensions(CSR crt, CertificateExtensions exts) throws IOException {
		//KEY USAGE (NON-CRITICAL FOR NOW)
		for(Map.Entry<String, Object> entry : crt.getExtensions().entrySet()) {
			Object ext = entry.getValue();
			if(ext instanceof Extension.KeyUsage) {
				Extension.KeyUsage keyUsage = (Extension.KeyUsage)ext;
				
				exts.set(KeyUsageExtension.NAME, new KeyUsageExtension(keyUsage.isCritical(),
						new KeyUsageExtension(keyUsage.getBits()).getExtensionValue()));
			} else if(ext instanceof Extension.SubjectAltName) {
				Extension.SubjectAltName subjectAltName = (Extension.SubjectAltName)ext;
				GeneralNames generalNames = new GeneralNames();
				
				if(subjectAltName.getRfc822Name() != null) {
					generalNames.add(new GeneralName(new RFC822Name(subjectAltName.getRfc822Name())));
				}
				if(subjectAltName.getDNSName() != null) {
					generalNames.add(new GeneralName(new DNSName(subjectAltName.getDNSName())));
				}
				if(subjectAltName.getURIName() != null) {
					generalNames.add(new GeneralName(new URIName(subjectAltName.getURIName())));
				}
				if(subjectAltName.getIPAddress() != null) {
					generalNames.add(new GeneralName(new IPAddressName(subjectAltName.getIPAddress())));
				}
				if(subjectAltName.getUPName() != null) {
					generalNames.add(new GeneralName(new OtherName(new ObjectIdentifier(Extension.SubjectAltName.OID_UPNAME),
							new DerValue(subjectAltName.getUPName()).toByteArray())));
				}
				if(!generalNames.isEmpty()) {
					exts.set(SubjectAlternativeNameExtension.NAME,
							new SubjectAlternativeNameExtension(subjectAltName.isCritical(), generalNames));
				}
			} else if(ext instanceof Extension.ExtKeyUsage) {//EXTENDED KEY USAGE
				Extension.ExtKeyUsage extKeyUsage = (Extension.ExtKeyUsage)ext;
				
				Vector<ObjectIdentifier> keyOIDS = new Vector<ObjectIdentifier>();
				if(extKeyUsage.isServerAuth()) {
					keyOIDS.add(new ObjectIdentifier(Extension.ExtKeyUsage.OID_SERVER_AUTH));
				}
				if(extKeyUsage.isClientAuth()) {
					keyOIDS.add(new ObjectIdentifier(Extension.ExtKeyUsage.OID_CLIENT_AUTH));
				}
				if(extKeyUsage.isCodeSigning()) {
					keyOIDS.add(new ObjectIdentifier(Extension.ExtKeyUsage.OID_CODE_SIGNING));
				}
				if(extKeyUsage.isEmailProtection()) {
					keyOIDS.add(new ObjectIdentifier(Extension.ExtKeyUsage.OID_EMAIL_PROTECTION));
				}
				if(!keyOIDS.isEmpty()) {
					exts.set(ExtendedKeyUsageExtension.NAME, new ExtendedKeyUsageExtension(extKeyUsage.isCritical(), keyOIDS));
				}
			} else if(ext instanceof Extension.BasicConstraints) {
				Extension.BasicConstraints basicConstraints = (Extension.BasicConstraints)ext;
				exts.set(BasicConstraintsExtension.NAME,
						new BasicConstraintsExtension(basicConstraints.isCA(), basicConstraints.isCritical(), basicConstraints.getDepth()));
			}
		}
	}
	
	/**
	 * Parsing out extension of certificate in details include most of the understandable stuff as possible.
	 * @param cert
	 * @return
	 * @throws IOException 
	 */
	public static CSR parseX509Extensions(X509Certificate cert) throws IOException {
		CSR crt = new CSR(cert.getSubjectDN().toString(), cert.getNotAfter());
		CertificateExtensions exts = new CertificateExtensions();
		Set<String> criticalOIDs = cert.getCriticalExtensionOIDs();
		
		byte[] encoded = cert.getExtensionValue(Extension.KeyUsage.OID);	//KEYUSAGE
		if(encoded != null) {
			exts.set(KeyUsageExtension.NAME,
					new KeyUsageExtension(criticalOIDs.contains(Extension.KeyUsage.OID),
							new DerValue(encoded).getOctetString()));
		}
		
		encoded = cert.getExtensionValue(Extension.SubjectAltName.OID);
		if(encoded != null) {
			exts.set(SubjectAlternativeNameExtension.NAME,
					new SubjectAlternativeNameExtension(criticalOIDs.contains(Extension.SubjectAltName.OID),
							new DerValue(encoded).getOctetString()));
		}
		
		encoded = cert.getExtensionValue(Extension.ExtKeyUsage.OID);
		if(encoded != null) {
			exts.set(ExtendedKeyUsageExtension.NAME,
					new ExtendedKeyUsageExtension(criticalOIDs.contains(Extension.ExtKeyUsage.OID),
							new DerValue(encoded).getOctetString()));
		}
		
		encoded = cert.getExtensionValue(Extension.BasicConstraints.OID);
		if(encoded != null) {
			exts.set(BasicConstraintsExtension.NAME,
					new BasicConstraintsExtension(criticalOIDs.contains(Extension.BasicConstraints.OID),
							new DerValue(encoded).getOctetString()));
		}
		
		//SUBJECT KEY IDENTIFIER
		encoded = cert.getExtensionValue(Extension.KeyIdentifier.OID_SUBJECT_KEY);
		if(encoded != null) {
			exts.set(SubjectKeyIdentifierExtension.NAME,
					new SubjectKeyIdentifierExtension(criticalOIDs.contains(Extension.KeyIdentifier.OID_SUBJECT_KEY),
							new DerValue(encoded).getOctetString()));
		}
		
		//AUTHORITY KEY IDENTIFIER
		encoded = cert.getExtensionValue(Extension.KeyIdentifier.OID_AUTHORITY_KEY);
		if(encoded != null) {
			exts.set(AuthorityKeyIdentifierExtension.NAME,
					new AuthorityKeyIdentifierExtension(criticalOIDs.contains(Extension.KeyIdentifier.OID_AUTHORITY_KEY),
							new DerValue(encoded).getOctetString()));
		}
		
		//PARSE EXTENSIONS
		parseX509Extensions(exts, crt);
		return crt;
	}
	
	/**
	 * PARSING certificate extensions and fill in the CRT
	 * 
	 * @param exts
	 * @param crt
	 * @throws IOException
	 */
	private static void parseX509Extensions(CertificateExtensions exts, CSR crt) throws IOException {
		//USE EVERYHIGN FROM SIMPLE CRT
		for(sun.security.x509.Extension ext: exts.getAllExtensions()) {
			if(ext instanceof KeyUsageExtension) {
				KeyUsageExtension keyUsage = (KeyUsageExtension)ext;
				Extension.KeyUsage usage = new Extension.KeyUsage(keyUsage.getBits());
				usage.setCritical(keyUsage.isCritical());
				crt.setKeyUsage(usage);
			} else if(ext instanceof SubjectAlternativeNameExtension) {
				SubjectAlternativeNameExtension altSubjectNames = (SubjectAlternativeNameExtension)ext;
				GeneralNames altNames = (GeneralNames)altSubjectNames.get(SubjectAlternativeNameExtension.SUBJECT_NAME);
				Extension.SubjectAltName altSubject = new Extension.SubjectAltName();
				for(GeneralName name: altNames.names()) {
					if(name.getType() == GeneralNameInterface.NAME_RFC822) {
						altSubject.setRfc822Name(((RFC822Name)name.getName()).getName());
					} else if(name.getType() == GeneralNameInterface.NAME_DNS) {
						altSubject.setDNSName(((DNSName)name.getName()).getName());
					} else if(name.getType() == GeneralNameInterface.NAME_URI) {
						altSubject.setURIName(((URIName)name.getName()).getName());
					} else if(name.getType() == GeneralNameInterface.NAME_IP) {
						altSubject.setIPAddress(((IPAddressName)name.getName()).getName());
					} else if(name.getType() == GeneralNameInterface.NAME_ANY) {
						OtherName oname = (OtherName)name.getName();
						String oid = oname.getOID().toString();
						byte[] nameValue = new DerValue(oname.getNameValue()).getDataBytes();//ARRAY
						if(oid.equals(Extension.SubjectAltName.OID_UPNAME)) {
							altSubject.setUPName(new DerValue(nameValue).getAsString());
						}
					}
				}
				altSubject.setCritical(altSubjectNames.isCritical());
				crt.setSubjectAltName(altSubject);
			} else if(ext instanceof ExtendedKeyUsageExtension) {
				ExtendedKeyUsageExtension extUsage = (ExtendedKeyUsageExtension)ext;
				Extension.ExtKeyUsage usage = new Extension.ExtKeyUsage();
				for(String oid: extUsage.getExtendedKeyUsage()) {
					if(Extension.ExtKeyUsage.OID_SERVER_AUTH.equals(oid)) {
						usage.setServerAuth(true);
					} else if(Extension.ExtKeyUsage.OID_CLIENT_AUTH.equals(oid)) {
						usage.setClientAuth(true);
					} else if(Extension.ExtKeyUsage.OID_CODE_SIGNING.equals(oid)) {
						usage.setCodeSigning(true);
					} else if(Extension.ExtKeyUsage.OID_EMAIL_PROTECTION.equals(oid)) {
						usage.setEmailProtection(true);
					}
				}
				usage.setCritical(extUsage.isCritical());
				crt.setExtKeyUsage(usage);
			} else if(ext instanceof BasicConstraintsExtension) {
				BasicConstraintsExtension caUsage = (BasicConstraintsExtension)ext;
				crt.setBasicConstraints((Boolean)caUsage.get(BasicConstraintsExtension.IS_CA),
						caUsage.isCritical(), (Integer)caUsage.get(BasicConstraintsExtension.PATH_LEN));
			} else if(ext instanceof SubjectKeyIdentifierExtension) {
				SubjectKeyIdentifierExtension subjectKeyExtension = (SubjectKeyIdentifierExtension)ext;
				KeyIdentifier keyIdentifier = (KeyIdentifier)subjectKeyExtension.get(SubjectKeyIdentifierExtension.KEY_ID);
				
				Extension.KeyIdentifier keyExtension = new Extension.KeyIdentifier();
				keyExtension.setCritical(subjectKeyExtension.isCritical());
				keyExtension.setIdentifier(Codecs.encodeHex(keyIdentifier.getIdentifier()));
				crt.getExtensions().set(Extension.KeyIdentifier.OID_SUBJECT_KEY, keyExtension);
			} else if(ext instanceof AuthorityKeyIdentifierExtension) {
				AuthorityKeyIdentifierExtension authorityKeyExtension = (AuthorityKeyIdentifierExtension)ext;
				KeyIdentifier keyIdentifier = (KeyIdentifier)authorityKeyExtension.get(AuthorityKeyIdentifierExtension.KEY_ID);
				
				Extension.KeyIdentifier keyExtension = new Extension.KeyIdentifier();
				keyExtension.setCritical(authorityKeyExtension.isCritical());
				keyExtension.setIdentifier(Codecs.encodeHex(keyIdentifier.getIdentifier()));
				crt.getExtensions().set(Extension.KeyIdentifier.OID_AUTHORITY_KEY, keyExtension);
			}
		}
	}
	
	/**
	 * Create and sign the cert request in PKCS10 format. Assuming we trust what customer send.
	 * 
	 * @param keySigner
	 * @param certCSR
	 * @param expired
	 * 
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws CertificateException 
	 * @throws InvalidKeyException 
	 */
	public static X509Certificate createX509(KeyChain keySigner, byte[] certCSR, Date expired)
		throws SignatureException, NoSuchAlgorithmException, IOException, InvalidKeyException, CertificateException, NoSuchProviderException {
		PKCS10 certReq = new PKCS10(certCSR);
		
		//TRANSFER ATTRIBUTES
		CSR crt = new CSR(certReq.getSubjectName().getName(), expired);
		
		//FINDING EXTENSION VALUE
		CertificateExtensions exts = null;
		for(PKCS10Attribute attr: certReq.getAttributes().getAttributes()) {
			if (PKCS9Attribute.EXTENSION_REQUEST_OID.equals((Object)attr.getAttributeId())) {
				exts = (CertificateExtensions)attr.getAttributeValue();
				break;
			}
		}
		
		//PARSING OUT EXTENSION
		if(exts != null) {
			parseX509Extensions(exts, crt);
		}
		//OVERRIDE WHAT FROM CRT?
		
		//ISSUE CERT WITH NEW ATTRIBUTES
		return	createX509(keySigner, certReq.getSubjectPublicKeyInfo(), crt);
	}
	
	/**
	 * Simplify certificate creation, using publicKey & subJectDN. It's would be use less if self-signed certificate
	 * have different subject DN.
	 * 
	 * TODO: 
	 * -extends to add more extension attributes.
	 * -some how still not able to generate a V3 that make apple MDM happy.
	 * 
	 * @param keySigner
	 * @param publicKey
	 * @param crt
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws CertificateException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 */
	public static X509Certificate createX509(KeyChain keySigner, PublicKey publicKey, CSR crt)
		throws IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateException, NoSuchProviderException, SignatureException {
		X509Certificate signerCert = keySigner.getCertificate();
		
		//NEW CERTIFICATE
		long nouce = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);	//BACK OFF 1 HOUR
		CertificateValidity validity = new CertificateValidity(new Date(nouce), crt.getExpired());
		
		X509CertInfo info = new X509CertInfo();
		X500Name subjectName = new X500Name(crt.getSubjectDN());
		
		//WHAT HAVE TO SET IF USING V3? APPLE DOES VALIDATION SOME HOW.
		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(PRNG.nextBInteger(16)));	//16 bytes
		
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get(PKCSTool.ALGO_SHA2withRSA)));
		info.set(X509CertInfo.SUBJECT, subjectName);
		
		info.set(X509CertInfo.KEY, new CertificateX509Key(publicKey));
		info.set(X509CertInfo.VALIDITY, validity);
		
		//SELF-SIGN IF NOT SIGNER CERT
		info.set(X509CertInfo.ISSUER, 
				signerCert != null? new X500Name(signerCert.getSubjectDN().getName()) : subjectName);
		
		//ANY EXTENSIONS V3?
		CertificateExtensions exts = new CertificateExtensions();
		
		//SUBJECT KEY IDENTIFIER
		exts.set(SubjectKeyIdentifierExtension.NAME,
				new SubjectKeyIdentifierExtension(new KeyIdentifier(publicKey).getIdentifier()));
		
		//SIGNER IDENTIFIER
		exts.set(AuthorityKeyIdentifierExtension.NAME, new AuthorityKeyIdentifierExtension(
				new KeyIdentifier(signerCert != null? signerCert.getPublicKey() : publicKey), null, null));
		
		//ADD MORE TO THE LIST
		buildX509Extensions(crt, exts);
		
		//SET EXTENSIONS
		info.set(X509CertInfo.EXTENSIONS, exts);
		
		//SIGN WITH NEW KEYs
		X509CertImpl x509cert = new X509CertImpl(info);
		x509cert.sign(keySigner.getPrivateKey(), PKCSTool.ALGO_SHA1withRSA);
		return x509cert;
	}
	
	/**
	 * Create a certificate from DER encoded.
	 * @param derEncoded
	 * @return
	 * @throws CertificateException 
	 */
	public static X509Certificate createX509(InputStream derEncoded) throws CertificateException {
		return	(X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(derEncoded);
	}
	
	/**
	 * Sign the DATA with envelope included, PKI format.
	 * 
	 * @param message
	 * @param signerKey
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 * @throws InvalidKeyException 
	 * @throws SignatureException 
	 */
	public static byte[] signPKCS7(SIG message, PrivateKey signerKey)
		throws NoSuchAlgorithmException, IllegalArgumentException, IOException, InvalidKeyException, SignatureException {
		//1. DIGEST CONTENT
		AlgorithmId sha1Id = AlgorithmId.get(Digests.SHA1);
		PKCS9Attributes authenticatedAttributes = new PKCS9Attributes(new PKCS9Attribute[] {
			new PKCS9Attribute(PKCS9Attribute.CONTENT_TYPE_OID, ContentInfo.DATA_OID),
			new PKCS9Attribute(PKCS9Attribute.SIGNING_TIME_OID, new Date()),
			new PKCS9Attribute(PKCS9Attribute.MESSAGE_DIGEST_OID, Digests.digest(Digests.SHA1, message.getData()))
		});
		
		//2. SIGN EVERYTHING
		Signature signer = Signature.getInstance(PKCSTool.ALGO_SHA2withRSA);
		signer.initSign(signerKey);
		signer.update(authenticatedAttributes.getDerEncoding());
		byte[] signedAttributes = signer.sign();
		
		//3. ENCODE EVERY THING TO DER
		ContentInfo contentInfo = new ContentInfo(ContentInfo.DATA_OID,
				new DerValue(DerValue.tag_OctetString, message.getData()));
		X509Certificate signerCert = message.getSigners()[0];
		
		SignerInfo signerInfo = new SignerInfo(new X500Name(signerCert.getIssuerDN().getName()),
				signerCert.getSerialNumber(), sha1Id, authenticatedAttributes,
				new AlgorithmId(AlgorithmId.RSAEncryption_oid), signedAttributes, null);
		
		PKCS7 pkcs7 = new PKCS7(new AlgorithmId[] {sha1Id}, contentInfo, message.getSigners(), new SignerInfo[] {signerInfo});
		
		//4. SIGN & FLUSH TO DER
		DerOutputStream signedDer = new DerOutputStream();
		pkcs7.encodeSignedData(signedDer);
		signedDer.flush();
		return signedDer.toByteArray();
	}
	
	/**
	 * Verify PKCS7 message + signer info attributes
	 * 
	 * @param message
	 * @return
	 * @throws IOException 
	 */
	public static SIG verifyPKCS7(byte[] message) throws IOException {
		PKCS7 pkcs7 = new PKCS7(message);
		
		Dictionary attributes = new Dictionary(new LinkedHashMap<String, Object>());
		SignerInfo[] signerInfos = pkcs7.getSignerInfos();
		if(signerInfos.length > 0) {
			for(PKCS9Attribute attr: signerInfos[0].getAuthenticatedAttributes().getAttributes()) {
				attributes.put(attr.getOID().toString(), attr.getValue());
			}
		}
		return new SIG(pkcs7.getContentInfo().getData(), attributes, pkcs7.getCertificates());
	}
}
