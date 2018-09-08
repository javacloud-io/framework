package io.javacloud.framework.i18n.internal;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages aggregation into a memory to build a unify bundle
 * 
 * @author ho
 *
 */
public class MessageBundles extends ResourceBundle {
	private final Collection<ResourceBundle> bundles;
	/**
	 * 
	 * @param bundles
	 */
	public MessageBundles(Collection<ResourceBundle> bundles) {
		this.bundles = bundles;
	}
	
	/**
	 * Find the first message from bundle
	 */
	@Override
	protected Object handleGetObject(String key) {
		for(ResourceBundle bundle: bundles) {
			try {
				return bundle.getObject(key);
			} catch (MissingResourceException ex) {
				//ASSUMING NOT FOUND
			}
		}
		return null;
	}
	
	/**
	 * COMBINE ALL KEYES ENUMERATION
	 */
	@Override
	public Enumeration<String> getKeys() {
		return new Enumeration<String>() {
            private final Iterator<ResourceBundle> iter = bundles.iterator();
            private Enumeration<String> keys = null;
            
            //KEEP CHECK UNLESS HAS ONE ELEMENT
            public boolean hasMoreElements() {
            	while(keys == null || !keys.hasMoreElements()) {
            		if(!iter.hasNext()) {
            			return false;
            		}
            		keys = iter.next().getKeys();
            	}
                return true;
            }
            
            //ADVANCE TO NEXT ELEMENT
            public String nextElement() {
            	while(keys == null || !keys.hasMoreElements()) {
            		keys = iter.next().getKeys();
            	}
                return keys.nextElement();
            }
        };
	}
}
