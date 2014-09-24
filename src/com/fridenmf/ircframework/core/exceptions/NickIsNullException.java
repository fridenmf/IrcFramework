package com.fridenmf.ircframework.core.exceptions;

public class NickIsNullException extends Exception {

	private static final long serialVersionUID = 7345366281685509249L;
	
	@Override
	public String getMessage() {
		return "Nick was null. Be sure to set that before calling connect, or override onConnect and do it there";
	}

}
