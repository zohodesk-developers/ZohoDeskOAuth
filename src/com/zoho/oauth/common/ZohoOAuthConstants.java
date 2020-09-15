package com.zoho.oauth.common;

public class ZohoOAuthConstants {
	/*Keys for properties have to be configured inside oauthConfig.properties file starts*/
	public static final String IS_SELFCLIENT="is_selfclient";
	
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REDIRECT_URL = "redirect_uri";
	public static final String ACCESS_TYPE = "access_type";
	public static final String SCOPES = "scope";
	public static final String STATE = "state";
	
	public static final String OAUTH_TOKENS_FILE_PATH = "oauth_tokens_file_path";
	public static final String PERSISTENCE_HANDLER_CLASS = "persistence_handler_class";
	/*Keys for properties have to be configured inside oauthConfig.properties file ends*/
	
	public static final String ACCESS_TYPE_OFFLINE = "offline";
	public static final String ACCESS_TYPE_ONLINE = "online";
	
	public static final String GRANT_TYPE = "grant_type";
	public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
	public static final String GRANT_TYPE_REFRESH = "refresh_token";
	public static final String CODE = "code";
	
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXPIRES_IN = "expires_in";
	
	public static final String DESK_URL = "deskURL";
	public static final String IAM_URL = "iamURL";
	public static final String AUTH_HEADER_PREFIX = "Zoho-oauthtoken ";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String IAM_SCOPE = "aaaserver.profile.read";
	
	
	
}
