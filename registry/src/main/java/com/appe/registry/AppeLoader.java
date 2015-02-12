/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.registry;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * Simple utils to load thing such as class/resources...We always use current ClassLoader to allow override.
 * 
 * @author ho
 *
 */
public final class AppeLoader {
	private AppeLoader() {
	}
	
	/**
	 * Always using the current class loader unless specified different
	 * 
	 * @return
	 */
	public static final ClassLoader getClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(loader == null) {
			loader = AppeLoader.class.getClassLoader();
		}
		return loader;
	}
	
	/**
	 * Using java service loader to dynamically load one service. The implementation of class have to locate under:
	 * META-INF/services/[service.class.getName()]
	 * 
	 * @param service
	 * @return
	 */
	public static final <T> T loadService(Class<T> service) {
		ServiceLoader<T> loader = ServiceLoader.load(service, getClassLoader());
		return loader.iterator().next();
	}
	
	/**
	 * Load the merge all the properties and merge together to form one. Or just load single one from current loader. 
	 * 
	 * @param resource
	 * @param scanning
	 * 
	 * @return NULL if not found any resource
	 * @throws IOException
	 */
	public static final Properties loadProperties(String resource, boolean scanning) throws IOException {
		List<URL> urls = null;
		ClassLoader loader = getClassLoader();
		
		//LOAD EVERYTHING OT JUST ONE
		if(scanning) {
			urls = Collections.list(loader.getResources(resource));
			Collections.reverse(urls);
		} else {
			URL url = loader.getResource(resource);
			if(url != null) {
				urls = Arrays.asList(url);
			}
		}
		
		//MAKE SURE IF ANYTHING NEED TO LOAD
		if(urls == null || urls.isEmpty()) {
			return null;
		}
		
		//LOAD THEM ALL UP & MERGE
		Properties props = new Properties();
		for(URL url: urls) {
			Properties p = new Properties();
			p.load(url.openStream());
			props.putAll(p);
		}
		
		//FINAL VIEW
		return props;
	}
}
