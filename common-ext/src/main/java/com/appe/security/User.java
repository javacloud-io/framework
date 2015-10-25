package com.appe.security;

import java.util.Date;

/**
 * System user which currently represent in few roles:
 * 
 * -ROOT: creator user which using only at bootstrap process & delegate to administrator
 * -ADMINISTRATOR: administrator of the network
 * -DEVELOPER: the main end user using the service
 * -USER: end user at some point if need to support them
 * 
 * @author tobi
 *
 */
public class User extends Identifiable<String> {
	public static enum State {
		REGISTER,	//just register, waiting for activation	
		ACTIVE,		//already activated
		EXPIRED,	//user password already expired, need to change
		LOCKED,		//locked by administrator
		DISABLED	//disabled by administrator
	}
	
	private String email;		//primary email to contact will be the same as user name in most case
	private	String firstName;	//first name
	private	String lastName;	//last name
	
	private	String 	roleName;	//which system role the account has
	private	State	state;		//current state of user, depends on state more thing need to be done.
	private	Date	created;	//date it's created
	public User() {
	}
	
	/**
	 * 
	 * @param userId
	 */
	public User(String userId) {
		super(userId);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * 
	 * @return
	 */
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
