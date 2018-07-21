package com.appe.framework.data.impl;

import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataType;
import com.appe.framework.io.Dictionary;

import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public class DataSchemaTest extends TestCase {
	public static class Foo {
		private String a;
		private boolean b;
		public void setA(String a) {
			this.a = a;
		}
		public String getA() {
			return a;
		}
		
		public boolean isB() {
			return b;
		}
		public void setB(boolean b) {
			this.b = b;
		}
	}
	
	/**
	 * 
	 */
	public void testColumns() throws Exception {
		DataMapper<Foo> schema = new DataMapper.POJO<Foo>(Foo.class);
		assertTrue(schema.getColumns().size() == 2);
		assertTrue(schema.getColumns().get("a") == DataType.UTF8);
	}
	
	public void testPerf() {
		DataMapper<Foo> schema = new DataMapper.POJO<Foo>(Foo.class);
		DataMapper<Dictionary> dschema = new DataMapper.MAP<Dictionary>(Dictionary.class,
				"a", DataType.UTF8,
				"a1", DataType.UTF8,
				"a2", DataType.UTF8,
				"a3", DataType.UTF8,
				"a4", DataType.UTF8,
				"a5", DataType.UTF8,
				"a6", DataType.UTF8,
				"a7", DataType.UTF8,
				"a8", DataType.UTF8,
				"a9", DataType.UTF8);
		
		long start = System.currentTimeMillis();
		doA(schema);
		System.out.println(System.currentTimeMillis() - start);
		
		start = System.currentTimeMillis();
		doAA(dschema);
		System.out.println(System.currentTimeMillis() - start);
		
		start = System.currentTimeMillis();
		doAB(dschema);
		System.out.println(System.currentTimeMillis() - start);
	}
	
	private void doA(DataMapper<Foo> schema) {
		for(int i = 0; i < 1000000; i ++) {
			Foo foo = schema.newModel();
			schema.setColumn(foo, "a", "123");
			schema.getColumn(foo, "a");
		}
	}
	
	private void doAB(DataMapper<Dictionary> schema) {
		for(int i = 0; i < 1000000; i ++) {
			Dictionary foo = schema.newModel();
			schema.setColumn(foo, "a", "123");
			schema.getColumn(foo, "a");
		}
	}
	private void doAA(DataMapper<Dictionary> dschema) {
		for(int i = 0; i < 1000000; i ++) {
			Foo foo = new Foo();
			foo.setA("123");
			foo.getA();
			
			//Collections.unmodifiableMap(dschema.getColumns());
			//Map<String, DataType> types = new LinkedHashMap<String, DataType>(dschema.getColumns());
			//types.keySet().retainAll(Objects.asList("a"));
		}
	}
}
