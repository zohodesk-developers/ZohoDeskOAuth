# OAuth Authorization in Zoho Desk
This is a sample reference code in Java to help you understand the OAuth authorization flow for Zoho Desk Rest APIs. If needed, You can reuse this same code in your Java application or can reuse the logic using any other language of your preference to make calls to Zoho Desk APIs.

### Note:
>Refer OAuth Authorization from Zoho Desk API Doc (To access appropriate reference links replace the `".com"` with your respective domain.)
>sample US doc link: https://desk.zoho.com/support/APIDocument.do#OauthTokens

### Configuration
To be able to access Zoho Desk functionalities through your application using Zoho Desk Rest APIs, you must authenticate your application.
To do this, first, pass a key-value configuration pair in oauthConfig.properties file and pass this property file path as value for zdesk-init-file system property key as a VM argument. 

Required configuration
 * **dc** : Domain from which the API calls are made. For US, which is the default domain, use com. For EU, use eu. For IN, use in. For AU, use au and for CN, use cn. 
 * **access_type** : use "offline" to get  refresh token, else use "online". 
 * **client_id, client_secret, redirect_uri** : OAuth credentials generated after registering your application through https://api-console.zoho.com/ (To access appropriate reference links replace the ".com" with your respective domain.)
 * **scope** : To generate access token from the self client instead of application, you need to provide Desk API scopes along with aaaserver.profile.read scope, separated by commas. The aaaserver.profile.read scope is used to fetch user information and to store access and refresh tokens against the user information. If you try to generate access token from your web app, then you must define scopes in the property file because we use this scope property in Zoho accounts URL construction function. 
 * **persistence_handler_class** : Implementation of the ZohoOAuthPersistenceInterface class. 
 * **is_selfclient** : you can mention it as "true" if you are using OAuth self client option. If `is_selfclient` is true you can ignore the key `redirect_uri`because  self  client doesn't require a redirect url.
 * **oauth_tokens_file_path** : file path to be used in ZohoOAuthFilePersistence implementation (The sample code given here uses FilePersistence to hold Oauth token details). 
 

### Configuring Persistence
This sample reference code provides ZohoOAuthPersistenceInterface class. If you want to use an implementation of your choice, you can do so by implementing this given interface.

**Implementing OAuth persistence**

After the client application is authorized, OAuth access and refresh tokens can be used for making subsequent data requests to Zoho Desk. Therefore, the tokens must be persisted by the application.

You can achieve this persistence by writing an implementation of the predefined ZohoPersistenceHandler interface, which has the following callback methods:

    saveOAuthData(ZohoOAuthTokens tokens) - This function helps to store OAuth token data in the persistence.
    deleteOAuthTokens(String mailId) - This function helps to delete given user OAuth token data from the persistence.
    getOAuthTokens(String mailId) - This function helps to get given user OAuth token data from the persistence.
    isUserAuthenticated(String mailId) - Identifies whether the current user is authenticated.

The  sample reference code provides sample implementations of the ZohoPersistenceHandler interface using file persistence. Refer ZohoOAuthFilePersistence class.

Implement com.zoho.oauth.client.ZohoPersistenceHandler if you need to persist OAuth data. Also specify the classpath in `persistence_handler_class` Key under oauthConfig.properties file.

### OAuth Selfclient 
Refer: https://desk.zoho.com/support/APIDocument.do#self-client
1. Generate authorization code for the Zoho Desk scopes (comma-separated) you want to authorize and the aaaserver.profile.read scope. 
2. use the code as parameter to the method _generateAccessTokenFromAuthorizationCode(code)_ in _ZohoOAuthClient_ class to generate the Oauth tokens for your application. You can refer the sample implementation from class _OAuthSelfClientHandler_

### OAuth Webclient (server based client)
Refer: https://desk.zoho.com/support/APIDocument.do#redirection
1. Create an entry point to your application using the _OAuthWebClientHandler_ class. Use the _zohoOAuthClient.getAuthorizationRequestURL()_  method to form a URL for generating the grant token and redirect the servlet to this generated URL. Refer class _OAuthWebClientHandler_
2. Here the user who access your application will give authorization and the respective Oauthtokens will be generated in the backend. You can use these token information to access Zoho Desk APIs on behalf of this user.  
### Sample code for making Desk API  call
 _DeskAPIHandler_ class has sample implementation about how to make Desk API call using the generated token information. 

### Demo video:
Demo For using OAuth Webclient client: https://workdrive.zohoexternal.com/external/6Oxchx1jp6Q-J8HFH
  
