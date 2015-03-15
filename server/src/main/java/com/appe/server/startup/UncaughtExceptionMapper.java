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
package com.appe.server.startup;

import com.appe.server.ext.DefaultExceptionMapper;
/**
 * The least caught exception handler, in case of the application doesn't have any better way to handler them.
 * 
 * @author ho
 *
 */
public final class UncaughtExceptionMapper extends DefaultExceptionMapper<Throwable> {
	public UncaughtExceptionMapper() {
	}
}
