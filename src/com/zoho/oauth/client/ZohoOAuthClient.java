package com.zoho.oauth.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.zoho.oauth.common.ZohoHTTPConnector;
import com.zoho.oauth.common.ZohoOAuthConstants;
import com.zoho.oauth.common.ZohoOAuthException;
import com.zoho.oauth.contract.ZohoOAuthTokens;

public class ZohoOAuthClient {
	
	private static final Logger LOGGER = Logger.getLogger(ZohoOAuthClient.class.getName());
	
	public ZohoOAuthClient() {
		ZohoOAuth.initialize();
	}
	public String getAuthorizationRequestURL() {
		return ZohoOAuth.getIAMUrl() + "/oauth/v2/auth?scope=" + ZohoOAuth.getScopes() + "&client_id="
				+ ZohoOAuth.getClientID() + "&client_secret=" + ZohoOAuth.getClientSecret()
				+ "&response_type=code&access_type=" + ZohoOAuth.getAccessType() + "&redirect_uri="
				+ ZohoOAuth.getRedirectURL();
	}
	
	public ZohoOAuthTokens generateAccessTokenFromAuthorizationCode(String grantToken) throws ZohoOAuthException {
		if (grantToken == null) {
			throw new ZohoOAuthException("Grant Token is not provided.");
		}
		try {
			ZohoHTTPConnector conn = getZohoConnector(ZohoOAuth.getTokenURL());
			conn.addParam( ZohoOAuthConstants.GRANT_TYPE, ZohoOAuthConstants.GRANT_TYPE_AUTH_CODE );
			conn.addParam( ZohoOAuthConstants.CODE, grantToken);
			String resp = conn.post();
			JSONObject responseJSON = new JSONObject(resp);
			if (responseJSON.has( ZohoOAuthConstants.ACCESS_TOKEN )) {
				ZohoOAuthTokens tokens = getTokensFromJSON(responseJSON);
				tokens.setUserMailId(getUserEmailId(tokens.getAccessToken()));
				tokens.setScopes( ZohoOAuth.getConfigValue( ZohoOAuthConstants.SCOPES ) );
				ZohoOAuth.getPersistenceHandlerInstance().saveOAuthData(tokens);
				return tokens;
			}
			throw new ZohoOAuthException("Exception while fetching access token from grant token - " + resp);
		} catch (Exception ex) {
			throw new ZohoOAuthException(ex);
		}
	}
	
	
	public ZohoOAuthTokens getOAuthTokenDetails(String mailId) throws ZohoOAuthException {
		try {
			ZohoPersistenceHandler persistence = ZohoOAuth.getPersistenceHandlerInstance();
			return persistence.getOAuthTokens(mailId);
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception while retrieving tokens from persistence - " + ex);
			throw new ZohoOAuthException(ex);
		}
	}

	public String getAccessToken(String userMailId) throws ZohoOAuthException {
		ZohoOAuthTokens tokens = getOAuthTokenDetails(userMailId);
		try {
			return tokens.getAccessToken();
		} catch (ZohoOAuthException ex) {
			LOGGER.log(Level.INFO, "Access Token has expired. Hence refreshing.");
			tokens = refreshAccessToken(tokens.getRefreshToken(), userMailId);
			return tokens.getAccessToken();
		}
	}
	
	public String getRefreshToken(String mailId) throws ZohoOAuthException {
		ZohoOAuthTokens tokens = getOAuthTokenDetails(mailId);
		return tokens.getRefreshToken();
	}
	
	public boolean isUserAuthenticated(String mailId) throws ZohoOAuthException {
		try {
			return ZohoOAuth.getPersistenceHandlerInstance().isUserAuthenticated( mailId );
		} catch( Exception ex ) {
			throw new ZohoOAuthException(ex);
		}
	}
	
	public String getUserEmailId(String accessToken) throws ZohoOAuthException {
		try {
			ZohoHTTPConnector conn = new ZohoHTTPConnector();
			conn.setUrl(ZohoOAuth.getUserInfoURL());
			conn.addHeadder("Authorization", ZohoOAuthConstants.AUTH_HEADER_PREFIX + accessToken);
			String response = conn.get();
			JSONObject responseJSON = new JSONObject(response);
			return responseJSON.getString("Email");
		} catch(Exception ex) {
			throw new ZohoOAuthException("Error occur while getting user info.");
		}
	}
	
	
	public ZohoOAuthTokens generateAccessTokenFromRefreshToken(String refreshToken, String userMailId) throws ZohoOAuthException {
		if (refreshToken == null) {
			throw new ZohoOAuthException("Refresh token is not provided.");
		}
		return refreshAccessToken(refreshToken, userMailId);
	}

	private ZohoOAuthTokens refreshAccessToken(String refreshToken, String userMailId) throws ZohoOAuthException {
		if (refreshToken == null) {
			throw new ZohoOAuthException("Refresh token is not provided.");
		}
		try {
			ZohoHTTPConnector conn = getZohoConnector(ZohoOAuth.getRefreshTokenURL());
			conn.addParam( ZohoOAuthConstants.GRANT_TYPE, ZohoOAuthConstants.GRANT_TYPE_REFRESH );
			conn.addParam( ZohoOAuthConstants.REFRESH_TOKEN, refreshToken );
			String resp = conn.post();
			JSONObject responseJSON = new JSONObject(resp);
			if (responseJSON.has( ZohoOAuthConstants.ACCESS_TOKEN )) {
				ZohoOAuthTokens tokens = getTokensFromJSON(responseJSON);
				tokens.setUserMailId(userMailId);
				tokens.setRefreshToken(refreshToken);
				tokens.setScopes( ZohoOAuth.getConfigValue( ZohoOAuthConstants.SCOPES ) );
				ZohoOAuth.getPersistenceHandlerInstance().saveOAuthData(tokens);
				return tokens;
			}
			throw new ZohoOAuthException("Exception while fetching access token from refresh token - " + resp);
		} catch (Exception ex) {
			throw new ZohoOAuthException(ex);
		}
	}

	private ZohoHTTPConnector getZohoConnector(String url) {
		ZohoHTTPConnector conn = new ZohoHTTPConnector();
		conn.setUrl(url);
		conn.addParam(ZohoOAuthConstants.CLIENT_ID, ZohoOAuth.getClientID());
		conn.addParam(ZohoOAuthConstants.CLIENT_SECRET, ZohoOAuth.getClientSecret());
		if(!Boolean.valueOf(ZohoOAuthConstants.IS_SELFCLIENT))
		{
			conn.addParam(ZohoOAuthConstants.REDIRECT_URL, ZohoOAuth.getRedirectURL());
		}
		return conn;
	}

	private ZohoOAuthTokens getTokensFromJSON(JSONObject response) throws JSONException, ZohoOAuthException {
		ZohoOAuthTokens tokens = new ZohoOAuthTokens();
		Integer expiresIn = (Integer) response.get( ZohoOAuthConstants.EXPIRES_IN );
		tokens.setExpiryTime( Long.valueOf( System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( expiresIn ) ) );
		String accessToken = (String) response.get( ZohoOAuthConstants.ACCESS_TOKEN );
		tokens.setAccessToken(accessToken);
		if (response.has( ZohoOAuthConstants.REFRESH_TOKEN )) {
			String refreshToken = (String) response.get( ZohoOAuthConstants.REFRESH_TOKEN );
			tokens.setRefreshToken(refreshToken);
		}
		return tokens;
	}
	
}
