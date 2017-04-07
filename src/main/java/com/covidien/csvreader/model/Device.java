package com.covidien.csvreader.model;

public class Device {
	
	String SOURCE_SYSTEM;
	
	String SKU;
	
	String SERIAL_NUMBER;
	
	String NAME;
	
	String DESCRIPTION;
	
	String MAINTENANCE_EXPIRATION_DATE;
	
	String INSTALL_COUNTRY_CODE;
	
	String CUSTOMER_ID;
	
	String LAST_PUBLISH_DATE;
	
	String LOCATION_ID;

	public String getSOURCE_SYSTEM() {
		return SOURCE_SYSTEM;
	}

	public String getSKU() {
		return SKU;
	}

	public String getSERIAL_NUMBER() {
		return SERIAL_NUMBER;
	}

	public String getNAME() {
		return NAME;
	}

	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	public String getMAINTENANCE_EXPIRATION_DATE() {
		return MAINTENANCE_EXPIRATION_DATE;
	}

	public String getINSTALL_COUNTRY_CODE() {
		return INSTALL_COUNTRY_CODE;
	}

	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}

	public String getLAST_PUBLISH_DATE() {
		return LAST_PUBLISH_DATE;
	}

	public String getLOCATION_ID() {
		return LOCATION_ID;
	}

	public void setSOURCE_SYSTEM(String sOURCE_SYSTEM) {
		SOURCE_SYSTEM = sOURCE_SYSTEM;
	}

	public void setSKU(String sKU) {
		SKU = sKU;
	}

	public void setSERIAL_NUMBER(String sERIAL_NUMBER) {
		SERIAL_NUMBER = sERIAL_NUMBER;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}

	public void setMAINTENANCE_EXPIRATION_DATE(String mAINTENANCE_EXPIRATION_DATE) {
		MAINTENANCE_EXPIRATION_DATE = mAINTENANCE_EXPIRATION_DATE;
	}

	public void setINSTALL_COUNTRY_CODE(String iNSTALL_COUNTRY_CODE) {
		INSTALL_COUNTRY_CODE = iNSTALL_COUNTRY_CODE;
	}

	public void setCUSTOMER_ID(String cUSTOMER_ID) {
		CUSTOMER_ID = cUSTOMER_ID;
	}

	public void setLAST_PUBLISH_DATE(String lAST_PUBLISH_DATE) {
		LAST_PUBLISH_DATE = lAST_PUBLISH_DATE;
	}

	public void setLOCATION_ID(String lOCATION_ID) {
		LOCATION_ID = lOCATION_ID;
	}
	
}
