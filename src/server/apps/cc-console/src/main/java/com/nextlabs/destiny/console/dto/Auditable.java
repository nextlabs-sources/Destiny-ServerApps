package com.nextlabs.destiny.console.dto;

import com.nextlabs.destiny.console.exceptions.ConsoleException;

import java.text.SimpleDateFormat;

public interface Auditable {

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	String toAuditString() throws ConsoleException;
	
}
