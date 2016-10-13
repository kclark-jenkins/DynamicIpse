package com.actuate.sample.icse.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.krisbox.dynamic.ipse.tests.JunitLogging;

import com.actuate.sample.admin.OTAdminTasks;
import com.google.gson.Gson;

public class IcseUtils {
	private Logger LOGGER = Logger.getLogger(this.getClass());
	private OTAdminTasks otAdmin;
	private Properties	   		   props;
	private String  			   PROPERTIES_FILE     = new String("icse.properties");
	private String				   PROTOCOL;
	private String  			   HOSTNAME;
	private String  			   PORT;
	private String				   OTCOOKBOOK_ROOT;
	private String				   OTCOOKBOOK_HOST;
	private String				   OTCOOKBOOK_PORT;
	private String				   OTCOOKBOOK_PROTOCOL;
	private String				   OTCOOKBOOK_URL;
	private String				   USERNAME;
	private String				   PASSWORD;
	private String				   USER_PARAMETER;
	private String				   CONNECTION_STRING_PARAMETER;
	private String				   VOLUME_PARAMETER;
	private String		           host;
	
	public IcseUtils() {
		otAdmin  = new OTAdminTasks();
		props    = getProperties();
		HOSTNAME = new String(props.getProperty("HOSTNAME"));
		PORT	 = new String(props.getProperty("PORT"));
		USERNAME = new String(props.getProperty("USERNAME"));
		PASSWORD = new String(props.getProperty("PASSWORD"));
		PROTOCOL = new String(props.getProperty("PROTOCOL"));
		host     = new String(PROTOCOL + "://" + HOSTNAME + ":" + PORT);
		
		OTCOOKBOOK_ROOT     = new String(props.getProperty("OTCOOKBOOK_ROOT"));
		OTCOOKBOOK_HOST     = new String(props.getProperty("OTCOOKBOOK_HOST"));
		OTCOOKBOOK_PORT     = new String(props.getProperty("OTCOOKBOOK_PORT"));
		OTCOOKBOOK_PROTOCOL = new String(props.getProperty("OTCOOKBOOK_PROTOCOL"));
		OTCOOKBOOK_URL		= new String(OTCOOKBOOK_PROTOCOL + "://" + OTCOOKBOOK_HOST + ":" + OTCOOKBOOK_PORT + "/" + OTCOOKBOOK_ROOT);
		
		USER_PARAMETER              = new String(props.getProperty("USER_PARAMETER"));
		CONNECTION_STRING_PARAMETER = new String(props.getProperty("CONNECTION_STRING_PARAMETER"));
		VOLUME_PARAMETER			= new String(props.getProperty("VOLUME_PARAMETER"));
	}

	public String getiHubHost()                     {return host;}
	public String getCookbookHost()                 {return OTCOOKBOOK_URL;}
	public String getiHubUsername()                 {return USERNAME;}
	public String getiHubPassword()                 {return PASSWORD;}
	public OTAdminTasks getOTAdmin()                {return otAdmin;}
	public String getUserParameterKey()             {return USER_PARAMETER;}
	public String getVolumeParameterKey()           {return VOLUME_PARAMETER;}
	public String getConnectionStringParameterKey() {return CONNECTION_STRING_PARAMETER;}
	
	public String createString(HttpServletRequest request, IcseUtils icseUtils) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String[]> p = request.getParameterMap();
			Map<String, String> params = new HashMap<String, String>();
			
			for (Map.Entry<String, String[]> entry : p.entrySet()){params.put(entry.getKey(), entry.getValue()[0]);}

			String username = encode(params.get(icseUtils.getUserParameterKey()));
			String volume = encode(params.get(icseUtils.getVolumeParameterKey()));
			String connectionString = encode(params.get(icseUtils.getConnectionStringParameterKey()));

			params.remove("username");
			params.remove("volume");
			params.remove("connectionString");

			String json = new Gson().toJson(params);

			String url = OTCOOKBOOK_URL;
			url += "/saveConnectionDetails?username=" + username;
			url += "&volume=" + volume;
			url += "&connectionString=" + connectionString;
			url += "&extendedProperties=" + encode(json);
			LOGGER.info(url);
			return url;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public String createString(HttpServletRequest request, String method) {
		try {
			String url = OTCOOKBOOK_URL;
			if(method.equals("save"))
				url += "/saveConnectionDetails";
			else
				url += "/getConnectionDetails";
			
			@SuppressWarnings("unchecked")
			Map<String, String[]> p = request.getParameterMap();
			Map<String, String> params = new HashMap<String, String>();
			for (Map.Entry<String, String[]> entry : p.entrySet()) {
				params.put(entry.getKey(), entry.getValue()[0]);
			}

			String username = encode(params.get("username"));
			String volume = encode(params.get("volume"));
			String connectionString = encode(params.get("connectionString"));

			params.remove("username");
			params.remove("volume");
			params.remove("connectionString");

			String json = new Gson().toJson(params);

			url += "?username=" + username;
			url += "&volume=" + volume;
			url += "&connectionString=" + connectionString;
			url += "&extendedProperties=" + encode(json);

			return url;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	private String encode(String text) throws UnsupportedEncodingException {

		return URLEncoder.encode(text, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
				.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
	}
	
	public String setProperties(String endpoint) {
		try {

			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringBuilder builder = new StringBuilder();
			String output;
			
			while ((output = br.readLine()) != null) {
				builder.append(output);
			}
			conn.disconnect();
			
			return builder.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Properties getProperties() {
		try {
			Properties prop = new Properties();
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			
			prop.load(input);
			return prop;
		}catch(IOException ex){
			LOGGER.log(JunitLogging.JUNIT,ex);
		}
		
		return null;
	}
}
