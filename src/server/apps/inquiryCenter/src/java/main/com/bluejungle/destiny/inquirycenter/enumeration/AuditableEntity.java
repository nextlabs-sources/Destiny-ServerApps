package com.bluejungle.destiny.inquirycenter.enumeration;

public enum AuditableEntity {
	
	APPLICATION("AP", "Application"),
	APPLICATION_USER("AU", "Application User"),
	USER_GROUP("UG", "User Group"),
	COMPONENT_TYPE("PM", "Component Type"),
	SUBJECT("SJ", "Subject"),
	ACTION("AC", "Action"),
	RESOURCE("RS", "Resource"),
	POLICY("PL", "Policy"),
	XACML_POLICY("XP", "Xacml Policy"),
	DELEGATE_ADMIN("DA", "Delegation Admin Policy"),
	POLICY_MIGRATION("PO", "Policy Migration"),
	REPORT("RP", "Report"),
	LOGIN("LI", "User Login"),
	LOGOUT("LO", "User Logout"),
	SECURE_STORE("SS", "Secure Store"),
	SYSTEM_CONFIGURATION("SC", "System Configuration"),
	ENVIRONMENT_CONFIGURATION("EC", "Environment Configuration"),
	COMPONENT_FOLDER("CF", "Component Folder"),
	POLICY_FOLDER("PF", "Policy Folder"),
	ENROLLMENT_TOOLS("ET", "Enrollment Tools"),
	PDP_PLUGIN("PP", "PDP Plugins");
	
	private final String code;
	
	private final String displayName;
	
	private AuditableEntity(final String code, final String displayName) {
		this.code = code;
		this.displayName = displayName;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public static AuditableEntity getEntityType(String entityCode) {
		for(AuditableEntity entity : AuditableEntity.values()) {
			if(entity.getCode().equals(entityCode)) {
				return entity;
			}
		}
		
		return null;
	}
}
