package com.nextlabs.destiny.console.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.destiny.console.model.SysInfo;

/**
 *
 * Utility class to read the server license details
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public final class LicenseCheckerUtil {
    /**
	 * 
	 */
	private static final String _EXTENDED_LICENSE_FILE_NAME = "sys_info.dat";
	private SysInfo sysInfo = null;
	
	private String licenseFolderLocation;

	public LicenseCheckerUtil(String licenseFolderLocation) throws Exception {
		super();
		this.licenseFolderLocation = licenseFolderLocation;
		this.loadLicenseFileDetails();
	}

	public SysInfo getSysInfo() {
		return sysInfo;
	}

	public void loadLicenseFileDetails() throws Exception {
		File extendedLicenseFile = new File(licenseFolderLocation, _EXTENDED_LICENSE_FILE_NAME);
		if(extendedLicenseFile.exists() && extendedLicenseFile.isFile()) {
			String encryptedLicenseInfo = IOUtils.toString(new FileInputStream(extendedLicenseFile), Charset.defaultCharset()).trim();
			ReversibleEncryptor encryptor = new ReversibleEncryptor();
			String decryptedLicenseInfo = encryptor.decrypt(encryptedLicenseInfo);
			ObjectMapper mapper = new ObjectMapper();
			this.sysInfo = mapper.readValue(decryptedLicenseInfo, SysInfo.class);
		} 
	}

}
