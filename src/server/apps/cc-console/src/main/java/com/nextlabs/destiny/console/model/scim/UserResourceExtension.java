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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bettercloud.scim2.common.BaseScimResource;
import com.bettercloud.scim2.common.annotations.Attribute;
import com.bettercloud.scim2.common.annotations.Schema;
import com.bettercloud.scim2.common.types.AttributeDefinition;
import com.nextlabs.destiny.console.controllers.scim.ScimUserController;

@Schema(id = "urn:nextlabs:scim:schemas:extension:nextlabs:User", name = "UserExtension",
        description = "Class to represent users")
public class UserResourceExtension extends BaseScimResource {

    private static final Logger log = LoggerFactory.getLogger(ScimUserController.class);

    private static final long serialVersionUID = -2098078453323431434L;


    @Attribute(
            description = "department",
            isRequired = false, isCaseExact = false,
            mutability = AttributeDefinition.Mutability.READ_WRITE,
            returned = AttributeDefinition.Returned.DEFAULT,
            uniqueness = AttributeDefinition.Uniqueness.NONE)
    private String department1;

    public String getDepartment1() {
        return department1;
    }

    public void setDepartment1(String department1) {
        this.department1 = department1;
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

        UserResourceExtension that = (UserResourceExtension) o;

        return department1 != null ? department1.equals(that.department1)
                : that.department1 == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (department1 != null ? department1.hashCode() : 0);
        return result;
    }

}

