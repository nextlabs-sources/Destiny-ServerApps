package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableMember
        implements Comparable<ComparableMember> {

    private final String name;
    private final String type;
    private final String memberType;
    private final String domainName;
    private final String uid;
    private final String uniqueName;
    private final String description;

    public ComparableMember(MemberDTO memberDTO) {
        super();
        name = memberDTO.getName();
        type = memberDTO.getType();
        memberType = memberDTO.getMemberType();
        domainName = memberDTO.getDomainName();
        uid = memberDTO.getUid();
        uniqueName = memberDTO.getUniqueName();
        description = memberDTO.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableMember)) return false;

        ComparableMember that = (ComparableMember) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(type, that.type)
                .append(memberType, that.memberType)
                .append(domainName, that.domainName)
                .append(uid, that.uid)
                .append(uniqueName, that.uniqueName)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, memberType, domainName, uid, uniqueName, description);
    }

    @Override
    public int compareTo(ComparableMember comparableMember) {
        if(name != null && !name.equals(comparableMember.name)) {
            return name.compareTo(comparableMember.name);
        }
        if(type != null && !type.equals(comparableMember.type)) {
            return type.compareTo(comparableMember.type);
        }
        if(memberType != null && !memberType.equals(comparableMember.memberType)) {
            return memberType.compareTo(comparableMember.memberType);
        }
        if(domainName != null && !domainName.equals(comparableMember.domainName)) {
            return domainName.compareTo(comparableMember.domainName);
        }
        if(uid != null && !uid.equals(comparableMember.uid)) {
            return uid.compareTo(comparableMember.uid);
        }
        if(uniqueName != null && !uniqueName.equals(comparableMember.uniqueName)) {
            return uniqueName.compareTo(comparableMember.uniqueName);
        }
        if(description != null && !description.equals(comparableMember.description)) {
            return description.compareTo(comparableMember.description);
        }

        return 0;
    }
}
