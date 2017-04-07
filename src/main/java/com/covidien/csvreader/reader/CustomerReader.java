package com.covidien.csvreader.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;


import com.covidien.csvreader.model.Customer;
import com.covidien.dbstore.DBConnection;
import com.covidien.dbstore.DBSetup;
import com.covidien.dbstore.DBUtiltityFunctions;

public class CustomerReader {

	
	boolean fileCopied = false;

	private StringBuffer node= null;
	private StringBuffer node_revisions= null;
	private StringBuffer content_type_bu_customer= null;
	private StringBuffer content_type_party= null;
	private StringBuffer content_type_party_voice_address= null;
	private StringBuffer customer_responce_status = null;

	private DBConnection dbConnection;

	public CustomerReader(String host, String dbName, 
			String user, String password, String port) {
		init();
		//dbConnection = new DBConnection(dbName, host, password, user, port);
		dbConnection = new DBConnection(dbName, host, password, user, port);
	}

	private void init() {
		node= new StringBuffer();
		node_revisions= new StringBuffer();
		content_type_bu_customer= new StringBuffer();
		content_type_party= new StringBuffer();
		content_type_party_voice_address= new StringBuffer();
		customer_responce_status = new StringBuffer();

	}

	/**
	 * Customer CSV File Reader Function.
	 */

	private void moveFile(String path) throws IOException {
		File filePath = new File(path);
		File successFolder = new File(path.substring(0, path.lastIndexOf(File.separator)) + File.separator + "success");
		
		if(!successFolder.exists()) {
			successFolder.mkdir();
		}
		
		InputStream in = new FileInputStream(new File(path));
		OutputStream out = new FileOutputStream(new File(successFolder.getAbsolutePath() + File.separator + filePath.getName()));
		
		byte[] buffer = new byte[10240];
		int length;
	    //copy the file content in bytes 
	    while ((length = in.read(buffer)) > 0){
	    	out.write(buffer, 0, length);
	    }

	    in.close();
	    out.close();
	    
	    fileCopied = true;

	}

	private void appendPartyVoiceAddress(long nid, long vid, long cusomerNid, 
			long etlAdmin, String type, Customer customer) {
		node_revisions.append("(" + nid + "," + etlAdmin + ",'" +customer.getNAME().replace("'","\\\'") + "','','','',unix_timestamp(),0),");
		node.append("('" + vid + "','party_voice_address','','" + customer.getNAME().replace("'","\\\'") + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

		if(type.equals("phone")) {
			content_type_party_voice_address.append("('" + nid + "','" + vid + "','" + cusomerNid +"','phone','" + customer.getPHONE() + "'),");
		} else if (type.equals("fax")) {
			content_type_party_voice_address.append("('" + nid + "','" + vid + "','" + cusomerNid + "','fax','" + customer.getFAX() + "'),");
		}
	}

	private void appendCustomerIdInserts(long nid, long vid, 
			long etlAdmin, long customerNid, Customer customer) {
		node_revisions.append("(" + nid + "," + etlAdmin + ",'" + customer.getCUSTOMER_ID() + "','','','',unix_timestamp(),0),");
		node.append("('" +vid + "','bu_customer','','" + customer.getCUSTOMER_ID() + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
		content_type_bu_customer.append("('" + vid + "','" + nid + "','" + customer.getCUSTOMER_ID() + "','" + customerNid + "'),");

	}

	private void appendCustomerNameInserts(long nid, Customer customer, 
			long vid, long customertype_nid, long etlAdmin) {
		node_revisions.append("(" + nid + "," + etlAdmin + ",'" +customer.getNAME().replace("'","\\'") + "','','','',unix_timestamp(),0),");  				
		node.append("("+vid+",'party','','" + customer.getNAME().replace("'","\\'") + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
		content_type_party.append("('" + vid +"','" + nid + "','" +customertype_nid +"'),");
	}

	private void batchInserts(Connection con) {
		Savepoint save = null;
		Statement stmt = null;
		try {
			save = con.setSavepoint();
			stmt = con.createStatement();
			stmt.executeUpdate("insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values " 
					+ node_revisions.substring(0, node_revisions.length()-1));
			stmt.executeUpdate("insert into node (vid,type,language,title,uid,status,created,changed,comment,promote," +
					"translate) values " + node.substring(0, node.length()-1));
			stmt.executeUpdate("insert into content_type_party (vid,nid,field_party_type_nid) values " + 
					content_type_party.substring(0, content_type_party.length()-1));
			stmt.executeUpdate("insert into content_type_bu_customer (vid,nid,field_bu_customer_account_number_value," +
					"field_customer_party_pk_nid) values " + content_type_bu_customer.substring(0, content_type_bu_customer.length()-1));
			if(content_type_party_voice_address.length() != 0 ) {
				stmt.executeUpdate("insert into content_type_party_voice_address (vid,nid,field_voice_party_pk_nid, " +
						"field_voice_type_value, field_voice_address_value) values " + 
						content_type_party_voice_address.substring(0, content_type_party_voice_address.length()-1));
			}
			stmt.executeUpdate("insert into customer_responce_status (customer_id, nid, status) values " + customer_responce_status.substring(0, customer_responce_status.length() - 1));
			con.commit();
			con.releaseSavepoint(save);
		} catch (SQLException ex) {
			if(save != null) {
				try {
					con.rollback(save);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		CustomerReader customerReader = new CustomerReader("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		System.out.println(System.currentTimeMillis());
	}
	
}
