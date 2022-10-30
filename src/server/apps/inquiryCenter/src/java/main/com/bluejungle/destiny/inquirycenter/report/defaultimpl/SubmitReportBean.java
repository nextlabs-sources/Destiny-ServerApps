package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.report.IMyReportsPageBean;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.nextlabs.destiny.inquirycenter.report.defaultimpl.BirtReportTransform;

public class SubmitReportBean {
	private IMyReportsPageBean myReportBean;
	
	public IMyReportsPageBean getMyReportBean() {
		return myReportBean;
	}

	public void setMyReportBean(IMyReportsPageBean myReportBean) {
		this.myReportBean = myReportBean;
	}

	public BirtReportTransform getReportTransform()
	{
		IReport selectedReport = getMyReportBean().getSelectedReport();
		BirtReportTransform reportTransform = new BirtReportTransform(selectedReport);
		
		return reportTransform;
	}
}
