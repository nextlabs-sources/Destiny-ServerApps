package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.enums.Operator;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ComparableMemberCondition
        implements Comparable<ComparableMemberCondition> {

    private final Operator operator;
    private final Set<ComparableMember> members;

    public ComparableMemberCondition(MemberCondition memberCondition) {
        super();
        operator = memberCondition.getOperator();
        members = new TreeSet<>();
        memberCondition.getMembers().forEach(member
                -> members.add(new ComparableMember(member)));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableMemberCondition)) return false;

        ComparableMemberCondition that = (ComparableMemberCondition) o;

        return new EqualsBuilder()
                .append(operator, that.operator)
                .append(members, that.members)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, members);
    }

    @Override
    public int compareTo(ComparableMemberCondition comparableMemberCondition) {
        if(operator != null && !operator.equals(comparableMemberCondition.operator)) {
            return operator.compareTo(comparableMemberCondition.operator);
        }
        if(!members.equals(comparableMemberCondition.members)) {
            return 1;
        }

        return 0;
    }
}
