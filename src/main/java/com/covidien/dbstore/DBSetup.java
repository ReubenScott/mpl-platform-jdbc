package com.covidien.dbstore;

public interface DBSetup {
	
	public String table1 = "CREATE TABLE if not exists customer_responce_status (nid int(10) unsigned DEFAULT NULL, " +
			"customer_id varchar(30) DEFAULT NULL, status varchar(15) DEFAULT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	public String table2 = "CREATE TABLE if not exists  location_role_responce_status (nid int(10) unsigned " +
			"DEFAULT NULL, customer_id varchar(30) DEFAULT NULL, location_id varchar(30) " +
			"DEFAULT NULL, status varchar(15) DEFAULT NULL ) ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	public String table3 = "CREATE TABLE if not exists  location_responce_status (nid int(10) " +
			"unsigned DEFAULT NULL, location_id varchar(30) DEFAULT NULL, " +
			"status varchar(15) DEFAULT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	public String table4 = "CREATE TABLE if not exists  device_responce_status (nid int(10) unsigned DEFAULT NULL, " +
			"serial_no varchar(30) DEFAULT NULL, status varchar(15) DEFAULT NULL)  " +
			"ENGINE=MyISAM DEFAULT CHARSET=utf8";
	
	public String index2 = "create index location_responce_status_location_id" +
			" on location_responce_status(location_id);";
	
	public String index1 = "create index customer_responce_status_customer_id " +
			"on customer_responce_status(customer_id)";
	
	public String alter1 = "alter table device_responce_status add column sku varchar(30) DEFAULT NULL";
	
	public String checkTableDRS = "desc device_responce_status";
	
}
