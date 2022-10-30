package com.nextlabs.destiny.console.dto.enrollment;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.nextlabs.destiny.console.annotations.ValidEnrollment;
import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.enums.EnrollmentStatus;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.model.dictionary.EnrollmentProperty;
import com.nextlabs.destiny.console.model.dictionary.Updates;

/**
 * DTO for Enrollment.
 *
 * @author Sachindra Dasun
 */
@ValidEnrollment
public class EnrollmentDTO extends BaseDTO {

    private static final long serialVersionUID = 5508936374543680030L;
    private boolean active;
    private String description;
    private Long lastUpdatedDate;
    private Long lastSyncDate;
    private String name;
    private boolean recurring;
    private EnrollmentStatus status;
    private String statusMessage;
    private EnrollmentType type;
    private Set<EnrollmentPropertyDTO> values;
    private int version;

    public EnrollmentDTO() {
    }

    public EnrollmentDTO(Enrollment enrollment) {
        id = enrollment.getId();
        active = enrollment.isActive();
        description = enrollment.getDescription();
        name = enrollment.getDomainName();
        type = enrollment.getEnrollmentType();
        lastUpdatedDate = enrollment.getLastUpdatedDate().getTime();
        values = new HashSet<>();
        for (EnrollmentProperty enrollmentProperty : enrollment.getProperties()) {
            values.add(new EnrollmentPropertyDTO(enrollmentProperty));
        }
        recurring = enrollment.isRecurring();
        status = EnrollmentStatus.ENROLLED;
        if (enrollment.isSyncing()) {
            status = EnrollmentStatus.IN_PROGRESS;
        } else {
            enrollment.getUpdates().stream()
                    .max(Comparator.comparingLong(Updates::getId)).ifPresent(updates -> {
                status = updates.getIsSuccessful() == 'Y' ? EnrollmentStatus.SUCCESS : EnrollmentStatus.FAILED;
                statusMessage = updates.getErrMessage();
            });
        }
        enrollment.getUpdates().stream().filter(updates -> updates.getIsSuccessful() == 'Y')
                .max(Comparator.comparingLong(Updates::getId)).ifPresent(updates -> lastSyncDate = updates.getEndTime());
        version = enrollment.getVersion();
    }

    public Optional<EnrollmentPropertyDTO> getEnrollmentProperty(String name) {
        return getValues().stream()
                .filter(property -> name.equalsIgnoreCase(property.getName()))
                .findFirst();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Long lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public EnrollmentType getType() {
        return type;
    }

    public void setType(EnrollmentType type) {
        this.type = type;
    }

    public Long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Set<EnrollmentPropertyDTO> getValues() {
        if (values == null) {
            values = new HashSet<>();
        }
        return values;
    }

    public void setValues(Set<EnrollmentPropertyDTO> values) {
        this.values = values;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEnrollmentPropertyValue(String name) {
        return values.stream()
                .filter(property -> name.equalsIgnoreCase(property.getName()))
                .findFirst()
                .map(EnrollmentPropertyDTO::getValue).orElse("");
    }

}
