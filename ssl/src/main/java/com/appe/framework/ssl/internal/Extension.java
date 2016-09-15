package com.appe.framework.ssl.internal;
/**
 * Extension definition for given stuff
 * @author tobi
 *
 */
public abstract class Extension {
	private boolean critical;
	protected Extension() {
	}
	
	public boolean isCritical() {
		return critical;
	}
	public void setCritical(boolean critical) {
		this.critical = critical;
	}
	
	//KEY USAGE
	public static class KeyUsage extends Extension {
		public static final String OID	= "2.5.29.15";
		
		private	boolean digitalSignature;
		private	boolean nonRepudiation;
		private	boolean keyEncipherment;
		private	boolean dataEncipherment;
		private	boolean keyAgreement;
		private	boolean keyCertSign;
		private	boolean cRLSign;
		private	boolean encipherOnly;
		private	boolean decipherOnly;
		public KeyUsage() {
		}
		public KeyUsage(boolean[] keyUsage) {
			digitalSignature = (keyUsage.length > 0) && keyUsage[0];
			nonRepudiation 	 = (keyUsage.length > 1) && keyUsage[1];
			keyEncipherment  = (keyUsage.length > 2) && keyUsage[2];
			dataEncipherment = (keyUsage.length > 3) && keyUsage[3];
			keyAgreement 	 = (keyUsage.length > 4) && keyUsage[4];
			keyCertSign 	 = (keyUsage.length > 5) && keyUsage[5];
			cRLSign 	 	 = (keyUsage.length > 6) && keyUsage[6];
			encipherOnly 	 = (keyUsage.length > 7) && keyUsage[7];
			decipherOnly 	 = (keyUsage.length > 8) && keyUsage[8];
		}
		public boolean isDigitalSignature() {
			return digitalSignature;
		}
		public void setDigitalSignature(boolean digitalSignature) {
			this.digitalSignature = digitalSignature;
		}
		
		public boolean isNonRepudiation() {
			return nonRepudiation;
		}
		public void setNonRepudiation(boolean nonRepudiation) {
			this.nonRepudiation = nonRepudiation;
		}
		
		public boolean isKeyEncipherment() {
			return keyEncipherment;
		}
		public void setKeyEncipherment(boolean keyEncipherment) {
			this.keyEncipherment = keyEncipherment;
		}
		
		public boolean isDataEncipherment() {
			return dataEncipherment;
		}
		public void setDataEncipherment(boolean dataEncipherment) {
			this.dataEncipherment = dataEncipherment;
		}
		
		public boolean isKeyAgreement() {
			return keyAgreement;
		}
		public void setKeyAgreement(boolean keyAgreement) {
			this.keyAgreement = keyAgreement;
		}
		
		public boolean isKeyCertSign() {
			return keyCertSign;
		}
		public void setKeyCertSign(boolean keyCertSign) {
			this.keyCertSign = keyCertSign;
		}
		
		public boolean isCRLSign() {
			return cRLSign;
		}
		public void setCRLSign(boolean cRLSign) {
			this.cRLSign = cRLSign;
		}
		
		public boolean isEncipherOnly() {
			return encipherOnly;
		}
		public void setEncipherOnly(boolean encipherOnly) {
			this.encipherOnly = encipherOnly;
		}
		
		public boolean isDecipherOnly() {
			return decipherOnly;
		}
		public void setDecipherOnly(boolean decipherOnly) {
			this.decipherOnly = decipherOnly;
		}
		
		//return array of 9 key usage attributes
		public boolean[] getBits() {
			return new boolean[]
					{digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment, keyAgreement, keyCertSign, cRLSign, encipherOnly, decipherOnly};
		}
	}
	
	//Subject alternate name
	public static class SubjectAltName extends Extension {
		public static final String OID = "2.5.29.17";
		public static final String OID_UPNAME	= "1.3.6.1.4.1.311.20.2.3";	//NT PRINCIPAL NAME
		
		private	String rfc822Name;
	    private	String dNSName;
	    private	String uRIName;
	    private	String iPAddress;
	    private String uPName;	//NT PRINCIPAL (1.3.6.1.4.1.311.20.2.3)
	    public SubjectAltName() {
	    }
	    public String getRfc822Name() {
			return rfc822Name;
		}
		public void setRfc822Name(String rfc822Name) {
			this.rfc822Name = rfc822Name;
		}
		
		public String getDNSName() {
			return dNSName;
		}
		public void setDNSName(String dNSName) {
			this.dNSName = dNSName;
		}
		
		public String getURIName() {
			return uRIName;
		}
		public void setURIName(String uRIName) {
			this.uRIName = uRIName;
		}
		
		public String getIPAddress() {
			return iPAddress;
		}
		public void setIPAddress(String iPAddress) {
			this.iPAddress = iPAddress;
		}
		
		//UPN
		public String getUPName() {
			return uPName;
		}

		public void setUPName(String uPName) {
			this.uPName = uPName;
		}
	}
	
	//EXTENDED KEY USAGE
	public static class ExtKeyUsage extends Extension {
		public static final String OID					= "2.5.29.37";
		
		public static final String OID_SERVER_AUTH 		= "1.3.6.1.5.5.7.3.1";
		public static final String OID_CLIENT_AUTH 		= "1.3.6.1.5.5.7.3.2";
		public static final String OID_CODE_SIGNING		= "1.3.6.1.5.5.7.3.3";
		public static final String OID_EMAIL_PROTECTION = "1.3.6.1.5.5.7.3.4";
		
		private boolean serverAuth; 	// (1.3.6.1.5.5.7.3.1) -- TLS Web server authentication
		private boolean clientAuth; 	// (1.3.6.1.5.5.7.3.2) -- TLS Web client authentication
		private boolean codeSigning; 	// (1.3.6.1.5.5.7.3.3) -- Code signing
		private boolean emailProtection;// (1.3.6.1.5.5.7.3.4) -- E-mail protection
		public ExtKeyUsage() {
		}

		public boolean isServerAuth() {
			return serverAuth;
		}

		public void setServerAuth(boolean serverAuth) {
			this.serverAuth = serverAuth;
		}

		public boolean isClientAuth() {
			return clientAuth;
		}

		public void setClientAuth(boolean clientAuth) {
			this.clientAuth = clientAuth;
		}

		public boolean isCodeSigning() {
			return codeSigning;
		}

		public void setCodeSigning(boolean codeSigning) {
			this.codeSigning = codeSigning;
		}

		public boolean isEmailProtection() {
			return emailProtection;
		}

		public void setEmailProtection(boolean emailProtection) {
			this.emailProtection = emailProtection;
		}
	}
	
	//BASIC CONSTRAINTS
	public static class BasicConstraints extends Extension {
		public static final String OID	= "2.5.29.19";
		private boolean cA;
		private int depth;
		public BasicConstraints() {
			this.depth = -1;
		}
		public boolean isCA() {
			return cA;
		}
		public void setCA(boolean cA) {
			this.cA = cA;
		}
		public int getDepth() {
			return depth;
		}
		public void setDepth(int depth) {
			this.depth = depth;
		}
	}
	
	//HEX ENCODED IDENTIFIER
	public static class KeyIdentifier extends Extension {
		public static final String OID_SUBJECT_KEY 	= "2.5.29.14";
		public static final String OID_AUTHORITY_KEY= "2.5.29.35";
		private String identifier;
		public KeyIdentifier() {
		}
		public String getIdentifier() {
			return identifier;
		}
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}
	}
}
