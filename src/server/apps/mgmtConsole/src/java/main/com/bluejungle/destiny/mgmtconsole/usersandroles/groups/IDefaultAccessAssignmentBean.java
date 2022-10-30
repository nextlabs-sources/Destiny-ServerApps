package com.bluejungle.destiny.mgmtconsole.usersandroles.groups;

/**
 * IDefaultAccessAssignmentBean specifies the default access assignments given
 * to a particular user group or user (prinicpal) when an object is created by a
 * user group
 * 
 * @author sgoldstein
 */
public interface IDefaultAccessAssignmentBean {

    /**
     * Retrieve the ID of the principal which will receive the access assignments
     * @return the ID of the principal which will receive the access assignments
     */
    public String getReceivingPrincipalId();

    /**
     * Retrieve the title of the principal which will receive the access assignments
     * 
     * @return the title of the principal which will receive the access assignments
     *  
     */
    public String getReceivingPrincipalTitle();

    /**
     * Determine if the receiving principal will be given read access
     * 
     * @return true if the receiving principal will given read access; false
     *         otherwise
     *  
     */
    public boolean isPrincipalGivenReadAccess();

    /**
     * Set whether or not the receiving principal will be given read access
     * 
     * @param giveAccess
     *            true to grant read access; false otherwise
     *  
     */
    public void setPrincipalGivenReadAccess(boolean giveAccess);

    /**
     * Determine if the receiving principal will be given write access
     * 
     * @return true if the receiving principal will given write access; false
     *         otherwise
     *  
     */
    public boolean isPrincipalGivenWriteAccess();

    /**
     * Set whether or not the receiving principal will be given write access
     * 
     * @param giveAccess
     *            true to grant write access; false otherwise
     *  
     */
    public void setPrincipalGivenWriteAccess(boolean giveAccess);

    /**
     * Determine if the receiving principal will be given delete access
     * 
     * @return true if the receiving principal will given delete access; false
     *         otherwise
     *  
     */
    public boolean isPrincipalGivenDeleteAccess();

    /**
     * Set whether or not the receiving principal will be given delete access
     * 
     * @param giveAccess
     *            true to grant delete access; false otherwise
     *  
     */
    public void setPrincipalGivenDeleteAccess(boolean giveAccess);
    
    /**
     * Determine if the receiving principal will be given submit access
     * 
     * @return true if the receiving principal will given submit access; false
     *         otherwise
     *  
     */
    public boolean isPrincipalGivenSubmitAccess();

    /**
     * Set whether or not the receiving principal will be given submit access
     * 
     * @param giveAccess
     *            true to grant submit access; false otherwise
     *  
     */
    public void setPrincipalGivenSubmitAccess(boolean giveAccess);

    /**
     * Determine if the receiving principal will be given deploy access
     * 
     * @return true if the receiving principal will given deploy access; false
     *         otherwise
     *  
     */
    public boolean isPrincipalGivenDeployAccess();

    /**
     * Set whether or not the receiving principal will be given deploy access
     * 
     * @param giveAccess
     *            true to grant deploy access; false otherwise
     *  
     */
    public void setPrincipalGivenDeployAccess(boolean giveAccess);

    /**
     * Determine if the receiving principal will be given access to edit permissions
     * 
     * @return true if the receiving principal will given access to edit permissions;
     *         false otherwise
     *  
     */
    public boolean isPrincipalGivenEditPermissionAccess();

    /**
     * Set whether or not the receiving principal will be given access to edit
     * permissions
     * 
     * @param giveAccess
     *            true to grant access to edit permissions; false otherwise
     *  
     */
    public void setPrincipalGivenEditPermissionAccess(boolean giveAccess);
}

