/*
 * Created on Apr 21, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.report;

/**
 * <p>
 * ReportLookupException
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public class ReportLookupException extends Exception {

	private static final long serialVersionUID = -1L;

	public ReportLookupException() {
		super();
	}

	public ReportLookupException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ReportLookupException(String message) {
		super(message);
	}

	public ReportLookupException(Throwable message) {
		super(message);
	}

}
