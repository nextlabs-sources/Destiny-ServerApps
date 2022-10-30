/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author safdar
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/helpers/UserComponentEntityResolver.java#1 $
 */

public class UserComponentEntityResolver {

    /**
     * Advanced qualifier that the expert user may enter (most users won't)
     */
    private static final String USER_QUALIFIER = "(User)";
    private static final String USER_CLASS_QUALIFIER = "(Group)";

    private List users;
    private List userClasses;

    /**
     * Constructor
     *  
     */
    public UserComponentEntityResolver(String entityList) {
        super();
        this.users = new ArrayList();
        this.userClasses = new ArrayList();
        if (entityList != null) {
            String[] entityArray = ExpressionCutter.convertToStringList(entityList).getValues();
            if (entityArray != null) {
                for (int i = 0; i < entityArray.length; i++) {
                    String entity = entityArray[i].trim();

                    // Determine if this is a user/user-group.
                    if (entity.startsWith(getUserQualifier())) {
                        String entityName = entity.substring(getUserQualifier().length());
                        this.users.add(entityName.trim());
                    } else if (entity.startsWith(getUserClassQualifier())) {
                        String entityName = entity.substring(getUserClassQualifier().length());
                        this.userClasses.add(entityName.trim());
                    } else {
                        //Already trimmed
                        this.users.add(entity);
                        this.userClasses.add(entity);
                    }
                }
            }
        }
    }

    /**
     * Returns an array of names for the users that are qualified with a
     * "(User)" qualifier. It is assumed that the returned strings represent the
     * principal names (also called 'display names') of the users.
     * 
     * @return
     */
    public String[] getQualifiedUsers() {
        String[] userArray = new String[this.users.size()];
        this.users.toArray(userArray);
        return userArray;
    }

    /**
     * Returns an array of group names that are qualified with the "(Group)"
     * qualifier. These names are not guaranteed to be unique.
     * 
     * @return
     */
    public String[] getQualifiedUserClasses() {
        String[] userClassArray = new String[this.userClasses.size()];
        this.userClasses.toArray(userClassArray);
        return userClassArray;
    }

    /**
     * Returns the user qualifier expression
     * 
     * @return the user qualifier expression
     */
    protected String getUserQualifier() {
        return USER_QUALIFIER;
    }

    /**
     * Returns the user class qualifier expression
     * 
     * @return the user class qualifier expression
     */
    protected String getUserClassQualifier() {
        return USER_CLASS_QUALIFIER;
    }

    /**
     * This is a utility method that generates a qualified name for a given user
     * name.
     * 
     * @param userName
     *            the user name
     * @return the qualified name for the user.
     */
    public static String createUserQualification(final String userName) {
        String result = null;
        if (userName != null) {
            result = userName.trim();
        }
        return result;
    }

    /**
     * This is a utility method that generates a qualified name for a given user
     * group.
     * 
     * @param userClassName
     *            user class name
     * @return the qualified name for the user group.
     */
    public static String createUserClassQualification(final String userClassName) {
        String result = null;
        if (userClassName != null) {
            result = userClassName.trim();
        }
        return result;
    }

}
