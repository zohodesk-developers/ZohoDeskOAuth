package com.zoho.oauth.common;

@SuppressWarnings("serial")
public class ZohoOAuthException extends Exception {
	
	private String message;
	
	private Throwable exception;
	
	public ZohoOAuthException( String message ) {
		super( message );
		this.message = message;
	}
	
	public ZohoOAuthException( Exception ex ) {
		super( ex );
		this.exception = ex;
	}
	
	public String getErrorMessage() {
		if( this.message == null ) {
			if( this.exception != null ) {
				return this.exception.getMessage();
			}
		}
		return this.message;
	}
	
	public ZohoOAuthException( Exception ex, String message ) {
		super( message, ex );
		this.message = message;
		this.exception = ex;
	}
	
	@Override
	public String toString() {
		return getErrorMessage();
	}
	
}