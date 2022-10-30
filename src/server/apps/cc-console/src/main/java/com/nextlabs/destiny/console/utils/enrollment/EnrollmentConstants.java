package com.nextlabs.destiny.console.utils.enrollment;

public final class EnrollmentConstants {


    public static final String SERVER = "server";
    public static final String PORT = "port";
    public static final String SECURE_TRANSPORT_MODE = "secure.transport.mode"; // One of SSL or TLS
    public static final String ALWAYS_TRUST_AD = "always.trust.ad"; // Require AD cert in
                                                                    // truststore?
    public static final String PASSWORD = "password";
    public static final String ENCRYPTED_PASSWORD = "encrypted_password";
    public static final String LOGIN = "login";
    public static final String FILTER = "filter";
    public static final String ROOTS = "roots";
    public static final String SSL_TRANSPORT_MODE = "SSL";
    public static final String PARENT_ID_ATTRIBUTE = "entry.attributefor.parentid";
    public static final String LAST_PARENT_ATTRIBUTE = "entry.attributefor.lastknownparent";
    public static final String IS_DELETED_ATTRIBUTE = "entry.attributefor.isdeleted";
    public static final String DIRSYNC_ENABLED = "EnableADDirChgReplication".toLowerCase();
    public static final String PAGING_ENABLED = "IsPagingEnabled".toLowerCase();

    public static final String COOKIE = "cookie";

    public static final String SID = "windowsSid";
    public static final String MAIL = "mail";

    public static final String STATIC_ID_ATTRIBUTE = "entry.attributefor.staticid";
    public static final String REQUIREMENT = ".requirements";

    /******************** USER ************************/
    public static final String ENROLL_USERS = "enroll.users";

    public static final String USER_SEARCHABLE_PREFIX = "user";
    public static final String USER_REQUIREMENTS = USER_SEARCHABLE_PREFIX + REQUIREMENT;
    /**************************************************/

    /******************** CONTACT ************************/
    public static final String ENROLL_CONTACTS = "enroll.contacts";
    public static final String CONTACT_SEARCHABLE_PREFIX = "contact";
    public static final String CONTACT_REQUIREMENTS = CONTACT_SEARCHABLE_PREFIX + REQUIREMENT;
    /*****************************************************/

    /******************** COMPUTER/HOST ************************/
    public static final String ENROLL_COMPUTERS = "enroll.computers";
    public static final String HOST_SEARCHABLE_PREFIX = "host";
    public static final String COMPUTER_SEARCHABLE_PREFIX = "computer";
    public static final String COMPUTER_REQUIREMENTS = COMPUTER_SEARCHABLE_PREFIX + REQUIREMENT;
    /***********************************************************/

    /******************** APPLICATION ************************/
    public static final String ENROLL_APPLICATIONS = "enroll.applications";
    public static final String APPLICATION_SEARCHABLE_PREFIX = "application";
    public static final String APPLICATION_REQUIREMENTS =
            APPLICATION_SEARCHABLE_PREFIX + REQUIREMENT;
    /*********************************************************/

    /******************** GROUPS ************************/
    public static final String ENROLL_GROUPS = "enroll.groups";
    public static final String GROUP_ENUMERATION_ATTRIBUTE = "group.attributefor.enumeration";
    public static final String GROUP_SEARCHABLE_PREFIX = "group";
    public static final String GROUP_REQUIREMENTS =  GROUP_SEARCHABLE_PREFIX + REQUIREMENT;
    public static final String STRUCTURAL_SEARCHABLE_PREFIX = "structure";
    public static final String STRUCTURAL_GROUP_REQUIREMENTS = STRUCTURAL_SEARCHABLE_PREFIX + REQUIREMENT;
    /****************************************************/

    /******************** SYNC ************************/
    public static final String START_TIME = "ScheduledSyncTime".toLowerCase();
    public static final String PULL_INTERVAL = "ScheduledSyncInterv".toLowerCase();
    public static final String TIME_FORMAT = "ScheduledSyncTimeFormat".toLowerCase();
    /**************************************************/

    /******************** OTHERS ************************/
    // Should missing attributes be skipped or should we write an empty value to the dictionary
    public static final String STORE_MISSING_ATTRIBUTES = "store.missing.attributes";
    public static final String OTHER_REQUIREMENTS = "other" + REQUIREMENT;
    public static final String SITE_SEARCHABLE_PREFIX = "site";
    public static final String FORMATTER_SUFFIX = ".formatter";
    /****************************************************/

    public static final String LDIF_NAME_PROPERTY = "ldif.filename";
    
    /******************** AZURE AD ************************/
    public static final String TENANT = "tenant";
    public static final String OAUTH_AUTHORITY = "azure-oauth-authority";
    public static final String APPLICATION_KEY = "application-key";
    public static final String APPLICATION_ID = "application-id";
    public static final String USER_EXTENDED_ATTRIBUTES_QUERY = "user.extended.attributes.query";
    /****************************************************/
    
    /******************** PORTAL ************************/
    public static final String PASSWORD_PROPERTY = "password";
    public static final String LOGIN_PROPERTY = "login";
    public static final String DOMAIN_PROPERTY = "domain";
    public static final String PORTALS_PROPERTY = "portals";
    /****************************************************/
    
    /******************** SCIM ************************/
    public static final String SCIM_DOMAIN = "SCIM";
    /****************************************************/
    

    private EnrollmentConstants() {
        throw new IllegalStateException();
    }
}
