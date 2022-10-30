/*
 * Created on Jun 18, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.monitor.service;

import com.nextlabs.report.datagen.ResultData;

/**
 * @author nnallagatla
 *
 */
public interface IAlertDataGenerator {
	
	public ResultData getResultData(String jsonQuery) throws Exception;
	
	public void cleanup() throws Exception;
}
