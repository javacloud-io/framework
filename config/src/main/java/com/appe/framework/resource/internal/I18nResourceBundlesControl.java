package com.appe.framework.resource.internal;

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

import com.appe.framework.AppeLoader;
import com.appe.framework.AppeLocale;
/**
 * Loading the unify message bundle from META-INF/i18n-messages.bundles
 * 
 * @author ho
 *
 */
public class I18nResourceBundlesControl extends ResourceBundle.Control {
	private static final String I18N_BUNDLES = "META-INF/i18n-messages.bundles";
	
	private final AppeLocale appeLocale;
	private Set<String> bundleNames = new ConcurrentSkipListSet<>();
	public I18nResourceBundlesControl(AppeLocale appeLocale) {
		this.appeLocale = appeLocale;
	}
	
	/**
	 * 
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
		return appeLocale.next(locale);
	}
	
	/**
	 * 
	 */
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
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
			return (bundles.isEmpty()? null : new I18nResourceBundles(bundles));
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}
	
	/**
	 * Scan all the bundles under the class LOADER
	 * @param classLoader
	 * @param refresh
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void scanBundles(ClassLoader classLoader, boolean refresh) throws IOException {
		//BY DEFAULT SHOULD ALWAYS REFRESH
		if(refresh) {
			bundleNames.clear();
		}
		
		//FIND ALL BUNDLES
		for(Enumeration<URL> resourceURLs = classLoader.getResources(I18N_BUNDLES); resourceURLs.hasMoreElements(); ) {
			URL url = resourceURLs.nextElement();
			Properties props = AppeLoader.loadProperties(url);
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
