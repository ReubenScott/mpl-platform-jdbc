package com.covidien.csvreader.model;

public class LocationRole {
	
	String CUSTOMER_ID;
	
	String LOCATION_ID;
	
	String LOCATION_ROLE;
	
	String LAST_PUBLISH_DATE;
	

	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}

	public String getLOCATION_ID() {
		return LOCATION_ID;
	}

	public String getLOCATION_ROLE() {
		return LOCATION_ROLE;
	}

	public String getLAST_PUBLISH_DATE() {
		return LAST_PUBLISH_DATE;
	}
	

	public void setCUSTOMER_ID(String cUSTOMER_ID) {
		CUSTOMER_ID = cUSTOMER_ID;
	}

	public void setLOCATION_ID(String lOCATION_ID) {
		LOCATION_ID = lOCATION_ID;
	}

	public void setLOCATION_ROLE(String lOCATION_ROLE) {
		LOCATION_ROLE = lOCATION_ROLE;
	}

	public void setLAST_PUBLISH_DATE(String lAST_PUBLISH_DATE) {
		LAST_PUBLISH_DATE = lAST_PUBLISH_DATE;
	}
		
}
