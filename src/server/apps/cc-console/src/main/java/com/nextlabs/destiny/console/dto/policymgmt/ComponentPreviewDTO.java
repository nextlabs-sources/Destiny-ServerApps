package com.nextlabs.destiny.console.dto.policymgmt;

import java.util.List;

import com.bluejungle.pf.destiny.lib.LeafObject;
import com.nextlabs.destiny.console.dto.common.BaseDTO;

public class ComponentPreviewDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	private int totalEnrolledSubjects;
	private List<LeafObject> enrolledSubjects;

	public int getTotalEnrolledSubjects() {
		return totalEnrolledSubjects;
	}

	public void setTotalEnrolledSubjects(int totalMembers) {
		this.totalEnrolledSubjects = totalMembers;
	}

	public List<LeafObject> getEnrolledSubjects() {
		return enrolledSubjects;
	}

	public void setEnrolledSubjects(List<LeafObject> enrolledSubjects) {
		this.enrolledSubjects = enrolledSubjects;
	}
}
