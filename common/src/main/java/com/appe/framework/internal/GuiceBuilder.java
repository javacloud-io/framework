package com.appe.framework.internal;

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
	 * Build an injector from set of modules and overrides
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
