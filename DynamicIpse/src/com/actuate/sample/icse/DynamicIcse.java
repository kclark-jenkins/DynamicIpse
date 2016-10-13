package com.actuate.sample.icse;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.actuate.iportal.security.iPortalSecurityAdapter;
import com.actuate.sample.icse.utils.IcseUtils;

public class DynamicIcse extends iPortalSecurityAdapter {
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	private IcseUtils    icseProps;
	public String        volume = null;
	public String        volumeProfile = null;
	public String        host;
	public String        sUsername = null;
	public String        sPassword = "password";

	public DynamicIcse() {
		LOGGER.info("Dynamic Constructor");
		icseProps = new IcseUtils();
	}

	public boolean authenticate(HttpServletRequest req) {
		LOGGER.info("DynamicIcse.authenticate()");
		
		String pUsername = req.getParameter(icseProps.getUserParameterKey());
		String pVolume   = req.getParameter(icseProps.getVolumeParameterKey());
		
		if(icseProps.getOTAdmin().createUser(pUsername, pVolume) == true) {
			LOGGER.info("User created");
			
			sUsername = pUsername;
			volume    = pVolume;
			
			icseProps.setProperties(icseProps.createString(req, icseProps));
			
			return true;
		}else{
			sUsername = "delegate";
			sPassword = "password";
		
			icseProps.setProperties(icseProps.createString(req, icseProps));
			
			return true;
		}
	}

	public String getDashboardTemplate() {
		return super.getDashboardTemplate();
	}

	public IcseUtils getIcseUtils(){return icseProps;}
	
	public byte[] getExtendedCredentials() {
		return super.getExtendedCredentials();
	}

	public String getPassword() {
		return sPassword;
	}

	public String getRepositoryType() {
		return super.getRepositoryType();
	}

	public String getRunAsUser() {
		return super.getRunAsUser();
	}

	public String getServerUrl() {
		return super.getServerUrl();
	}

	public String getUserHomeFolder() {
		return super.getUserHomeFolder();
	}

	public String getUserName() {
		return sUsername;
	}

	public String getVolume() {
		return volume;
	}

	public String getVolumeProfile() {
		return volumeProfile;
	}

	public boolean isEnterprise() {
		return true;
	}

}