package com.zoho.oauth.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.zoho.oauth.common.ZohoOAuthConstants;
import com.zoho.oauth.common.ZohoOAuthException;

public class ZohoOAuth {
	
	private static Map<String, String> configProperties = new HashMap<>();
	private static boolean isPropertyInfoLoaded = false;
	
	static {
		if(!isPropertyInfoLoaded) {
			readProperties();
			isPropertyInfoLoaded = true;
		}
	}
	static void initialize() {}
	
	public static String getConfigValue(String key) {
		return configProperties.get(key);
	}

	public static Map<String, String> getAllConfigs() {
		return configProperties;
	}

	public static String getIAMUrl() {
		return configProperties.get( ZohoOAuthConstants.IAM_URL);
	}

	static String getTokenURL() {
		return ZohoOAuth.getIAMUrl() + "/oauth/v2/token";
	}

	static String getRefreshTokenURL() {
		return ZohoOAuth.getIAMUrl() + "/oauth/v2/token";
	}

	static String getUserInfoURL() {
		return ZohoOAuth.getIAMUrl() + "/oauth/user/info";
	}

	static String getRevokeTokenURL() {
		return ZohoOAuth.getIAMUrl() + "/oauth/v2/token/revoke";
	}

	public static String getScopes() {
		String scopes = ZohoOAuth.getConfigValue( ZohoOAuthConstants.SCOPES );
		if(scopes != null && !scopes.contains(ZohoOAuthConstants.IAM_SCOPE)) {
			scopes = scopes + "," + ZohoOAuthConstants.IAM_SCOPE;
		}
		return scopes;
	}

	public static String getClientID() {
		return ZohoOAuth.getConfigValue( ZohoOAuthConstants.CLIENT_ID );
	}

	public static String getClientSecret() {
		return ZohoOAuth.getConfigValue( ZohoOAuthConstants.CLIENT_SECRET );
	}

	public static String getRedirectURL() {
		return ZohoOAuth.getConfigValue( ZohoOAuthConstants.REDIRECT_URL );
	}

	public static String getAccessType() {
		return ZohoOAuth.getConfigValue( ZohoOAuthConstants.ACCESS_TYPE );
	}
	
	public static String getDeskURL() {
		return ZohoOAuth.getConfigValue( ZohoOAuthConstants.DESK_URL );
	}
	
	public static ZohoPersistenceHandler getPersistenceHandlerInstance() throws ZohoOAuthException {
		try {
			String className = ZohoOAuth.getConfigValue( ZohoOAuthConstants.PERSISTENCE_HANDLER_CLASS );
			return (ZohoPersistenceHandler) Class.forName(className).getConstructor(null).newInstance(null);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException
				| InvocationTargetException ex) {
			throw new ZohoOAuthException(ex);
		}
	}
	
	private static void readProperties() {
		try {
			Map<String, String> configProperties = new HashMap<>();
			String sdkInitializationFilePath = System.getProperty("zdesk-init-file");
			if( sdkInitializationFilePath == null ) {
				throw new ZohoOAuthException("Unable to find initialization file. Please give a valid file path.");
			}
			BufferedReader reader = new BufferedReader( new FileReader( new File( sdkInitializationFilePath ) ) );
			String line = null;
			while( ( line = reader.readLine() ) != null ) {
				String[] property = line.split("=");
				String value = property.length == 1 ? null : property[1].trim();
				configProperties.put( property[0].trim() , value );
			}
			reader.close();
			ZohoOAuth.initialize(configProperties);
		} catch( IOException | ZohoOAuthException ex ) {
			ex.printStackTrace();
		}
	}
	
	private static void initialize(Map<String, String> configurations) {
		configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.com" );
		configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.com");
		configProperties.put( ZohoOAuthConstants.ACCESS_TYPE, ZohoOAuthConstants.ACCESS_TYPE_OFFLINE );
		configProperties.putAll(configurations);
		if( configurations.get("dc") != null ) {
			setDeskAndIAMUrlDomain( configurations.get("dc") );
		}
	}
	
	
	private static void setDeskAndIAMUrlDomain(String deskDomain) {
		switch( deskDomain ) {
			case "eu": 
				configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.eu");
				configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.eu");
				break;
			case "cn":
				configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.com.cn");
				configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.com.cn");
				break;
			case "in":
				configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.in");
				configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.in");
				break;
			case "au":
				configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.com.au");
				configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.com.au");
				break;
			default:
				configProperties.put( ZohoOAuthConstants.DESK_URL, "https://desk.zoho.com");
				configProperties.put(ZohoOAuthConstants.IAM_URL, "https://accounts.zoho.com");
		}
	}
}
