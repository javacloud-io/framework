package com.appe.framework.data.impl;

import java.util.List;
import java.util.Map;

import com.appe.framework.data.DataCondition;
import com.appe.framework.data.DataKey;
import com.appe.framework.data.DataManager;
import com.appe.framework.data.DataMapper;
import com.appe.framework.data.DataRange;
import com.appe.framework.data.DataResult;
import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;
import com.appe.framework.data.DataType;
import com.appe.framework.io.Dictionary;
import com.appe.framework.util.Objects;

import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public abstract class DataStoreImplTest extends TestCase {
	protected DataManager dataManager;
	/**
	 * 
	 */
	public void testHashKey() {
		DataSchema<Dictionary> schema = new DataSchema<Dictionary>("table1", "id",
					new DataMapper.MAP<Dictionary>(Dictionary.class, 
										"id", DataType.UTF8,
										"name", DataType.UTF8,
										"ai", DataType.UTF8,
										"a2i", DataType.UTF8,
										"to2", DataType.UTF8,
										"to2ABC", DataType.UTF8,
										"ci", DataType.COUNTER));
		
		assertTrue(schema.getTable().equals("table1"));
		assertTrue(schema.getHashKey().equals("id"));
		
		//CREATE A TABLE
		DataStore<Dictionary> dataStore = dataManager.bindStore(schema);
		
		Dictionary model;
		for(int i = 0; i < 110; i ++) {
			model = schema.createModel(new DataKey("id-" + i));
			model.put("ai",  String.valueOf(i));
			model.put("a2i", String.valueOf(2 * i));
			model.put("ci", i * 1.5);
			
			//STORE THE MODEL.
			dataStore.put(model);
		}
		
		//TEST UPDATE PARTIALLY
		model = schema.createModel(new DataKey("xxxx"));
		model.set("ai", "1");
		model.set("a2i", "1");
		model.set("ci", 1);
		
		dataStore.put(model);
		model = dataStore.get(new DataKey("xxxx"));
		assertNotNull(model.get("ci"));
		assertNotNull(model.get("ai"));
		
		
		//OK, TRY TO QUERY
		for(int i = 0; i < 100; i ++) {
			model = dataStore.get(new DataKey("id-" + i));
			
			assertEquals(model.get("ai"), String.valueOf(i));
			assertEquals(model.get("a2i"), String.valueOf(2 * i));
		}
		
		//test to list
		List<Dictionary> result = dataStore.fetch(Objects.asList(
				new DataKey("id-0"),
				new DataKey("id-3"),
				new DataKey("id-4"),
				new DataKey("d000")));
		assertEquals(3, result.size());
		
		//DUMP RESULT...
		System.out.println(result);
		
		//TEST MISSING COLUMN
		model = dataStore.get(new DataKey("id-0"), "ai", "a2i");
		System.out.println(model);
	}
	
	public void testRangeKey() {
		DataSchema<Dictionary> schema = new DataSchema<Dictionary>("table2", "id", "timestamp",
				new DataMapper.MAP<Dictionary>(Dictionary.class,
									"id", DataType.UTF8,
									"timestamp", DataType.INTEGER,
									 "ai", DataType.UTF8,
									"a2i", DataType.UTF8));
		
		assertTrue(schema.getTable().equals("table2"));
		assertTrue(schema.getHashKey().equals("id"));
		
		//CREATE A TABLE
		DataStore<Dictionary> dataStore = dataManager.bindStore(schema);
		
		Dictionary model;
		for(int i = 0; i < 10; i ++) {
			for(int j = 0; j < 10; j ++) {
				model = schema.createModel(new DataKey("id-" + i, j));
				
				model.put("ai",  String.valueOf(i));
				model.put("a2i", String.valueOf(2 * i));
				
				//STORE THE MODEL.
				dataStore.put(model);
			}
		}
		
		//OK, TRY TO QUERY
		for(int i = 0; i < 10; i ++) {
			model = dataStore.get(new DataKey("id-" + i, i));
			assertEquals(model.get("ai"), String.valueOf(i));
			assertEquals(model.get("a2i"), String.valueOf(2 * i));
		}
		
		//test to list
		List<Dictionary> models = dataStore.fetch(Objects.asList(
				new DataKey("id-0", 0),
				new DataKey("id-3", 3),
				new DataKey("id-4", 4),
				new DataKey("d000", 0)));
		assertEquals(3, models.size());
		
		//Test slice => NEED RANGE KEY
		DataResult<Dictionary> result = dataStore.query("id-5", DataCondition.GE(5), new DataRange(3));
		assertEquals(3, result.count());
		System.out.println(result);
		
		//NEXT PAGE
		result = dataStore.query("id-5", DataCondition.GE(5), new DataRange(3, false, result.lastEvaluatedKey()));
		System.out.println(result);
	}
	
	public void testInsertOnly() {
		DataSchema<Dictionary> schema = new DataSchema<Dictionary>("table3", "id", "timestamp",
				new DataMapper.MAP<Dictionary>(Dictionary.class,
									"id", DataType.UTF8,
									"timestamp", DataType.INTEGER,
									"ai", DataType.UTF8,
									"a2i", DataType.UTF8));
		
		assertTrue(schema.getTable().equals("table3"));
		assertTrue(schema.getHashKey().equals("id"));
		
		//CREATE A TABLE
		DataStore<Dictionary> dataStore = dataManager.bindStore(schema);
		
		Dictionary model;
		for(int i = 0; i < 10; i ++) {
			for(int j = 0; j < 10; j ++) {
				model = schema.createModel(new DataKey("id-" + i, j));
				
				model.put("ai",  String.valueOf(i));
				model.put("a2i", String.valueOf(2 * i));
				
				//STORE THE MODEL.
				dataStore.put(model);
			}
		}
		
		//OK, TRY TO QUERY
		for(int i = 0; i < 10; i ++) {
			model = dataStore.get(new DataKey("id-" + i, i));
			assertEquals(model.get("ai"), String.valueOf(i));
			assertEquals(model.get("a2i"), String.valueOf(2 * i));
		}
		
		//test to list
		List<Dictionary> models = dataStore.fetch(Objects.asList(
				new DataKey("id-0", 0),
				new DataKey("id-3", 3),
				new DataKey("id-4", 4),
				new DataKey("d000", 0)));
		assertEquals(3, models.size());
		
		//Test slice => NEED RANGE KEY
		DataResult<Dictionary> result = dataStore.query("id-5", DataCondition.GE(5), new DataRange(3));
		assertEquals(3, result.count());
		System.out.println(result);
		
		//NEXT PAGE
		result = dataStore.query("id-5", DataCondition.GE(5), new DataRange(3, false, result.lastEvaluatedKey()));
		System.out.println(result);
		
		//MAKE SURE TO DROP THE TABLE.
		//dataManager.dropStore(schema.getTable());
	}
	
	/**
	 * 
	 */
	public void testNull() {
		DataSchema<Dictionary> schema = new DataSchema<Dictionary>("table4", "id",
				new DataMapper.MAP<Dictionary>(Dictionary.class,
						"id", DataType.UTF8,
						"name", DataType.UTF8,
						"value", DataType.UTF8,
						"action", DataType.UTF8,
						"data", DataType.BYTEB));

		assertTrue(schema.getTable().equals("table4"));
		assertTrue(schema.getHashKey().equals("id"));
		
		//CREATE A TABLE
		DataStore<Dictionary> dataStore = dataManager.bindStore(schema);
		Dictionary model = new Dictionary();
		model.set("id", "id-123");
		model.set("name", "hohoho");
		model.set("value", "1234");
		model.set("action", "test-123");
		
		dataStore.put(model);
		Dictionary model1 = dataStore.get(new DataKey("id-123"));
		assertEquals(model.get("name"), model1.get("name"));
		assertEquals(model.get("value"), model1.get("value"));
		assertEquals(model.get("action"), model1.get("action"));
		
		//RESET
		model.set("action", null);
		dataStore.put(model);
		model1 = dataStore.get(new DataKey("id-123"));
		assertNotNull(model1.get("action"));	//DOESN'T CHANGE VALE
		
		//ONLY SAVE ACTION + ID
		model.set("id", "id-xxx");
		model.set("action", "xxx");
		dataStore.put(model, "action");
		model1 = dataStore.get(new DataKey("id-xxx"));
		assertEquals(model1.get("id"), "id-xxx");
		assertEquals(model1.get("action"), "xxx");
		assertNull(model1.get("value"));
		
		//SET DATA BLOB
		Dictionary blob = new Dictionary();
		blob.set("id", "id-blob");
		blob.set("data", "dsds  dss dsdsdsdsd dss ds".getBytes());
		dataStore.put(blob);
		
		blob = dataStore.get(new DataKey("id-blob"));
		System.out.println("XXXXXX:" + blob.get("data").getClass());
	}
	
	/**
	 * 
	 */
	public void testScan() {
		DataSchema<Dictionary> schema = new DataSchema<Dictionary>("table5", "id",
				new DataMapper.MAP<Dictionary>(Dictionary.class, 
									"id", DataType.UTF8,
									"name", DataType.UTF8,
									"ai", DataType.UTF8));
	
		assertTrue(schema.getTable().equals("table5"));
		assertTrue(schema.getHashKey().equals("id"));
		
		//CREATE A TABLE
		DataStore<Dictionary> dataStore = dataManager.bindStore(schema);
		
		Dictionary model;
		for(int i = 0; i < 50; i ++) {
			model = schema.createModel(new DataKey("id-" + i));
			model.put("ai",  String.valueOf(i));
			model.put("name", String.valueOf(2 * i));
			
			//STORE THE MODEL.
			dataStore.put(model);
		}
		Map<String, DataCondition> conditions = Objects.asMap("id", DataCondition.EQ("id-0"));
		DataResult<Dictionary> result = dataStore.scan(conditions, null);
		assertTrue(result.count() == 1);
	}
}
