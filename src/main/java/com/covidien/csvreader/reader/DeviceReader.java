package com.covidien.csvreader.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.covidien.csvreader.model.Device;
import com.covidien.dbstore.DBConnection;
import com.covidien.dbstore.DBSetup;
import com.covidien.dbstore.DBUtiltityFunctions;

public class DeviceReader {
	
	
	static class DeviceTypeKey {
		
		public DeviceTypeKey(String sku, String sourceSystem, String serailNumberValidation) {
			this.sku = sku;
			this.sourceSystem = sourceSystem;
			this.serailNumberValidation = serailNumberValidation;
		}
		
		String sku;
		
		String sourceSystem;
		
		String serailNumberValidation;

		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public String getSourceSystem() {
			return sourceSystem;
		}

		public void setSourceSystem(String sourceSystem) {
			this.sourceSystem = sourceSystem;
		}

		public String getSerailNumberValidation() {
			return serailNumberValidation;
		}

		public void setSerailNumberValidation(String serailNumberValidation) {
			this.serailNumberValidation = serailNumberValidation;
		}
	}


	

	static HashMap<String, ArrayList<DeviceTypeKey>> deviceTypeSKUMap = new HashMap<String, ArrayList<DeviceTypeKey>>();
	
	ArrayList<DeviceTypeKey> emptySkus = new ArrayList<DeviceReader.DeviceTypeKey>();

	boolean fileCopied = false;

	//StringBuffer buffer = null;

	StringBuffer nodeRevisions = null;
	StringBuffer node = null;
	StringBuffer deviceStr = null;
	StringBuffer contentFieldActivation = null;
	StringBuffer contentFieldExpiration = null;
	StringBuffer contentFieldActivationUTC = null;
	StringBuffer contentFieldExpirationUTC = null;
	StringBuffer contentFieldDeviceType = null;

	StringBuffer deviceInstallation = null;
	StringBuffer contentFieldDevice = null;
	StringBuffer contentFieldFacility = null;

	StringBuffer deviceServiceHistory = null;

	StringBuffer deviceResponse = null;

	HashMap<String, String> countryCodeMap = new HashMap<String, String>();

	private DBConnection dbConnection;

	public DeviceReader(String host, String dbName, 
			String user, String password, String port) {
		init();
		dbConnection = new DBConnection(dbName, host, password, user, port);
	}

