package com.covidien.dbstore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBUtiltityFunctions {

	public static long getLatestNid(Statement stmt) throws SQLException {
		long nid = 0;
		ResultSet rs1 = stmt.executeQuery("SHOW TABLE STATUS LIKE 'node'");
		if(rs1.next()) {  				
			nid=rs1.getLong("Auto_increment");
		}
		return nid;
	}

	public static long getLatestVid(Statement stmt) throws SQLException {
		long vid = 0;
		ResultSet rs2 = stmt.executeQuery("SHOW TABLE STATUS LIKE 'node_revisions'");
		if(rs2.next()) {
			vid=rs2.getInt("Auto_increment");
		}
		return vid;
	}

	public static long getCustomerTypeNid(Statement stmt) throws SQLException {
		long customerTypeNid = 0;
		ResultSet rs3 = stmt.executeQuery("select nid from node where title='Customer'");
		if(rs3.next()) {
			customerTypeNid = rs3.getInt("nid");
		}
		return customerTypeNid;
	}
	
	public static long getCustomerNid(Statement stmt, String customerId) throws SQLException {
		long customerNid = 0;
		String sqlQuery = "select nid from customer_responce_status where customer_id='" + customerId + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			customerNid = rs3.getInt("nid");
		}
		return customerNid;
	}
	
	public static long getunknownCustomerNid(Statement stmt) throws SQLException {
		long customerNid = 0;
		String sqlQuery = "select node.nid,node.title from node " +
	"join content_type_party on content_type_party.vid=node.vid " +
	"join content_type_party_type on content_type_party_type.nid=content_type_party.field_party_type_nid " +
	"join node as node1 on node1.vid=content_type_party_type.vid and node1.title='customer' where node.title='Unknown'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			customerNid = rs3.getInt("nid");
		}
		return customerNid;
	}
	
	String sqlQuery = "";
	
	public static long getDeviceTypeNid(Statement stmt, String deviceType) throws SQLException {
		long deviceTypeNid = 0;
		String sqlQuery = "select nid from node where type='devicetype' and title='" + deviceType + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			deviceTypeNid = rs3.getInt("nid");
		}
		return deviceTypeNid;
	}
	
	public static long getCountryNid(Statement stmt, String countryName) throws SQLException {
		long countryNid = 0;
		String sqlQuery = "select nid from node where type='country' and title='" + countryName + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			countryNid = rs3.getInt("nid");
		}
		return countryNid;
	}
	
	public static long getPostalAddressRefNid(Statement stmt, String postalAddressId) throws SQLException {
		long postalAddressNid = 0;
		String sqlQuery = "select nid from location_responce_status where location_id='" + postalAddressId + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			postalAddressNid = rs3.getInt("nid");
		}
		return postalAddressNid;
	}

	public static long getAddressTypeNid(Statement stmt, String location_ROLE) throws SQLException {
		long addressTypeNid = 0;
		String sqlQuery = "select nid from node where type='address_type' and title='" + location_ROLE + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			addressTypeNid = rs3.getInt("nid");
		}
		return addressTypeNid;
	}
	
	public static long getPartyAddressNid(Statement stmt, String customer_id) throws SQLException {
		long partyAddressNid = 0;
		String sqlQuery = "select nid from customer_responce_status where customer_id='" + customer_id + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			partyAddressNid = rs3.getInt("nid");
		}
		return partyAddressNid;
	}

	public static long getServiceTypeNid(Statement stmt, String serviceType) throws SQLException {
		long serviceTypeNid = 0;
		String sqlQuery = "select nid from node where title='"+ serviceType + "' and type='device_service_type'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			serviceTypeNid = rs3.getInt("nid");
		}
		return serviceTypeNid;
	}
	
	public static long getPersonNid(Statement stmt, String personName) throws SQLException {
		long personNid = 0;
		String sqlQuery = "select nid from node where title='" + personName + "' and type='person'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			personNid = rs3.getInt("nid");
		}
		return personNid;
	}

	public static long getUserId(Statement stmt, String userName) throws SQLException {
		long userid = 0;
		String sqlQuery = "select uid from users where mail='" + userName + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			userid = rs3.getInt("uid");
		}
		return userid;
	}

	public static String getDeviceType(Statement stmt, String sku, String sourceSystem) throws SQLException {
		String sqlQuery = "select title from node join content_type_sku on " +
			"content_type_sku.field_device_type_pk_nid=node.nid where " +
			"content_type_sku.field_sku_id_value='" + sku + "'" +
			" and content_type_sku.field_source_system_value='" + sourceSystem + "'";
		String deviceType = null;
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			deviceType = rs3.getString("title");
		}
		return deviceType;
		
	}
	
	public static String getSerialNumberValidation(Statement stmt, String deviceType) throws SQLException {
		String sqlQuery = "select field_serial_number_regex_value from content_type_devicetype " +
				"join node on node.nid=content_type_devicetype.nid where title='" + deviceType + "'";
		String serialNumberRegex = null;
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3.next()) {
			serialNumberRegex = rs3.getString("field_serial_number_regex_value");
		}
		return serialNumberRegex;
	}
	
	public static boolean checkDuplicateSerialNumber(Statement stmt, String deviceType, String serialNumber) throws SQLException {
		String sqlQuery = " select content_type_device.nid from content_type_device " +
				"join content_field_device_type on content_field_device_type.nid=content_type_device.nid " +
				"join node on node.nid=content_field_device_type.field_device_type_nid " +
				"where field_device_serial_number_value='3fe26f78d014e26b' and node.title='PB980_Ventilator'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3 != null && rs3.next() && rs3.getInt("nid") > 0) {
			return true;
		}
		return false;
	}
	
	public static ArrayList<String> getDeviceTypeSerialNumbers(Statement stmt, String deviceType) throws SQLException {
		String sqlQuery = "select field_device_serial_number_value from content_type_device " +
				"join content_field_device_type on content_field_device_type.nid=content_type_device.nid " +
				"join node on node.nid=content_field_device_type.field_device_type_nid " +
				"where node.title='" + deviceType + "'";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		ArrayList<String> serialNumbers = new ArrayList<String>();
		if(rs3 != null) {
			while(rs3.next()) {
				serialNumbers.add(rs3.getString("field_device_serial_number_value"));
			}
		}
		return serialNumbers;
	}
	
	public static boolean checkAnyCustomerRecordAddedByETL(Statement stmt) throws SQLException {
		String sqlQuery = "select count(nid) as count from customer_responce_status";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean checkAnyLocationRecordAddedByETL(Statement stmt) throws SQLException {
		String sqlQuery = "select count(nid) as count from location_responce_status";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean checkAnyLocationRoleRecordAddedByETL(Statement stmt) throws SQLException {
		String sqlQuery = "select count(nid) as count from location_role_responce_status";
		ResultSet rs3 = stmt.executeQuery(sqlQuery);
		if(rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
			return true;
		}
		return false;
	}

}
