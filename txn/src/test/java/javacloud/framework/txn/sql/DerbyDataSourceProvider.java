package javacloud.framework.txn.sql;

import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javacloud.framework.cdi.SingletonProvider;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class DerbyDataSourceProvider extends SingletonProvider<DataSource> {
	@Override
	protected DataSource create() {
		EmbeddedDataSource dataSource = new EmbeddedDataSource();
		dataSource.setDatabaseName("target/derbydb");
		dataSource.setCreateDatabase("create");
		return dataSource;
	}
}
