 /*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
 package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.util.List;

public class PALogDetailsData extends LogDetailsData {
	
	private List<ObligationLogData> obligationLogData;
	private PADetailsTableData logDetailsData;
	
	public List<ObligationLogData> getObligationLogData() {
		return obligationLogData;
	}

	public void setObligationLogData(List<ObligationLogData> obligationLogData) {
		this.obligationLogData = obligationLogData;
	}

	public PADetailsTableData getSingleLogDetailsData() {
		return logDetailsData;
	}

	public void setSingleLogDetailsData(PADetailsTableData logDetailsData) {
		this.logDetailsData = logDetailsData;
	}
}
