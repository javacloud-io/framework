package javacloud.framework.jetty.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
/**
 * 
 * @author ho
 *
 */
@Path("/greetings")
public class HelloworldResource {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
		return "Hello world!";
	}
}
