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
package javacloud.framework.cdi;

import javacloud.framework.cdi.LazySingleton;
import javacloud.framework.cdi.internal.GuiceModule;

import javax.inject.Singleton;
/**
 * @author ho
 *
 */
public class TestModule extends GuiceModule {
	@Override
	public void configure() {
		bind(TestService.class).to(TestServiceImpl.class);
		bind(TestInject.class);
		
		bindToName(TestService.class, "named").to(TestServiceImpl.class);
		bind(TestInjectNamed.class);
		
		//Lazy
		bind(TestLazyService.class).to(TestLazyServiceImpl.class);
	}
	
	//TEST
	@Singleton
	public static class TestServiceImpl implements TestService {
	}
	
	//
	@LazySingleton
	public static class TestLazyServiceImpl implements TestLazyService {
	}
}
