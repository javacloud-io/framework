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
package com.appe.ext.internal;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.CommonProperties;
/**
 * Make sure to always using customized version of JACKSON.
 * 
 * @author ho
 *
 */
public class JacksonFeature implements Feature {
	public JacksonFeature() {
		
	}
	@Override
	public boolean configure(FeatureContext context) {
		context.property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true)
			   .register(JacksonJaxbProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
		return true;
	}

}
