package io.javacloud.framework.i18n.internal;

import io.javacloud.framework.i18n.ContextLocale;
import io.javacloud.framework.util.ResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
/**
 * Auto discover message bundles from META-INF/javacloud.i18n.bundles from all class path
 * 
 * @author ho
 *
 */
public class MessageBundlesControl extends ResourceBundle.Control {
	private final ContextLocale contextLocale;
	private Set<String> bundleNames = new ConcurrentSkipListSet<>();
	
	public MessageBundlesControl(ContextLocale contextLocale) {
		this.contextLocale = contextLocale;
	}
	
	/**
	 * Default formation using PROPERTIES ONLY
	 */
	@Override
	public List<String> getFormats(String baseName) {
		return FORMAT_DEFAULT;
	}
	
	/**
	 * TODO: support correct fallback to a local if not found.
	 */
	@Override
	public Locale getFallbackLocale(String baseName, Locale locale) {
		return contextLocale.next(locale);
	}
	
	/**
	 * Allocate new bundle with baseName
	 */
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		//OK, request universal bundles
		if(baseName == null || baseName.isEmpty()) {
			List<ResourceBundle> bundles = new ArrayList<>(bundleNames.size());
			for(String bundleName: bundleNames) {
				ResourceBundle bundle = super.newBundle(bundleName, locale, format, loader, reload);
				if(bundle != null) {
					bundles.add(bundle);
				}
			}
			return (bundles.isEmpty()? null : new MessageBundles(bundles));
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}
	
	/**
	 * Scan all the bundles under the class LOADER to avoid re-define
	 * @param classLoader
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void discoverBundles(ClassLoader classLoader) throws IOException {
		Enumeration<URL> resourceURLs = classLoader.getResources(ResourceLoader.META_INF + "javacloud.i18n.bundles");
		while(resourceURLs.hasMoreElements()) {
			Properties props = ResourceLoader.loadProperties(resourceURLs.nextElement());
			bundleNames.addAll((Set)props.keySet());
		}
	}
	
	/**
	 * return the internal bundles
	 * @return
	 */
	public Set<String> getBundleNames() {
		return bundleNames;
	}
}
