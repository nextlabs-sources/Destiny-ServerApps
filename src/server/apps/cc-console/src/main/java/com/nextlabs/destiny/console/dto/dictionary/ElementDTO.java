package com.nextlabs.destiny.console.dto.dictionary;

import java.util.Map;
import java.util.TreeMap;

import com.nextlabs.destiny.console.enums.EnrollmentElementType;

/**
 * DTO for enrolled element.
 *
 * @author Sachindra Dasun
 */
public class ElementDTO {

    private Map<String, String> attributes;
    private String displayName;
    private String enrollment;
    private Long id;
    private EnrollmentElementType type;
    private String uniqueName;

    public ElementDTO() {
    }

    public ElementDTO(com.nextlabs.destiny.console.model.dictionary.Element element, Map<Long, String> allTypes,
                      Map<String, String> attributes) {
        id = element.getId();
        if (element.getGroup() == null) {
            if (element.getLeafElement() != null) {
                type = EnrollmentElementType.valueOf(allTypes.get(element.getLeafElement().getTypeId()));
            }
        } else {
            type = EnrollmentElementType.GROUP;
        }
        uniqueName = element.getUniqueName();
        displayName = element.getDisplayName();
        if (element.getEnrollment() != null) {
            enrollment = element.getEnrollment().getDomainName();
        }
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public EnrollmentElementType getType() {
        return type;
    }

    public void setType(EnrollmentElementType type) {
        this.type = type;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new TreeMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
