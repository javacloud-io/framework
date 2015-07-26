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
package com.appe.registry.internal;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
/**
 * Delegate in what form we would like to build injector
 * 
 * @author ho
 *
 */
public interface GuiceBuilder {
	/**
	 * Build an injector with default & override modules
	 * 
	 * @param modules
	 * @param overrides
	 * @return
	 */
	public Injector build(List<Module> modules, List<Module> overrides);
	
	
	//IMPLEMENT BUILDER BY STAGE
	public class StageBuilder implements GuiceBuilder {
		private Stage stage;
		public StageBuilder(Stage stage) {
			this.stage = stage;
		}
		
		@Override
		public Injector build(List<Module> modules, List<Module> overrides) {
			if(overrides == null || overrides.isEmpty()) {
				return Guice.createInjector(stage, modules);
			}
			return Guice.createInjector(stage, Modules.override(modules).with(overrides));
		}
	}
	
	//IMPLEMENT BUILDER BY PARENT
	public class InheritBuilder implements GuiceBuilder {
		private Injector parent;
		public InheritBuilder(Injector parent) {
			this.parent = parent;
		}
		
		@Override
		public Injector build(List<Module> modules, List<Module> overrides) {
			if(overrides == null || overrides.isEmpty()) {
				return parent.createChildInjector(modules);
			}
			return parent.createChildInjector(Modules.override(modules).with(overrides));
		}
	}
}
