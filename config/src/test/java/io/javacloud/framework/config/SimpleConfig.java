package io.javacloud.framework.config;
/**
 * Config definition with name/default value
 * 
 * @author ho
 *
 */
public interface SimpleConfig {
	@ConfigProperty(name="test.javacloud.niceName", value="123")
	public String getNiceName();
	
	@ConfigProperty(name="test.javacloud.redefName", value="123")
	public String getRedefName();
	
	@ConfigProperty(name="test.javacloud.intValue", value="123")
	public int getIntValue();
	
	public String getAutoName();
}