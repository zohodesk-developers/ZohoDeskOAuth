//$Id$
package com.zoho.oauth.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import com.zoho.oauth.client.ZohoOAuthClient;

@SuppressWarnings("serial")
public class OAuthWebClientHandler extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> responseData = new HashMap<>();
		ZohoOAuthClient zohoOAuthClient=new ZohoOAuthClient();
		responseData.put("authorizationURL", zohoOAuthClient.getAuthorizationRequestURL());
		response.setStatus(HttpStatus.SC_OK);
		response.getWriter().write(new JSONObject(responseData).toString());
}
}
