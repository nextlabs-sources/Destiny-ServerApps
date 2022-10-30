/*
 * Created on Jan 22, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 * @version $Id: //depot/main/Destiny/filledinautomatically#1 $:
 */
package com.nextlabs.destiny.console.exceptions;

public class CircularReferenceException extends Exception {
    private static final long serialVersionUID = 1L;
    private String name;
    private String statusCode;
    private String statusMsg;

    public CircularReferenceException() {
        super();
    }

    public CircularReferenceException(String name) {
        super();
        this.name = name;
    }
    
    public CircularReferenceException(String statusCode, String statusMsg) {
        super();
        this.statusCode = statusCode;
        this.statusMsg =statusMsg;
    }

    public String getName() {
        return name;
    }
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
}
