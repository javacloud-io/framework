package javacloud.framework.flow.spi;

import java.util.Date;

/**
 * An instance of a flow identify by version, flowName is for reference
 * 
 * @author ho
 *
 */
public interface FlowInstance {
	public String getName();
	/**
	 * 
	 * 
	 * @return
	 */
	public String getVersion();
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Date getCreationDate();
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Date getUpdateDate();
}
