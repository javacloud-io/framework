package javacloud.framework.flow.spi;

import java.util.Date;

/**
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
