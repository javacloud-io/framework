package com.appe.framework.resource;
/**
 * 
 * @author ho
 *
 */
@ConfigBundle.Resource("platform.test")
public interface TestConfig extends ConfigBundle {
	String name();
	String user();
}
