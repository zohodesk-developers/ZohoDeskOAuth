package com.zoho.oauth.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class ZohoHTTPConnector {
	private String url;
	private HashMap<String, String> requestParams = new HashMap<>();
	private HashMap<String, String> requestHeaders = new HashMap<>();
	protected static final Logger LOGGER = Logger.getLogger(ZohoHTTPConnector.class.getName());
	public String post() throws Exception {
		String value;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(this.url);
		ArrayList<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
		if (this.requestParams != null && !this.requestParams.isEmpty()) {
			for (String key : this.requestParams.keySet()) {
				value = this.requestParams.get(key);
				if (value == null) {
					continue;
				}
				urlParameters.add(new BasicNameValuePair(key, value));
			}
		}
		post.setEntity((HttpEntity) new UrlEncodedFormEntity(urlParameters));
		if (this.requestHeaders != null && !this.requestHeaders.isEmpty()) {
			for (String key : this.requestHeaders.keySet()) {
				value = this.requestHeaders.get(key);
				if (value == null) {
					continue;
				}
				post.setHeader(key, value);
			}
		}
		LOGGER.info("POST - URL = " + this.url + " , HEADERS = " + this.requestHeaders + " , PARAMS = "+ this.requestParams + " . ");
		CloseableHttpResponse response = client.execute((HttpUriRequest) post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		LOGGER.info("STATUS_CODE = " + response.getStatusLine().getStatusCode() + " ,RESPONSE_JSON = " + result.toString());
		return result.toString();
	}

	public String get() throws IOException, NoSuchAlgorithmException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(this.url);
		if (this.requestHeaders != null && !this.requestHeaders.isEmpty()) {
			for (String key : this.requestHeaders.keySet()) {
				String value = this.requestHeaders.get(key);
				if (value == null) {
					continue;
				}
				request.setHeader(key, value);
			}
		}
		LOGGER.info("GET - URL = " + this.url + " , HEADERS = " + this.requestHeaders + " , PARAMS = "+ this.requestParams + " . ");
		CloseableHttpResponse response = client.execute((HttpUriRequest) request);
		String result = EntityUtils.toString((HttpEntity) response.getEntity());
		LOGGER.info("STATUS_CODE = " + response.getStatusLine().getStatusCode() + " ,RESPONSE_JSON = " + result);
		return result;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addParam(String key, String value) {
		this.requestParams.put(key, value);
	}

	public void addHeadder(String key, String value) {
		this.requestHeaders.put(key, value);
	}
}