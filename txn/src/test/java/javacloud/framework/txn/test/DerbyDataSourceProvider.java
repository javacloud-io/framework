package javacloud.framework.txn.test;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javacloud.framework.util.LazySupplier;

/**
 * 
 * @author ho
 *
 */
@Singleton
public class DerbyDataSourceProvider extends LazySupplier<DataSource> implements Provider<DataSource> {
	@Override
	protected DataSource newInstance() {
		EmbeddedDataSource dataSource = new EmbeddedDataSource();
		dataSource.setDatabaseName("target/derbydb");
		dataSource.setCreateDatabase("create");
		return dataSource;
	}
}
