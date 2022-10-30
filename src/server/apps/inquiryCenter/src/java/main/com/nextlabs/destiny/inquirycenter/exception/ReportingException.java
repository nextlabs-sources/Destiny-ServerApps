/*
 * Created on Apr 7, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.exception;

/**
 * <p>
 *  ReportingException exception class to wrap exceptions related to inquiry center module.
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class ReportingException extends Exception {

	private static final long serialVersionUID = 1L;


	public ReportingException() {
		super();
	}

	public ReportingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ReportingException(String arg0) {
		super(arg0);
	}

	public ReportingException(Throwable arg0) {
		super(arg0);
	}

}
