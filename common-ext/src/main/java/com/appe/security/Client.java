package com.appe.security;




/**
 * Something that can consume or provide service: A CLIENT, SERVER APPLICATION, CONNECTOR or SERVICE.
 * In theory a SERVICE (generic one) always has few CONNECTORs settings where it will access to everything else. An example
 * should be:
 * 
 * 1. A mail service, might need to connect a mail connector where it knows how to access email server.
 * 2. Similar stuff will work out of the box that way. If i want to connect to salesforce, it's easy to make salesforce service
 * which there is a connector that is NOT DOING ANYTHING but bunch of saleforces configuration.
 * 
 * System will managede the dependency chains and pull them in properly.
 * 
 * @author ho
 *
 */
public class Client extends Identifiable<String> {
	public static enum Kind {
		BROWSER,	//agent-based
		NATIVE,		//natively run on mobile device, desktop..
		SERVER		//on premises/clouds
	}
	
	private String	namespace;	//NAMESPACE
	private Kind 	kind;		//KIND
	private String	homeURI;	//client home URI if ANY.
	public Client() {
	}
	
	/**
	 * 
	 */
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	/**
	 * To be able to tell what KIND of client
	 * @return
	 */
	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	/**
	 * The home ROOT URI
	 * 
	 * @return
	 */
	public String getHomeURI() {
		return homeURI;
	}

	public void setHomeURI(String homeURI) {
		this.homeURI = homeURI;
	}
}
