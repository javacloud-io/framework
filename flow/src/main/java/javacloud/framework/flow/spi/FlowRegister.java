package javacloud.framework.flow.spi;

import java.util.Date;

/**
 * 
 * @author ho
 *
 */
public interface FlowRegister {
	/**
	 * 
	 * 
	 * @return
	 */
	public int getRevision();
	
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
