package io.javacloud.framework.server.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.javacloud.framework.cdi.ServiceRegistry;
import io.javacloud.framework.i18n.LocaleContext;
/**
 * ALL APIs SHOULD PASSING THROUGH THE MAIN FILTER IF WOULD LIKE TO TRACK EVENT....
 * 
 * @author ho
 *
 */
public class I18nLocaleFilter extends ServletFilter {
	private LocaleContext localeContext;
	public I18nLocaleFilter() {
	}
	
	/**
	 * Ensure to scan the bundles during initialization
	 */
	@Override
	protected void init() throws ServletException {
		this.localeContext = ServiceRegistry.get().getInstance(LocaleContext.class);
	}
	
	/**
	 * Find all locales and set to the context
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws ServletException, IOException {
		try {
			List<Locale> locales = Collections.list(req.getLocales());
			localeContext.set(locales.toArray(new Locale[locales.size()]));
			
			chain.doFilter(req, resp);
		} finally {
			localeContext.clear();
		}
	}
}
