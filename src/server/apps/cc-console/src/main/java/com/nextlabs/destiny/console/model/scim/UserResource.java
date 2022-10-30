/*
 * Copyright 2015-2019 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License (GPLv2 only) or the terms of the GNU Lesser General Public License
 * (LGPLv2.1 only) as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, see <http://www.gnu.org/licenses>.
 */

package com.nextlabs.destiny.console.model.scim;

import com.bettercloud.scim2.common.BaseScimResource;
import com.bettercloud.scim2.common.annotations.Attribute;
import com.bettercloud.scim2.common.annotations.Schema;
import com.bettercloud.scim2.common.types.AttributeDefinition;

@Schema(id = "urn:nextlabs:scim:schemas:User", name = "User",
        description = "Class to represent users", ignoreUnknown = true)
public class UserResource extends BaseScimResource {

    private static final long serialVersionUID = -2098078453323431434L;

    @Attribute(
            description = "The name of the User, suitable for display "
                    + "to end-users. The name SHOULD be the full name of the User being "
                    + "described if known.",
            isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String displayName;

    @Attribute(
            description = "The first name of the User",
            isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String firstName;

    @Attribute(
            description = "The last name of the User",
            isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String lastName;

    @Attribute(description = "The email of the User", isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String mail;

    @Attribute(
            description = "Unique identifier for the User typically "
                    + "used by the user to directly authenticate to the service provider.",
            isRequired = true, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.IMMUTABLE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.SERVER)
    private String principalName;

    @Attribute(description = "The unixId of the User", isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String unixId;

    @Attribute(description = "The windowsSid of the User", isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String windowsSid;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String email) {
        this.mail = email;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getUnixId() {
        return unixId;
    }

    public void setUnixId(String unixId) {
        this.unixId = unixId;
    }

    public String getWindowsSid() {
        return windowsSid;
    }

    public void setWindowsSid(String windowsSid) {
        this.windowsSid = windowsSid;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UserResource that = (UserResource) o;

        if (displayName != null ? !displayName.equals(that.displayName)
                : that.displayName != null) {
            return false;
        }

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) {
            return false;
        }

        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) {
            return false;
        }

        if (mail != null ? !mail.equals(that.mail) : that.mail != null) {
            return false;
        }

        if (principalName != null ? !principalName.equals(that.principalName) : that.principalName != null) {
            return false;
        }

        if (unixId != null ? !unixId.equals(that.unixId) : that.unixId != null) {
            return false;
        }

        return windowsSid != null ? !windowsSid.equals(that.windowsSid) : that.windowsSid != null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (mail != null ? mail.hashCode() : 0);
        result = 31 * result + (principalName != null ? principalName.hashCode() : 0);
        result = 31 * result + (unixId != null ? unixId.hashCode() : 0);
        result = 31 * result + (windowsSid != null ? windowsSid.hashCode() : 0);

        return result;
    }
}

