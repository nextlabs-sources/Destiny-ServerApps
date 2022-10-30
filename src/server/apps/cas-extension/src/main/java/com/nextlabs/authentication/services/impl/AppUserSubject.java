package com.nextlabs.authentication.services.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.subject.Subject;
import com.bluejungle.pf.domain.epicenter.subject.ISubjectType;

/**
 * Entity to consider application user as a subject for delegation policy evaluation.
 *
 * @author Sachindra Dasun
 */
public class AppUserSubject extends Subject {

    private static final long serialVersionUID = 8280847905015377691L;
    private final DynamicAttributes attributes;

    public AppUserSubject(String uid, String uniqueName, String name, Long id,
                          ISubjectType subjectType, DynamicAttributes attributes) {
        super(uid, uniqueName, name, id, subjectType);
        this.attributes = Optional.ofNullable(attributes).orElse(DynamicAttributes.EMPTY);
    }

    @Override
    public IEvalValue getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public synchronized Set<Map.Entry<String, IEvalValue>> getEntrySet() {
        return Collections.unmodifiableSet(attributes.entrySet());
    }

    public void setAttribute(String name, IEvalValue value) {
        attributes.put(name, value);
    }

    @Override
    public boolean isCacheable() {
        return attributes.isEmpty();
    }

}
