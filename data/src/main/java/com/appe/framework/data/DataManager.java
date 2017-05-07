package com.appe.framework.data;

/**
 * Simple data manager interface, it's not intended to using as FULL DBMS, JUST QUICK WAY TO GET DATASTORE OBJECT.
 * TODAY we just need to bindStore() when needing a database and a quick ways to DROP any DATA table.
 * @author tobi
 *
 */
public interface DataManager {
	/**
	 * Create table only if not exist, b/c it takes time to create table this might block for awhile.
	 * Physical table is not always create, just does that to register with the system locally.
	 * 
	 * @param schema
	 * @return
	 */
	public <T> DataStore<T> bindStore(DataSchema<T> schema);
	
	/**
	 * Drop the table if any exist, physically!!!
	 * 
	 * @param table
	 * @return
	 */
	public void dropTable(String table);
}
