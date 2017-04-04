package com.soak.framework.exception;

public class CustomException extends Exception {
	private static final long serialVersionUID = -7334217316804085767L;

	public CustomException(){
		super();
	}
	
	public CustomException(String message){
		super(message);
	}
}
