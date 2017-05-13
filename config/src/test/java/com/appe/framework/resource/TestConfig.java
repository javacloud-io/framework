package com.appe.framework.resource;

/**
 * 
 * @author ho
 *
 */
@ConfigBundle.Resource("platform.test")
public interface TestConfig extends ConfigBundle {
	@Entry(key="name")
	String name();
	String user();
	
	//dummy with default value lala
	@Entry(key="dummy.key", value="lala")
	String dummy();
}
