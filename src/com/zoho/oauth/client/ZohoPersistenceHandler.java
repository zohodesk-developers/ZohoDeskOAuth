package com.zoho.oauth.client;

import com.zoho.oauth.common.ZohoOAuthException;
import com.zoho.oauth.contract.ZohoOAuthTokens;

public interface ZohoPersistenceHandler {
	
	public void saveOAuthData(ZohoOAuthTokens tokens) throws ZohoOAuthException;

	public ZohoOAuthTokens getOAuthTokens(String mailId) throws ZohoOAuthException;

	public void deleteOAuthTokens(String mailId) throws ZohoOAuthException;
	
	public boolean isUserAuthenticated(String mailId) throws Exception;
	
}