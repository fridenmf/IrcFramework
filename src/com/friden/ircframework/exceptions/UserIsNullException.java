package com.friden.ircframework.exceptions;

public class UserIsNullException extends Exception{

	private static final long serialVersionUID = 2300946602778492441L;

	@Override
	public String getMessage() {
		return "User was null. Be sure to set that before calling connect, or override onConnect and do it there";
	}
	
}
