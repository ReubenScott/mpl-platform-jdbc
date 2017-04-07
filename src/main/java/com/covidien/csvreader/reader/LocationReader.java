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
import java.util.HashMap;


import com.covidien.csvreader.model.Location;
import com.covidien.dbstore.DBConnection;
import com.covidien.dbstore.DBSetup;
import com.covidien.dbstore.DBUtiltityFunctions;

public class LocationReader {

	//private static final String LOCATION_CSV_FILEPATH = "C:\\Users\\Prakash\\Desktop\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V3\\LOCATION.csv";


	boolean fileCopied = false;

	HashMap<String, String> countryCodeMap = new HashMap<String, String>();

	//StringBuffer buffer = null;
	StringBuffer nodeRevisions = null;
	StringBuffer node = null;
	StringBuffer contentTypePostalAddress = null;
	StringBuffer contentFieldIsActive = null;
	StringBuffer contentFieldSortSeq = null;
	StringBuffer contentTyepCountry = null;

	StringBuffer locationResponse = null;

	private DBConnection dbConnection;

	public LocationReader(String host, String dbName, 
			String user, String password, String port) {
		//buffer = new StringBuffer();
		countryCodeMap.put("US", "United States");

		dbConnection = new DBConnection(dbName, host, password, user, port);

		init();

	}

	private void init() {
		nodeRevisions = new StringBuffer();
		node = new StringBuffer();
		contentTypePostalAddress = new StringBuffer();
		contentFieldIsActive = new StringBuffer();
		contentFieldSortSeq = new StringBuffer();
		contentTyepCountry = new StringBuffer();
		locationResponse = new StringBuffer();
	}

	/**
	 * Location CSV File Reader Function.
	 */
	public void readLocationCSVFile(String path) {

		try {
			if (!new File(path).exists()) {
				return;
			}

			// the header elements are used to map the values to the bean (names must match)

			Location location;
			Connection con = dbConnection.getConnection();
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(DBSetup.table3);
			con.commit();
			
			if(DBUtiltityFunctions.checkAnyLocationRecordAddedByETL(stmt)) {
				return;
			}
			ResultSet indexResult = stmt.executeQuery("show index from location_responce_status");
			if(!indexResult.next()) {
				stmt.executeUpdate(DBSetup.index2);
			}
			long nid = DBUtiltityFunctions.getLatestNid(stmt);
			long vid = DBUtiltityFunctions.getLatestVid(stmt);
			long etlAdmin = DBUtiltityFunctions.getUserId(stmt, "GWetl.admin@covidien.com");
			//String sqlQuery = "INSERT INTO location (location_id,address_line1_value,address_line2_value,address_line3_value,address_line4_value,address_line5_value," +
			//		"address_city_value,state_province_value,postal_code_value,country_code_value,last_publish_date,status) values ";
			int loopCount = 0;
			int totalCount =0;
			long countryNid;
			boolean countryCreate = false;
			if(loopCount > 0) {
				//				stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
				insertBatch(con);
				totalCount = totalCount + loopCount;
				System.out.println("Insert Total : " + totalCount);
				init();
			} 
			moveFile(path);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		}
	}
	
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

	/*private void stageingDB(Location location) {
		buffer.append(" ('"+ location.getLOCATION_ID() +"' , ");
			if( location.getADDRESS_LINE1()==null || location.getADDRESS_LINE1().equals("null") ) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getADDRESS_LINE1().replace("'", "\\'") +"' , ");  					
			}

			if( location.getADDRESS_MODIFIER_1()==null || location.getADDRESS_MODIFIER_1().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getADDRESS_MODIFIER_1().replace("'", "\\'") +"' , ");
			}

			if( location.getADDRESS_MODIFIER_2()==null || location.getADDRESS_MODIFIER_2().equals("null") ) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getADDRESS_MODIFIER_2().replace("'", "\\'") +"' , ");
			}

			if( location.getADDRESS_MODIFIER_3()==null || location.getADDRESS_MODIFIER_3().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getADDRESS_MODIFIER_3().replace("'", "\\'") +"' , ");
			}

			if( location.getADDRESS_MODIFIER_4()==null || location.getADDRESS_MODIFIER_4().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getADDRESS_MODIFIER_4().replace("'", "\\'") +"' , ");
			}

			if( location.getCITY()==null || location.getCITY().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getCITY().replace("'", "\\'") +"' , ");
			}

			if( location.getSTATE_PROVINCE()==null || location.getSTATE_PROVINCE().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getSTATE_PROVINCE() +"' , ");
			}

			if( location.getPOSTAL_CODE()==null || location.getPOSTAL_CODE().equals("null")) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getPOSTAL_CODE() +"' , ");
			}

			if( location.getCOUNTRY_CODE()==null || location.getCOUNTRY_CODE().equals("null") ) {
				buffer.append("'',");
			} else {
				buffer.append(" '"+ location.getCOUNTRY_CODE() +"' , ");
			}

			buffer.append(" '"+ location.getLAST_PUBLISH_DATE() +"' , ");
			buffer.append("0),");
	}*/

	private void insertBatch(Connection con) {
		Savepoint save = null;
		Statement stmt = null;
		try {
			save = con.setSavepoint();
			stmt = con.createStatement();
			stmt.executeUpdate("insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values " 
					+ nodeRevisions.substring(0, nodeRevisions.length()-1));
			stmt.executeUpdate("insert into node (vid,type,language,title,uid,status,created,changed,comment,promote," +
					"translate) values " + node.substring(0, node.length()-1));
			stmt.executeUpdate("insert into content_type_postal_address (nid, vid, field_postal_address_line1_value," +
					"field_postal_address_city_value, field_state_province_value, field_postal_code_value, field_postal_code_country_nid) values " +
					contentTypePostalAddress.substring(0, contentTypePostalAddress.length()-1));
			if(contentTyepCountry.length() > 0) {
				stmt.executeUpdate("insert into content_type_country (nid, vid, field_iso_3166_2lcode_value) values " +
						contentTyepCountry.substring(0, contentTyepCountry.length()-1));
				stmt.executeUpdate("insert into content_field_is_active (nid, vid) values " +
						contentFieldIsActive.substring(0, contentFieldIsActive.length()-1));
				stmt.executeUpdate("insert into content_field_sort_sequence (nid, vid) values " +
						contentFieldSortSeq.substring(0, contentFieldSortSeq.length()-1));			
			}
			stmt.executeUpdate("insert into location_responce_status (nid, location_id, status) values " + 
					locationResponse.substring(0, locationResponse.length()-1));
			con.commit();
			con.releaseSavepoint(save);
		} catch (SQLException e) {
			if(save != null) {
				try {
					con.rollback(save);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	private void insertCountry(long nid, long vid, String countryCode, long etlAdmin) {
		nodeRevisions.append("(" + nid + "," + etlAdmin + ",'" +countryCodeMap.get(countryCode) + "','','','',unix_timestamp(),0),");
		node.append("(" + vid +",'postal_address','','" + countryCodeMap.get(countryCode) + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
		contentTyepCountry.append("(" + nid + "," + vid + ",'" + countryCode + "'),");
		contentFieldIsActive.append("(" + nid + "," + vid + "),");
		contentFieldSortSeq.append("(" + nid + "," + vid + "),");
	}

	public static void main(String[] args) {
		LocationReader locationReader = new LocationReader("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		locationReader.readLocationCSVFile("F:\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V4\\GATEWAY_EXTRACT_V4\\success1\\Testcase1\\LOCATION.csv");
	}	

}
