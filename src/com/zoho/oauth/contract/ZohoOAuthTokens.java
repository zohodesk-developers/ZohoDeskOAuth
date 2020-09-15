package com.zoho.oauth.contract;

import org.json.JSONException;
import org.json.JSONObject;

import com.zoho.oauth.common.ZohoOAuthConstants;
import com.zoho.oauth.common.ZohoOAuthException;

public class ZohoOAuthTokens {
	
	private String userMailId;
	
	private String accessToken;
	
	private String refreshToken;
	
	private long expiryTime;
	
	private String scopes;

	public void setUserMailId(String userMailId) {
		this.userMailId = userMailId;
	}

	public String getUserMailId() {
		return this.userMailId;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() throws ZohoOAuthException {
		if ( isAccessTokenValid() ) {
			return this.accessToken;
		}
		throw new ZohoOAuthException("Access Token has expired.");
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public Long getExpiryTime() {
		return this.expiryTime;
	}

	public void setExpiryTime(Long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public long getExpiryLapseInMillis() {
		return getExpiryTime() - System.currentTimeMillis();
	}

	private boolean isAccessTokenValid() {
		return getExpiryLapseInMillis() > 15000L;
	}
	
	public void setScopes(String scopes) {
		if( scopes != null ) {
			StringBuilder builder = new StringBuilder();
			String[] scopeSplit = scopes.split(",");
			for( int i = 0; i < scopeSplit.length; i++ ) {
				builder.append( scopeSplit[i].trim() );
				if( i != ( scopeSplit.length - 1 ) ) {
					builder.append(",");
				}
			}
			this.scopes = builder.toString().toLowerCase();
			if( !this.scopes.contains( ZohoOAuthConstants.IAM_SCOPE ) ) {
				this.scopes = ZohoOAuthConstants.IAM_SCOPE + "," + this.scopes;
			}
		} else {
			this.scopes = scopes;
		}
	}
	
	public String getScopes() {
		return this.scopes;
	}

	public JSONObject toJSON() throws JSONException, ZohoOAuthException {
		JSONObject tokens = new JSONObject();
		tokens.put( "user_mail_id", this.getUserMailId() );
		tokens.put( "access_token", this.getAccessToken() );
		tokens.put( "refresh_token", this.getRefreshToken() );
		tokens.put( "expires_in", this.getExpiryLapseInMillis() );
		tokens.put( "expiry_time", this.getExpiryTime() );
		tokens.put( "scopes", this.getScopes() );
		return tokens;
	}
	
	public String toString() {
		return "user_mail_id: " + this.userMailId + ", access_token: " + this.accessToken + ", refresh_token: " + this.refreshToken
				+ ", expiry_time: " + this.expiryTime + ", scope: " + this.scopes;
	}
	
}
