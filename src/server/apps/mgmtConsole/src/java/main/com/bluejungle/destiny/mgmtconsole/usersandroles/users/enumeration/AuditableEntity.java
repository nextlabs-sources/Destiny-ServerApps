package com.bluejungle.destiny.mgmtconsole.usersandroles.users.enumeration;

public enum AuditableEntity {
	
	APPLICATION("AP", "Application"),
	APPLICATION_USER("AU", "Application User"),
	COMPONENT_TYPE("PM", "Component Type"),
	SUBJECT("SJ", "Subject"),
	ACTION("AC", "Action"),
	RESOURCE("RS", "Resource"),
	POLICY("PL", "Policy"),
	DELEGATE_ADMIN("DA", "Delegation Admin Policy"),
	REPORT("RP", "Report"),
	LOGIN("LI", "User Login"),
	LOGOUT("LO", "User Logout"),
	SYSTEM_CONFIGURATION("SC", "System Configuration"),
	ENVIRONMENT_CONFIGURATION("EC", "Environment Configuration");
	
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
