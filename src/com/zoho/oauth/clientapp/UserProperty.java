//$Id$
package com.zoho.oauth.clientapp;

class UserProperty {
	
	String userIdentifier;
	
	String accessToken;
	
	String refreshToken;
	
	String expiryTime;
	
	String scopes;
	
	String getUserIdentifier() {
		return userIdentifier;
	}

	void setUserIdentifier(String userIdentifier) {
		String[] split = userIdentifier.split("=");
		this.userIdentifier = split.length == 1 ? userIdentifier : split[1].trim();
	}

	String getAccessToken() {
		return accessToken;
	}

	void setAccessToken(String accessToken) {
		String[] split = accessToken.split("=");
		this.accessToken = split.length == 1 ? accessToken : split[1].trim();
	}

	String getRefreshToken() {
		return refreshToken;
	}

	void setRefreshToken(String refreshToken) {
		String[] split = refreshToken.split("=");
		this.refreshToken = split.length == 1 ? refreshToken : split[1].trim();
	}

	String getExpiryTime() {
		return expiryTime;
	}

	void setExpiryTime(String expiryTime) {
		String[] split = expiryTime.split("=");
		this.expiryTime = split.length == 1 ? expiryTime : split[1].trim();
	}

	String getScopes() {
		return scopes;
	}

	void setScopes(String scopes) {
		if( scopes != null ) {
			String[] split = scopes.split("=");
			if( split.length == 1 ) {
				this.scopes = null;
			} else {
				String[] scopeNames = split[1].split(",");
				StringBuilder builder = new StringBuilder();
				for( int i = 0; i < scopeNames.length; i++ ) {
					builder.append( scopeNames[i] );
					if( i != ( scopeNames.length - 1 ) ) {
						builder.append(",");
					}
				}
				this.scopes = builder.toString();
			}
		} else {
			this.scopes = scopes;
		}
	}

}
