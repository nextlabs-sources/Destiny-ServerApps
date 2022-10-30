package com.nextlabs.destiny.console.dto.policymgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nextlabs.destiny.console.enums.Operator;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class MemberCondition implements Serializable {

    private static final long serialVersionUID = -6931552668710433696L;

    @ApiModelProperty(position = 10)
    private Operator operator; //IN or NOT IN

    @ApiModelProperty(position = 20)
    private List<MemberDTO> members;

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<MemberDTO> getMembers() {
        if (members == null) {
            members = new ArrayList<>();
        }
        return members;
    }

    public void setMembers(List<MemberDTO> members) {
        this.members = members;
    }

}