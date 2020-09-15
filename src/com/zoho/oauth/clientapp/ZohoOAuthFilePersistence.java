package com.zoho.oauth.clientapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zoho.oauth.client.ZohoOAuth;
import com.zoho.oauth.client.ZohoPersistenceHandler;
import com.zoho.oauth.common.ZohoOAuthConstants;
import com.zoho.oauth.common.ZohoOAuthException;
import com.zoho.oauth.contract.ZohoOAuthTokens;

public class ZohoOAuthFilePersistence implements ZohoPersistenceHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ZohoOAuthFilePersistence.class.getName());
	
	private static final String USER_IDENTIFIER = "useridentifier=";
	
	private static final String ACCESS_TOKEN = "accesstoken=";
	
	private static final String REFRESH_TOKEN = "refreshtoken=";
	
	private static final String EXPIRY_TIME = "expirytime=";
	
	private static final String SCOPE = "scope=";

	public void saveOAuthData(ZohoOAuthTokens tokens) throws ZohoOAuthException {
		try {
			File file = new File( getPersistenceHandlerFilePath() );
			PrintWriter writer = null;
			if( !file.exists() ) {
				writer = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );
			} else {
				writer = new PrintWriter( new BufferedWriter( new FileWriter( file, true ) ) );
				writer.println();
			}
			if( isUserAuthenticated( tokens.getUserMailId() ) ) {
				List<UserProperty> userProperties = getAllUserProperties();
				for( UserProperty property : userProperties ) {
					if( property.getUserIdentifier().equals( tokens.getUserMailId() ) ) {
						property.setAccessToken( tokens.getAccessToken() );
						property.setRefreshToken( tokens.getRefreshToken() );
						property.setExpiryTime( String.valueOf( tokens.getExpiryTime() ) );
						property.setScopes( tokens.getScopes() );
						break;
					}
				}
				writeAllProperties( userProperties );
				LOGGER.log(Level.SEVERE, "Tokens are updated in a file.");
				return;
			}
			writer.println( USER_IDENTIFIER + tokens.getUserMailId() );
			writer.println( ACCESS_TOKEN + tokens.getAccessToken() );
			writer.println( REFRESH_TOKEN + tokens.getRefreshToken() );
			writer.println( EXPIRY_TIME + tokens.getExpiryTime() );
			String scope = tokens.getScopes() == null ? SCOPE : SCOPE + tokens.getScopes();
			writer.println( scope );
			writer.close();
			LOGGER.log(Level.SEVERE, "Tokens inserted into a file.");
		} catch (Exception exp) {
			LOGGER.log(Level.SEVERE, "Exception while inserting tokens to file." + exp);
			exp.printStackTrace();
			throw new ZohoOAuthException(exp);
		}
	}

	public ZohoOAuthTokens getOAuthTokens(String mailId) throws ZohoOAuthException {
		try {
			UserProperty property = getUserProperty(mailId);
			if( property == null ) {
				throw new ZohoOAuthException("Given User not found in a file.");
			}
			ZohoOAuthTokens tokens = new ZohoOAuthTokens();
			tokens.setUserMailId( property.getUserIdentifier() );
			tokens.setAccessToken( property.getAccessToken() );
			tokens.setRefreshToken( property.getRefreshToken() );
			tokens.setExpiryTime( Long.valueOf( property.getExpiryTime() ) );
			tokens.setScopes( property.getScopes() );
			return tokens;
		} catch (Exception exp) {
			LOGGER.log(Level.SEVERE, "Exception while fetching tokens from file." + exp);
			throw new ZohoOAuthException(exp);
		}
	}
	
	public void deleteOAuthTokens(String mailId) throws ZohoOAuthException {
		try {
			List<UserProperty> userProperties = new ArrayList<>();
			for( UserProperty property : getAllUserProperties() ) {
				if( !property.getUserIdentifier().equals( mailId ) ) {
					userProperties.add( property );
				}
			}
			writeAllProperties(userProperties);
			LOGGER.log(Level.SEVERE, "tokens deleted from a file.");
		} catch (Exception exp) {
			LOGGER.log(Level.SEVERE, "Exception while deleting tokens from file." + exp);
			exp.printStackTrace();
			throw new ZohoOAuthException(exp);
		}
	}
	
	public boolean isUserAuthenticated(String mailId) throws IOException, ZohoOAuthException {
		return getUserProperty(mailId) != null;
	}
	
	private UserProperty getUserProperty(String mailId) throws IOException, ZohoOAuthException {
		UserProperty userProperty = null;
		for( UserProperty property : getAllUserProperties() ) {
			if( property.getUserIdentifier().equals( mailId ) ) {
				userProperty = property;
				break;
			}
		}
		return userProperty;
	}
	
	private List<UserProperty> getAllUserProperties() throws IOException, ZohoOAuthException {
		BufferedReader reader = new BufferedReader( new FileReader( new File( getPersistenceHandlerFilePath() ) ) );
		String line = null;
		List<UserProperty> properties = new ArrayList<>();
		while( ( line = reader.readLine() ) != null ) {
			if( line.contains("useridentifier")  ) {
				UserProperty property = new UserProperty();
				property.setUserIdentifier( line );
				property.setAccessToken( reader.readLine() );
				property.setRefreshToken( reader.readLine() );
				property.setExpiryTime( reader.readLine() );
				property.setScopes( reader.readLine() );
				properties.add( property );
			}
		}
		reader.close();
		return properties;
	}
	
	private void writeAllProperties( List<UserProperty> properties ) throws IOException, ZohoOAuthException {
		PrintWriter writer = new PrintWriter( new BufferedWriter( new FileWriter( new File( getPersistenceHandlerFilePath() ) ) ) );
		for( int i = 0; i < properties.size(); i++ ) {
			UserProperty property = properties.get(i);
			writer.println( USER_IDENTIFIER + property.getUserIdentifier() );
			writer.println( ACCESS_TOKEN + property.getAccessToken() );
			writer.println( REFRESH_TOKEN + property.getRefreshToken() );
			writer.println( EXPIRY_TIME + property.getExpiryTime() );
			String scope = property.getScopes() == null ? "" : property.getScopes();
			writer.println( SCOPE + scope );
			if( i != ( properties.size() - 1 ) ) {
				writer.println();
			}
		}
		writer.close();
	}

	public static String getPersistenceHandlerFilePath() throws ZohoOAuthException {
		String filePath = ZohoOAuth.getConfigValue( ZohoOAuthConstants.OAUTH_TOKENS_FILE_PATH );
		if( filePath != null ) {
			return filePath;
		}
		throw new ZohoOAuthException("oauth_tokens_file_path - property is missing/invalid.");
	}
	
}