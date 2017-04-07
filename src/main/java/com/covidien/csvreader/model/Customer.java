package com.covidien.csvreader.model;


public class Customer {
	
	String CUSTOMER_ID;
	
	String NAME;
	
	String PHONE;
	
	String FAX;
	
	int DISTRIBUTOR_FLAG;
	
	String LAST_PUBLISH_DATE;

	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}

	public void setCUSTOMER_ID(String cUSTOMER_ID) {
		CUSTOMER_ID = cUSTOMER_ID;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getFAX() {
		return FAX;
	}

	public void setFAX(String fAX) {
		FAX = fAX;
	}

	public int getDISTRIBUTOR_FLAG() {
		return DISTRIBUTOR_FLAG;
	}

	public void setDISTRIBUTOR_FLAG(int dISTRIBUTOR_FLAG) {
		DISTRIBUTOR_FLAG = dISTRIBUTOR_FLAG;
	}

	public String getLAST_PUBLISH_DATE() {
		return LAST_PUBLISH_DATE;
	}

	public void setLAST_PUBLISH_DATE(String lAST_PUBLISH_DATE) {
		LAST_PUBLISH_DATE = lAST_PUBLISH_DATE;
	}

}
