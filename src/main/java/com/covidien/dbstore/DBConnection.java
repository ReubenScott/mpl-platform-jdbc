package com.covidien.dbstore;


import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Create db connection based on the configuration properties.
 * @author Prakash
 *
 */
public final class DBConnection {
		
	/**
	 * MySQL datasource to be used.
	 */
	public static MysqlDataSource datasource;
	
	private String dbName;
	
	private String host;
	
	private String user;
	
	private String password;
	
	private String port;
	
	/**
	 * Private constructor for singleton implementation.
	 */
	public DBConnection(String dbName, String host, 
			String password, String user, String port) {
		this.dbName = dbName;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	/**
	 * Create new datasource if there is one not already available.
	 * @return MySQLDataSource object.
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		if (datasource == null) {
			datasource = new MysqlDataSource();
			datasource.setDatabaseName(this.dbName);
			datasource.setUser(this.user);
			datasource.setPassword(this.password);
			datasource.setServerName(this.host);
			datasource.setPort(Integer.parseInt(this.port));
		}
		return datasource.getConnection();
	}

}
