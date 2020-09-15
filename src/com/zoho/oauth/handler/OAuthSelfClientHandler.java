//$Id$
package com.zoho.oauth.handler;

import com.zoho.oauth.client.ZohoOAuthClient;
import com.zoho.oauth.common.ZohoOAuthException;

public class OAuthSelfClientHandler {
public static void main(String args[]) throws ZohoOAuthException{	
	String code = "1000.6d83b8f26e58546754bfc98a6f4e6754.78815460e7c7677f1563f3435c7dce91"; //replace your authorization code here
	ZohoOAuthClient client =new  ZohoOAuthClient();
	client.generateAccessTokenFromAuthorizationCode(code);
	}
}
