package com.appe.framework.server.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appe.framework.AppeLocale;
import com.appe.framework.AppeRegistry;
/**
 * ALL APIs SHOULD PASSING THROUGH THE MAIN FILTER IF WOULD LIKE TO TRACK EVENT....
 * 
 * @author ho
 *
 */
public class I18nLocaleFilter extends ServletFilter {
	private AppeLocale appeLocale;
	public I18nLocaleFilter() {
	}
	
	/**
	 * 
	 */
	@Override
	protected void init() throws ServletException {
		this.appeLocale = AppeRegistry.get().getInstance(AppeLocale.class);
	}
	
	/**
	 * Find all locales and set to the context
	 */
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws ServletException, IOException {
		try {
			List<Locale> locales = Collections.list(req.getLocales());
			appeLocale.set(locales.toArray(new Locale[locales.size()]));
			
			chain.doFilter(req, resp);
		} finally {
			appeLocale.clear();
		}
		
	}
}
