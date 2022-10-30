package com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl;

import com.bluejungle.destiny.services.management.types.UserDTO;

/**
 * Default implementation of the
 * {@see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.defaultimpl.IInternalMemberBean}
 * 
 * @author sgoldstein
 */
public class MemberBeanImpl implements IInternalMemberBean {

    private String displayName;
    private String memberId;
    private String memberUniqueName;

    /**
     * Create an instance of MemberBeanImpl
     * 
     * @param userDTO
     */
    public MemberBeanImpl(UserDTO userDTO) {
        if (userDTO == null) {
            throw new NullPointerException("userDTO cannot be null.");
        }

        this.displayName = userDTO.getLastName() + ", " + userDTO.getFirstName();
        this.memberId = userDTO.getId().toString();
        this.memberUniqueName = userDTO.getUniqueName();
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IMemberBean#getDisplayName()
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IMemberBean#getMemberId()
     */
    public String getMemberId() {
        return this.memberId;
    }

    /**
     * @see com.bluejungle.destiny.mgmtconsole.usersandroles.groups.IMemberBean#getMemberUniqueName()
     */
    public String getMemberUniqueName() {
        return this.memberUniqueName;
    }
}

