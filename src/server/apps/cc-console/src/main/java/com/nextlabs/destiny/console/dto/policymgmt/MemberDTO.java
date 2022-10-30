package com.nextlabs.destiny.console.dto.policymgmt;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;

/*
 * Corresponds to LeafObject. Directly using LeafOfbject class caused deserialization problems.
 */
public class MemberDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "The name of the member component.", position = 10, example = "Sample Member Component")
    private String name;

    @ApiModelProperty(value = "The type of the member component.\n" +
            "When member is a User or User Group, applicable value for type is MEMBER.",
            example = "RESOURCE",
    allowableValues = "RESOURCE, ACTION, SUBJECT, MEMBER", position = 20)
    private String type;

    @ApiModelProperty(value = "The type of the member value. Applicable only when value of type attribute is MEMBER.",
            allowableValues = "USER, USER_GROUP, HOST, HOST_GROUP, APPLICATION", example = "USER", position = 30)
    private String memberType;

    @ApiModelProperty(value = "The unique name of the member. Applicable only when value of type attribute is MEMBER.",
            example = "sarah.o.myers@mycompany.com", position = 40)
    private String uniqueName;

    @ApiModelProperty(position = 50, value = "Unique identifier of the member.", example = "S-11555-12")
    private String uid;

    @ApiModelProperty(value= "Domain name of the member. Applicable only when value of type attribute is MEMBER.", position = 60,
            example = "constessa.com")
    private String domainName;

    @ApiModelProperty(position = 70, value = "Description of the member.", example = "Sample description")
    private String description;

    @ApiModelProperty(position = 80, value = "Indicates the current status of the component.",
            allowableValues = "DRAFT, APPROVED, DELETED", example = "APPROVED")
    private String status;

    @ApiModelProperty(position = 90, value = "Indicates if the member is not found while retrieving the component.")
    private boolean notFound;

    public MemberDTO() {
        super();
    }

    public MemberDTO(Long id, String type) {
        this.id = id;
        this.type = type;
        this.notFound = id < 0;
    }

    public MemberDTO(Long id, String name, String memberType, String uniqueName, String uid, String domainName) {
        super();
        this.id = id;
        this.name = StringUtils.isEmpty(name) ? uniqueName : name;
        this.type = ComponentPQLHelper.MEMBER_GROUP;
        this.memberType = memberType;
        this.uniqueName = uniqueName;
        this.uid = uid;
        this.domainName = domainName;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.notFound = id < 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }

}
