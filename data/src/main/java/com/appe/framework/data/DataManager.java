package com.appe.framework.data;

/**
 * Simple data manager interface, it's not intended to using as FULL DBMS, JUST QUICK WAY TO GET DATASTORE OBJECT.
 * TODAY we just need to bindStore() when needing a database and a quick ways to DROP any DATA table.
 * @author tobi
 *
 */
public interface DataManager {
	/**
	 * Take everything as default of default...
	 * @param schema
	 * @return
	 */
	public <T> DataStore<T> bindStore(DataSchema<T> schema);
	
	/**
	 * Create table only if not exist, b/c it takes time to create table this might block for awhile.
	 * Physical table is not always create, just does that to register with the system locally.
	 * 
	 * Optimization flags just effect for first time, if some how u want to change that it might not effect.
	 * SO MAKE SURE TO THINK ABOUT STUFF TWICE.
	 * 
	 * @param schema
	 * @param options
	 * @return
	 */
	public <T> DataStore<T> bindStore(DataSchema<T> schema, int options);
	
	/**
	 * Drop the table if any exist, physically!!!
	 * 
	 * @param table
	 * @return
	 */
	public void dropTable(String table);
}
