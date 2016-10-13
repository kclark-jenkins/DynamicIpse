package com.actuate.sample.admin;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.krisbox.dynamic.ipse.tests.JunitLogging;

import com.actuate.aces.idapi.Authenticator;
import com.actuate.aces.idapi.control.ActuateException;
import com.actuate.schemas.AdminOperation;
import com.actuate.schemas.Administrate;
import com.actuate.schemas.CreateUser;
import com.actuate.schemas.DeleteUser;
import com.actuate.schemas.User;
import com.actuate.schemas.UserViewPreference;

public class OTAdminTasks {
	private final Logger  		 LOGGER   		  = Logger.getLogger(this.getClass());
	private final String  		 PROPERTIES_FILE  = new String("icse.properties");
	private final String		 PROTOCOL;
	private final String  		 HOSTNAME;
	private final String  		 PORT;
	private final String  		 USERNAME;
	private final String  		 PASSWORD;
	private final String		 IHUB_URL;
	private       Authenticator	 auth;
	private		  Properties 	 prop;
	private		  AdminOperation adminOperations[] = new AdminOperation[1];
	private		  Administrate   administrate;

	public OTAdminTasks() {
		getProperties();
		HOSTNAME = new String(prop.getProperty("HOSTNAME"));
		PORT	 = new String(prop.getProperty("PORT"));
		USERNAME = new String(prop.getProperty("USERNAME"));
		PASSWORD = new String(prop.getProperty("PASSWORD"));
		PROTOCOL = new String(prop.getProperty("PROTOCOL"));
		IHUB_URL = new String(PROTOCOL + "://" + HOSTNAME + ":" + PORT);
	}
	
	public boolean createUser(String pUsername, String pVolume){
		try{
			LOGGER.info("Creating new Authenticator object with " + IHUB_URL + ", " + USERNAME + ", " + PASSWORD + ", " + pVolume);
			auth = new Authenticator(IHUB_URL, USERNAME, PASSWORD, pVolume);

			LOGGER.info("Creating new AdminOperation");
			AdminOperation adminOperations[] = new AdminOperation[1];

			LOGGER.info("Creating user " + pUsername);
			User user = new User();
			user.setName(pUsername);
			user.setPassword("password");
			user.setHomeFolder("/home/" + pUsername);
			user.setViewPreference(UserViewPreference.Default);
			user.setMaxJobPriority(1000L);

			CreateUser createUser = new CreateUser();
			createUser.setUser(user);
			createUser.setIgnoreDup(true);

			adminOperations[0] = new AdminOperation();
			adminOperations[0].setCreateUser(createUser);

			administrate = new Administrate();
			administrate.setAdminOperation(adminOperations);
			auth.getAcxControl().proxy.administrate(administrate);			
		} catch (Exception ex){
			LOGGER.error(ex.getMessage());
			return false;
		}		
		return true;
	}
	
	public boolean deleteUser(String pUsername, String pVolume){
		try {
			auth = new Authenticator(IHUB_URL, USERNAME, PASSWORD, pVolume);

			DeleteUser delUser = new DeleteUser();
			delUser.setName(pUsername);
			
			adminOperations[0] = new AdminOperation();
			adminOperations[0].setDeleteUser(delUser);

			administrate = new Administrate();
			administrate.setAdminOperation(adminOperations);
			auth.getAcxControl().proxy.administrate(administrate);
		} catch (MalformedURLException | ServiceException | ActuateException | RemoteException e) {
			LOGGER.error(e);
			return false;
		}
		
		return false;
	}
	
	private void getProperties() {
		try {
			prop = new Properties();
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			
			prop.load(input);
		}catch(IOException ex){
			LOGGER.log(JunitLogging.JUNIT,ex);
		}
	}
}
