package com.nextlabs.destiny.console.enums;

/**
 * 
 * @author Moushumi Seal
 *
 */
public enum UserCategory {
	
	ADMINISTRATOR("ADMIN", "Administrator"), 
	CONSOLE_USER("CONSOLE", "Console User"), 
	API_ACCOUNT("API", "API Account");

    private final String code;
    private final String displayName;

	private UserCategory(final String code, final String displayName) {
		this.code = code;
		this.displayName = displayName;
	}
	
	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}

    public static UserCategory getUserCategory(String category) {
        for (UserCategory userCategory : UserCategory.values()) {
            if (userCategory.getCode().equals(category)) {
                return userCategory;
            }
        }
        return null;
    }

}
