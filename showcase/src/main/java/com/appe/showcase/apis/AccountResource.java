package com.appe.showcase.apis;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * 
 * @author ho
 *
 */
@Path("accounts")
public class AccountResource {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("User")
	public Principal getAccount(@Context SecurityContext scontext) {
		return scontext.getUserPrincipal();
	}
}
