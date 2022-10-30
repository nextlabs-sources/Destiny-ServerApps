/*
 * Created on Jun 12, 2014
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import java.util.List;

import com.nextlabs.destiny.inquirycenter.report.birt.datagen.GroupByData;

/**
 * <p>
 * Interface for Share point related data genarator.
 * </p>
 * 
 * 
 * @author Amila Silva
 * 
 */
public interface ISharePointDataGenerator {

	/**
	 * <p>
	 * Generate Share point report related data. 
	 * </p>
	 *
	 * @param jsonModel {@link SharePointCriteriaJSONModel}
	 * @return List of GroupByData
	 */
	List<GroupByData> generateSQLAndGetResults(
			SharePointCriteriaJSONModel jsonModel);
	
	/**
	 * <p>
	 * Clean up the data generator after work done. 
	 * </p>
	 *
	 */
	void cleanUp() throws Exception;

}
