//$Id$
package com.zoho.oauth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import com.zoho.oauth.client.ZohoOAuth;
import com.zoho.oauth.client.ZohoOAuthClient;
import com.zoho.oauth.common.ZohoHTTPConnector;
import com.zoho.oauth.common.ZohoOAuthConstants;
import com.zoho.oauth.common.ZohoOAuthException;

public class DeskAPIHandler extends HttpServlet {
private static final long serialVersionUID = 1L;
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String userMailId = request.getParameter("userMailId");
	String ticketResponse=new String();
	try {
		ticketResponse = getTickets(userMailId);
	} catch (ZohoOAuthException e) {
		e.printStackTrace();
	}
	response.setStatus(HttpStatus.SC_OK);
	response.getWriter().write(ticketResponse);
}


private String getTickets(String userMailId) throws ZohoOAuthException {
	try {
		ZohoOAuthClient zohoOAuthClient=new ZohoOAuthClient();
		String accessToken=zohoOAuthClient.getAccessToken(userMailId);
		ZohoHTTPConnector conn =new ZohoHTTPConnector();
		conn.setUrl(ZohoOAuth.getDeskURL()+"/api/v1/tickets");
		conn.addHeadder(ZohoOAuthConstants.AUTHORIZATION_HEADER, ZohoOAuthConstants.AUTH_HEADER_PREFIX + accessToken);
		String response = conn.get();
		JSONObject responseJSON = new JSONObject(response);
		return responseJSON.toString();
	} catch(Exception ex) {
		throw new ZohoOAuthException("Error occur while getting ticket info.");
	}
}
}
