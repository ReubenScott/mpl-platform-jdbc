package com.covidien.dbstore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCleanup {

	private DBConnection dbConnection;
	
	StringBuilder nodeRevisions = null;
	StringBuilder node = null;
	
	public DBCleanup(String host, String dbName, 
			String user, String password, String port) {
		//dbConnection = new DBConnection(dbName, host, password, user, port);
		dbConnection = new DBConnection(dbName, host, password, user, port);
	}

	public static void main(String[] args) {
		DBCleanup dbClean = new DBCleanup("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		dbClean.dbNodeCleanup();
	}

	public void dbNodeCleanup() {
	
		try {
			Connection con = dbConnection.getConnection();
			Statement stmt = con.createStatement();
			
			// Cleanup device
			System.out.println("Device started " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			deviceCleanup();
			System.out.println("Device completed " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			
			// Cleanup location_role
			System.out.println("Location Role started " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			locationRoleCleanup();
			System.out.println("Location Role completed " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			
			// Cleanup location
			System.out.println("Location started " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			locationCleanup();
			System.out.println("Location completed " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			
			// Cleanup customer
			System.out.println("Customer started " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
			customerCleanup();		
			System.out.println("Customer completed " + System.currentTimeMillis() + " with current max(nid)" + DBUtiltityFunctions.getLatestNid(stmt));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		nodeRevisions = new StringBuilder();
		node = new StringBuilder();
	}


	private void deviceCleanup() {
		Connection con = null;
		String sqlQuery = "select nid from device_responce_status";
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;

		//Init
		init();

		try {
			con = dbConnection.getConnection();
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			result = stmt.executeQuery(sqlQuery);
			
			int loopCount = 0;
			while (result.next()) {
				nodeRevisions.append(result.getLong("nid") + ",");
				node.append(result.getLong("nid") + ",");

				long deviceInstallationNid = deviceInstallation(con, result.getLong("nid"));

				if(deviceInstallationNid!=0) {
					deviceServiceHistory(con, result.getLong("nid"));					
				}
				loopCount++;
				if(loopCount == 10000) {
					System.out.println("Device : " + loopCount);
					deleteDevice(stmt1);
					loopCount = 0;
					init();
				}
			}
			
			if(loopCount > 0) {
				System.out.println("Device : " + loopCount);
				deleteDevice(stmt);
				loopCount = 0;
				init();
			}


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
				}
				if(stmt1!=null) {
					stmt1.close();
				}
				if(con!=null) {
					con.close();
				}				
			} catch (SQLException ex) {

			}
		}
	}

	private void deleteDevice(Statement stmt) throws SQLException {
		// Delete the entries;
		String nodeRevisionsStr = nodeRevisions.substring(0, nodeRevisions.length()-1);
		String nodeStr = node.substring(0, node.length()-1);
		stmt.executeUpdate("delete from node_revisions where nid in (" + nodeRevisionsStr + ")");
		stmt.executeUpdate("delete from node where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_device where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_device_type where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_device_installation where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_device_pk where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_facility_pk where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_device_service_history where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_activation_datetime where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_expiration_datetime where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_activation_utc_offset where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_expiration_utc_offset where nid in (" + nodeStr + ")");

		stmt.executeUpdate("delete from device_responce_status where nid in (" + nodeStr + ")");
		
	}

	private void deviceServiceHistory(Connection con,
			long deviceInstallationNid) throws SQLException {
		String sqlQuery = "select content_type_device_service_history.nid from content_type_device_service_history " +
		"join content_field_device_pk on content_field_device_pk.nid=content_type_device_service_history.nid " +
		"where field_device_pk_nid=" + deviceInstallationNid;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery(sqlQuery);
		
		if(result.next()) {
			long nid = result.getLong("nid");
			nodeRevisions.append(nid + ",");
			node.append(nid + ",");
		}

	}

	private long deviceInstallation(Connection con, long long1) throws SQLException {
		String sqlQuery = "select content_type_device_installation.nid from content_type_device_installation " +
		"join content_field_device_pk on content_field_device_pk.nid=content_type_device_installation.nid " +
		"where field_device_pk_nid=" + long1;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery(sqlQuery);
		long nid = 0;
		if(result.next()) {
			nid = result.getLong("nid");
			nodeRevisions.append(nid + ",");
			node.append(nid + ",");
		}
		return nid;
	}

	private void locationRoleCleanup() {
		Connection con = null;
		String sqlQuery = "select nid from location_role_responce_status";
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;

		//Init
		init();

		try {
			con = dbConnection.getConnection();
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			result = stmt.executeQuery(sqlQuery);
			
			int loopCount = 0;
			while (result.next()) {
				nodeRevisions.append(result.getLong("nid") + ",");
				node.append(result.getLong("nid") + ",");
				loopCount++;
				if(loopCount == 10000) {
					deleteLocationRole(stmt1);
					System.out.println("Location role : " + loopCount);
					loopCount = 0;
					init();
				}
			}
			
			if(loopCount > 0) {
				deleteLocationRole(stmt1);
				System.out.println("Location role : " + loopCount);
				loopCount = 0;
				init();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
				}
				if(stmt1!=null) {
					stmt1.close();
				}
				if(con!=null) {
					con.close();
				}				
			} catch (SQLException ex) {

			}
		}
	}

	private void deleteLocationRole(Statement stmt) throws SQLException {
		// Delete the entries;
		String nodeRevisionsStr = nodeRevisions.substring(0, nodeRevisions.length()-1);
		String nodeStr = node.substring(0, node.length()-1);
		stmt.executeUpdate("delete from node_revisions where nid in (" + nodeRevisionsStr + ")");
		stmt.executeUpdate("delete from node where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_party_postal_address where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_address_type where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_activation_datetime where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_expiration_datetime where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_activation_utc_offset where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_expiration_utc_offset where nid in (" + nodeStr + ")");

		stmt.executeUpdate("delete from location_role_responce_status");
	}

	private void locationCleanup() {
		Connection con = null;
		String sqlQuery = "select nid from location_responce_status";
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;

		//Init
		init();

		try {
			con = dbConnection.getConnection();
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			result = stmt.executeQuery(sqlQuery);
			int loopCount = 0;
			while (result.next()) {
				nodeRevisions.append(result.getLong("nid") + ",");
				node.append(result.getLong("nid") + ",");
				loopCount++;
				if(loopCount == 10000) {
					deleteLocation(stmt1);
					System.out.println("Location : " + loopCount);
					loopCount = 0;
					init();
				}
			}
			
			if(loopCount > 0) {
				deleteLocation(stmt1);
				System.out.println("Location : " + loopCount);
				loopCount = 0;
				init();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
				}
				if(stmt1!=null) {
					stmt1.close();
				}
				if(con!=null) {
					con.close();
				}				
			} catch (SQLException ex) {

			}
		}
	}

	private void deleteLocation(Statement stmt) throws SQLException {
		// Delete the entries;
		String nodeRevisionsStr = nodeRevisions.substring(0, nodeRevisions.length()-1);
		String nodeStr = node.substring(0, node.length()-1);

		stmt.executeUpdate("delete from node_revisions where nid in (" + nodeRevisionsStr + ")");
		stmt.executeUpdate("delete from node where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_postal_address where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_country where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_is_active where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_field_sort_sequence where nid in (" + nodeStr + ")");

		stmt.executeUpdate("delete from location_responce_status where nid in (" + nodeStr + ")");

	}

	private void customerCleanup() {
		Connection con = null;
		String sqlQuery = "select nid from customer_responce_status";
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet result = null;

		//Init
		init();

		try {
			con = dbConnection.getConnection();
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			result = stmt.executeQuery(sqlQuery);
			
			int loopCount = 0;
			while (result.next()) {
				nodeRevisions.append(result.getLong("nid") + ",");
				node.append(result.getLong("nid") + ",");
				getBuCustomerNode(con, result.getLong("nid"));
				getPartyVoiceAddress(con, result.getLong("nid"));
				loopCount++;
				if(loopCount == 10000) {
					deleteCustomer(stmt1);
					System.out.println("Customer : " + loopCount);
					loopCount = 0;
					init();
				}
			}
			
			if(loopCount > 0) {
				deleteCustomer(stmt1);
				System.out.println("Customer : " + loopCount);
				loopCount = 0;
				init();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
				}
				if(stmt1!=null) {
					stmt1.close();
				}
				if(con!=null) {
					con.close();
				}				
			} catch (SQLException ex) {

			}
		}
	}



	private void getPartyVoiceAddress(Connection con, long long1) throws SQLException {
		String sqlQuery = "select nid from content_type_party_voice_address where field_voice_party_pk_nid=" + long1;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery(sqlQuery);
		while(result.next()) {
			nodeRevisions.append(result.getLong("nid") + ",");
			node.append(result.getLong("nid") + ",");
		}
	}

	private void deleteCustomer(Statement stmt) throws SQLException {
		// Delete the entries;
		String nodeRevisionsStr = nodeRevisions.substring(0, nodeRevisions.length()-1);
		String nodeStr = node.substring(0, node.length()-1);
		stmt.executeUpdate("delete from node_revisions where nid in (" + nodeRevisionsStr + ")");
		stmt.executeUpdate("delete from node where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_party where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_bu_customer where nid in (" + nodeStr + ")");
		stmt.executeUpdate("delete from content_type_party_voice_address where nid in (" + nodeStr + ")");

		stmt.executeUpdate("delete from customer_responce_status where nid in (" + nodeStr + ")");
	}

	private void getBuCustomerNode(Connection con, long long1) throws SQLException {
		String sqlQuery = "select nid from content_type_bu_customer where field_customer_party_pk_nid=" + long1;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery(sqlQuery);
		while(result.next()) {
			nodeRevisions.append(result.getLong("nid") + ",");
			node.append(result.getLong("nid") + ",");
		}

	}
}