	private void init() {
		//buffer = new StringBuffer();
		nodeRevisions = new StringBuffer();
		node = new StringBuffer();
		deviceStr = new StringBuffer();
		contentFieldActivation = new StringBuffer();
		contentFieldExpiration = new StringBuffer();
		contentFieldActivationUTC = new StringBuffer();
		contentFieldExpirationUTC = new StringBuffer();
		contentFieldDeviceType = new StringBuffer();

		deviceInstallation = new StringBuffer();
		contentFieldDevice = new StringBuffer();
		contentFieldFacility = new StringBuffer();

		deviceServiceHistory = new StringBuffer();

		deviceResponse = new StringBuffer();

		countryCodeMap.put("US", "United States");
	}
	/**
	 * Check the sku column exists .
	 * will be removed in next phase .
	 * @return boolean .
	 */
	private boolean checkColumnExists(){
		Connection con = null;
		try {
			con = dbConnection.getConnection();
			PreparedStatement stmt = null;
			ResultSet result = null;
			stmt = con.prepareStatement(DBSetup.checkTableDRS);
			if (stmt.execute()) {
				result = stmt.getResultSet();
				if (result != null) {
					while(result.next()) {
						if(result.getString("field").equals("sku")){
							return true;
						}
					}
				}
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Device CSV File Reader Function.
	 */
	public void readDeviceCSVFile(String path) {

		Connection con = null;
		try {
			if (!new File(path).exists()) {
				return;
			}

			Device device;
			con = dbConnection.getConnection();
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			//String sqlQuery = "INSERT INTO device (source_system,sku,serial_number,name,description,maintanance_expiration_date,install_country_code," +
			//"customer_id,last_publish_date,location_id,status) values ";
			stmt.executeUpdate(DBSetup.table4);
			if(!checkColumnExists()){
				System.out.println(DBSetup.table4);
				con.commit();
				try {
					stmt.executeUpdate(DBSetup.alter1);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				
				con.commit();
			}
			int loopCount = 0;
			int totalCount =0;

			long nid = DBUtiltityFunctions.getLatestNid(stmt);
			long vid = DBUtiltityFunctions.getLatestVid(stmt);
			String deviceType = null;

			long deviceInstallationNid;
			long serviceTypeNid = DBUtiltityFunctions.getServiceTypeNid(stmt, "Device Registration");
			long personNid = DBUtiltityFunctions.getPersonNid(stmt, "GWetl.admin@covidien.com");
			long etlAdmin = DBUtiltityFunctions.getUserId(stmt, "GWetl.admin@covidien.com");
			HashMap<String, Long> deviceTypeMapNid = new HashMap<String, Long>();
			HashMap<String, Long> customerMapNid = new HashMap<String, Long>();
			HashMap<String, Long> countryCodeNid = new HashMap<String, Long>();
			HashMap<String, Long> locationMapNid = new HashMap<String, Long>();
			long unknownCustomer = DBUtiltityFunctions.getunknownCustomerNid(stmt);
			String serialNumber = null;
			
			
			//Will Commented out later.
			String directory = path.substring(0, path.lastIndexOf(File.separator));
			final String[] writeHeader = new String[]{"SOURCE_SYSTEM","SKU","SERIAL_NUMBER","MAINTENANCE_EXPIRATION_DATE",
					"CUSTOMER_ID","LAST_PUBLISH_DATE","LOCATION_ID"};
			
			int invalidDevice = 0;
			int duplicateDevice = 0;
			int validDevice = 0;
			HashMap<String, ArrayList<String>> deviceTypeSerial = new HashMap<String, ArrayList<String>>();
			
			if(loopCount > 0) {
				//	stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
				batchInserts(con);
				totalCount = totalCount + loopCount;
				System.out.println("Total records : " + totalCount);
				System.out.println("Valid records : " + validDevice);
				System.out.println("Invalid records :" + invalidDevice);
				System.out.println("Duplication records :" + duplicateDevice);
				init();
			} 
			moveFile(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
	    fileCopied = true;

	    in.close();
	    out.close();
	}

	/*private void appendForStageingDB(Device device) {
		buffer.append(" ('"+ device.getSOURCE_SYSTEM() +"' , ");
		buffer.append(" '"+ device.getSKU() +"' , ");  				
		if( device.getSERIAL_NUMBER()==null || device.getSERIAL_NUMBER().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getSERIAL_NUMBER() +"' , ");  					
		}
		if( device.getNAME()==null || device.getNAME().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getNAME().replace("'", "\\'") +"' , ");  					
		}
		if( device.getDESCRIPTION()==null || device.getDESCRIPTION().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getDESCRIPTION().replace("'", "\\'") +"' , ");  					
		}
		if( device.getMAINTENANCE_EXPIRATION_DATE()==null || device.getMAINTENANCE_EXPIRATION_DATE().equals("null") ) {
			buffer.append("'0000.00.00',");
		} else {
			buffer.append(" '"+ device.getMAINTENANCE_EXPIRATION_DATE() +"' , ");  					
		}
		if( device.getINSTALL_COUNTRY_CODE()==null || device.getINSTALL_COUNTRY_CODE().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getINSTALL_COUNTRY_CODE().replace("'", "\\'") +"' , ");  					
		}
		buffer.append(" '"+ device.getCUSTOMER_ID() +"' , ");
		buffer.append(" '"+ device.getLAST_PUBLISH_DATE() +"' , ");
		buffer.append(" '"+ device.getLOCATION_ID() +"' , ");
		buffer.append("0),");
	}*/

	private void batchInserts(Connection con) {
		Savepoint save = null;
		Statement stmt = null;
		try {
			save = con.setSavepoint();
			stmt = con.createStatement();
			stmt.addBatch("insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values " 
					+ nodeRevisions.substring(0, nodeRevisions.length()-1));
			stmt.addBatch("insert into node (vid,type,language,title,uid,status,created,changed,comment,promote," +
					"translate) values " + node.substring(0, node.length()-1));
			stmt.addBatch("insert into content_type_device (nid, vid, field_device_serial_number_value, " +
					"field_device_is_active_value, field_device_owner_nid, field_maintance_expiration_date_value) values " +
					deviceStr.substring(0, deviceStr.length()-1));
			stmt.addBatch("insert into content_field_device_type (nid, vid, field_device_type_nid) values " +
					contentFieldDeviceType.substring(0, contentFieldDeviceType.length()-1));
			stmt.addBatch("insert into content_type_device_installation (nid, vid, field_device_country_nid, field_location_id_nid) values" +
					deviceInstallation.substring(0, deviceInstallation.length()-1));
			stmt.addBatch("insert into content_field_device_pk (nid, vid, field_device_pk_nid) values " +
					contentFieldDevice.substring(0, contentFieldDevice.length()-1));
			stmt.addBatch("insert into content_field_facility_pk (nid, vid) values " +
					contentFieldFacility.substring(0, contentFieldFacility.length()-1));
			stmt.addBatch("insert into content_field_activation_datetime (nid, vid) " +
					"values " + contentFieldActivation.substring(0, contentFieldActivation.length()-1));
			stmt.addBatch("insert into content_field_expiration_datetime (nid, vid) " +
					"values " + contentFieldExpiration.substring(0, contentFieldExpiration.length()-1));
			stmt.addBatch("insert into content_field_activation_utc_offset (nid, vid) " +
					"values " + contentFieldActivationUTC.substring(0, contentFieldActivationUTC.length()-1));
			stmt.addBatch("insert into content_field_expiration_utc_offset (nid, vid) " +
					"values " + contentFieldExpirationUTC.substring(0, contentFieldExpirationUTC.length()-1));
			stmt.addBatch("insert into content_type_device_service_history (nid, vid, field_device_installation_pk_nid, " +
					"field_device_service_type_nid, field_service_person_pk_nid, field_service_note_value, field_service_datetime_value) values " +
					deviceServiceHistory.substring(0, deviceServiceHistory.length()-1));
			stmt.addBatch("insert into device_responce_status (nid, serial_no, sku, status) values " +
					deviceResponse.substring(0, deviceResponse.length()-1));
			stmt.executeBatch();
			con.commit();
			con.releaseSavepoint(save);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(save != null) {
				try {
					con.rollback(save);
					con.commit();
					con.releaseSavepoint(save);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}			
		} finally {
			
		}
	}

	private String getDeviceType(Statement stmt, String sku, String sourceSystem) throws SQLException {
		String deviceType = null;
		String serialKeyValidation = null;
		for(DeviceTypeKey key : emptySkus) {
			if(key.getSku().equals(sku) && key.getSourceSystem().equals(sourceSystem)) {
				return deviceType;
			}
		}
		Set<String> deviceTypes = deviceTypeSKUMap.keySet();
		for(String type : deviceTypes) {
			if(deviceType == null) {
				for(DeviceTypeKey deviceTypeKey : deviceTypeSKUMap.get(type)) {
					if(deviceTypeKey.getSku().equals(sku) && deviceTypeKey.getSourceSystem().equals(sourceSystem)) {
						deviceType = type;
						break;
					}
				}				
			} else {
				break;
			}
		}
		if(deviceType==null) {
			deviceType = DBUtiltityFunctions.getDeviceType(stmt, sku, sourceSystem);
			if(deviceType!=null && deviceType.equals("SCD 700")) {
				serialKeyValidation = DBUtiltityFunctions.getSerialNumberValidation(stmt, deviceType);				
			}
			if(deviceType != null) {
				if(!deviceTypeSKUMap.containsKey(deviceType)) {
					deviceTypeSKUMap.put(deviceType, new ArrayList<DeviceReader.DeviceTypeKey>());
				}
				deviceTypeSKUMap.get(deviceType).add(new DeviceTypeKey(sku, sourceSystem, serialKeyValidation));
			} else {
				emptySkus.add(new DeviceTypeKey(sku, sourceSystem, serialKeyValidation));
			}
		}
		return deviceType;
	}

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		DeviceReader deviceReader = new DeviceReader("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		deviceReader.readDeviceCSVFile("F:\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V4\\GATEWAY_EXTRACT_V4\\success1\\Testcase1\\DEVICE.csv");
		System.out.println(System.currentTimeMillis());
	}
}
