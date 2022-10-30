package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.bluejungle.destiny.inquirycenter.report.IMyReportsPageBean;
import com.bluejungle.destiny.inquirycenter.report.IReport;
import com.bluejungle.destiny.inquirycenter.report.IReportPageBean;
import com.bluejungle.destiny.inquirycenter.report.ReportViewException;
import com.bluejungle.destiny.types.report.v1.ReportQuerySpec;

public class AuditLogPageBeanImpl
		extends ReportPageBeanImpl
		implements IMyReportsPageBean {
	
	private String dacLocation;

	public void load() {
		super.load();
	}
	
	@Override
	public List getReportList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReport getSelectedReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onExecuteReport(ActionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelectedReportId(Long id) {
		// TODO Auto-generated method stub
	}

	/**
	 * Sets the location of the data provider
	 * 
	 * @param location
	 *            location of the data provider
	 */
	public void setDataLocation(String location) {
		this.dacLocation = location;
	}

	@Override
	protected ReportQuerySpec getReportListQuerySpec() {
		return null;
	}

	@Override
	public void deleteSelectedReport() throws ReportViewException {

	}

	@Override
	public void updateSelectedReport() throws ReportViewException {

	}

	@Override
	public void cancelSelectedReportEdit() {

	}

	@Override
	public void createNewQuickReport() {

	}

	@Override
	public void insertSelectedReport() throws ReportViewException {

	}
}
