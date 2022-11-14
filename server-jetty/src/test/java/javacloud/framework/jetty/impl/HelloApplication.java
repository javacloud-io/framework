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
package javacloud.framework.jetty.impl;

import jakarta.ws.rs.ApplicationPath;
import javacloud.framework.server.ServerApplication;

import java.util.Collections;
import java.util.List;
/**
 * 
 * @author ho
 *
 */
@ApplicationPath("/v1")
public class HelloApplication extends ServerApplication {
	public HelloApplication() {
		super("javacloud.framework.jetty.api");
	}

	@Override
	protected List<?> serverComponents() {
		return Collections.emptyList();
	}
}
