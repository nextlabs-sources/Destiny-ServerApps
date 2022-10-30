package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.APPLICATION_SEARCHABLE_PREFIX;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.COMPUTER_SEARCHABLE_PREFIX;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.CONTACT_SEARCHABLE_PREFIX;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.ENROLL_APPLICATIONS;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.ENROLL_COMPUTERS;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.ENROLL_CONTACTS;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.ENROLL_USERS;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.HOST_SEARCHABLE_PREFIX;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.SCIM_DOMAIN;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.START_TIME;
import static com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants.USER_SEARCHABLE_PREFIX;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentPropertyDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.ElementFieldType;
import com.nextlabs.destiny.console.enums.EnrollmentPropertyType;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.DirtyUpdateException;
import com.nextlabs.destiny.console.model.dictionary.ElementType;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.model.dictionary.EnrollmentProperty;
import com.nextlabs.destiny.console.model.dictionary.FieldMapping;
import com.nextlabs.destiny.console.model.dictionary.Property;
import com.nextlabs.destiny.console.repositories.dictionary.ElementTypeRepository;
import com.nextlabs.destiny.console.repositories.dictionary.EnrollmentRepository;
import com.nextlabs.destiny.console.repositories.dictionary.PropertyRepository;
import com.nextlabs.destiny.console.services.EnrollmentMgmtService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.enrollment.EnrollmentServiceWrapperService;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;

/**
 * Service implementation for enrollment management functions.
 *
 * @author Sachindra Dasun.
 */
@Service
public class EnrollmentMgmtServiceImpl implements EnrollmentMgmtService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentMgmtServiceImpl.class);
    @Value("${cc.home}")
    private String ccHome;
    @Autowired
    private ElementTypeRepository elementTypeRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EnrollmentServiceWrapperService enrollmentServiceWrapperService;
    @Autowired
    private EntityAuditLogDao entityAuditLogDao;
    @Autowired
    private MessageBundleService msgBundle;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private TextEncryptor textEncryptor;

    @Override
    public List<EnrollmentDTO> findAll() {
        return enrollmentRepository.findByIsActiveIsTrue(Sort.by("lastUpdatedDate").descending()).stream()
                .filter(enrollment -> enrollment.getEnrollmentType() != EnrollmentType.SCIM)
                .map(EnrollmentDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO findById(Long id) {
        return enrollmentRepository.findById(id)
                .map(EnrollmentDTO::new)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void checkAutoSync(Long id) throws ConsoleException {
        Enrollment enrollment = enrollmentRepository
                .findById(id)
                .orElseThrow(NoSuchElementException::new);
        if (!enrollment.isRecurring()) {
            enrollmentServiceWrapperService.cancelAutoSync(enrollment.getDomainName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(EnrollmentDTO enrollmentDTO) throws ConsoleException {
        String oldValue = null;
        Enrollment enrollment;
        if (enrollmentDTO.getId() == null) {
            validateEnrollmentDomainName(enrollmentDTO.getName());
            enrollment = new Enrollment();
        } else {
            enrollment = enrollmentRepository
                    .findById(enrollmentDTO.getId())
                    .orElseThrow(NoSuchElementException::new);
            if (enrollmentDTO.getVersion() < enrollment.getVersion()) {
                throw new DirtyUpdateException(
                        msgBundle.getText("server.error.dirty.update.code"),
                        msgBundle.getText("server.error.dirty.update"));
            }
            if (!StringUtils.equals(enrollment.getDomainName(), enrollmentDTO.getName())) {
                validateEnrollmentDomainName(enrollmentDTO.getName());
            }
            oldValue = enrollment.getAuditString();
        }
        enrollment.updateFrom(enrollmentDTO);
        updateEnrollmentProperties(enrollment, enrollmentDTO);
        enrollment.setRecurring(enrollment.getEnrollmentProperty(START_TIME)
                .map(property -> StringUtils.isNotBlank(property.getValue())
                        && Long.parseLong(property.getValue()) > 0)
                .orElse(false));
        updateFieldMappings(enrollment);
        enrollmentRepository.save(enrollment);
        entityAuditLogDao.addEntityAuditLog(enrollmentDTO.getId() == null ? AuditAction.UPDATE : AuditAction.CREATE,
                AuditableEntity.ENROLLMENT_TOOLS.getCode(), enrollment.getId(), oldValue, enrollment.getAuditString());
        logger.info("Enrollment saved: {}", enrollment.getId());
        return enrollment.getId();
    }

    private void validateEnrollmentDomainName(String domainName) throws ConsoleException {
        if (domainName.equalsIgnoreCase(SCIM_DOMAIN)) {
            throw new ConsoleException(
                    String.format("Domain name %s is reserved for SCIM enrollments. Please choose another domain name.",
                            domainName));
        }
        if (enrollmentRepository.findByDomainNameIgnoreCase(domainName).orElse(null) != null) {
            throw new ConsoleException(
                    String.format("Enrollment with domain name %s already exists", domainName));
        }
    }

    /**
     * Update filed mappings by merging existing enrollment properties with updated enrollment properties.
     *
     * @param enrollment    to update
     * @param enrollmentDTO contains updated enrollment properties
     */
    private void updateEnrollmentProperties(Enrollment enrollment, EnrollmentDTO enrollmentDTO) {
        setEncryptedProperties(enrollment, enrollmentDTO);
        Set<String> updatedPropertyNames = new HashSet<>();
        enrollmentDTO.getValues().forEach(enrollmentPropertyDTO -> {
            updatedPropertyNames.add(enrollmentPropertyDTO.getName());
            enrollment.getEnrollmentProperty(enrollmentPropertyDTO.getName())
                    .ifPresentOrElse(enrollmentProperty -> enrollmentProperty.setValue(enrollmentPropertyDTO.getValue()),
                            () -> enrollment.getProperties().add(new EnrollmentProperty(enrollment, enrollmentPropertyDTO)));
        });
        Iterator<EnrollmentProperty> iterator = enrollment.getProperties().iterator();
        while (iterator.hasNext()) {
            EnrollmentProperty property = iterator.next();
            if (!updatedPropertyNames.contains(property.getName())) {
                property.setEnrollment(null);
                iterator.remove();
            }
        }
    }

    private void setEncryptedProperties(Enrollment enrollment, EnrollmentDTO enrollmentDTO) {
        // Update password only if changed.
        enrollmentDTO.getEnrollmentProperty(EnrollmentConstants.PASSWORD).ifPresent(enrollmentPropertyDTO -> {
            if (StringUtils.isNotEmpty(enrollmentPropertyDTO.getValue()) &&
                    !EnrollmentPropertyDTO.ENROLLMENT_DISPLAY_VALUE.equals(enrollmentPropertyDTO.getValue())) {
                enrollmentPropertyDTO.setValue(textEncryptor.encrypt(enrollmentPropertyDTO.getValue()));
            } else {
                enrollmentPropertyDTO.setValue(enrollment.getEnrollmentPropertyValue(EnrollmentConstants.PASSWORD));
            }
        });
        enrollmentDTO.getEnrollmentProperty(EnrollmentConstants.APPLICATION_KEY).ifPresent(enrollmentPropertyDTO -> {
            if (StringUtils.isNotEmpty(enrollmentPropertyDTO.getValue()) &&
                    !EnrollmentPropertyDTO.ENROLLMENT_DISPLAY_VALUE.equals(enrollmentPropertyDTO.getValue())) {
                enrollmentPropertyDTO.setValue(textEncryptor.encrypt(enrollmentPropertyDTO.getValue()));
            } else {
                enrollmentPropertyDTO.setValue(enrollment.getEnrollmentPropertyValue(EnrollmentConstants.APPLICATION_KEY));
            }
        });
    }

    /**
     * Update filed mappings by merging existing field mappings with updated field mappings.
     *
     * @param enrollment to update
     */
    private void updateFieldMappings(Enrollment enrollment) {
        Set<Long> updatedFiledIds = new HashSet<>();
        getFiledMappings(enrollment).forEach(fieldMapping -> {
            updatedFiledIds.add(fieldMapping.getField().getId());
            enrollment.getFiledMappingByFieldId(fieldMapping.getField().getId())
                    .ifPresentOrElse(mapping -> mapping.setExternalName(fieldMapping.getExternalName()),
                            () -> enrollment.getExternalMappings().add(fieldMapping));
        });
        Iterator<FieldMapping> iterator = enrollment.getExternalMappings().iterator();
        while (iterator.hasNext()) {
            FieldMapping fieldMapping = iterator.next();
            if (!updatedFiledIds.contains(fieldMapping.getField().getId())) {
                fieldMapping.setEnrollment(null);
                iterator.remove();
            }
        }
    }

    /**
     * Create the list of filed mappings. Filed mappings are created for each property.
     *
     * @param enrollment contains the list of properties
     * @return the list of filed mappings
     */
    private List<FieldMapping> getFiledMappings(Enrollment enrollment) {
        List<FieldMapping> fieldMappings = new ArrayList<>();

        if (enrollment.getEnrollmentProperty(ENROLL_USERS)
                .map(property -> Boolean.parseBoolean(property.getValue())).orElse(false)) {
            addFieldMappings(enrollment, USER_SEARCHABLE_PREFIX, fieldMappings);
        }
        if (enrollment.getEnrollmentProperty(ENROLL_CONTACTS)
                .map(property -> Boolean.parseBoolean(property.getValue())).orElse(false)) {
            addFieldMappings(enrollment, CONTACT_SEARCHABLE_PREFIX, fieldMappings);
        }
        if (enrollment.getEnrollmentProperty(ENROLL_COMPUTERS)
                .map(property -> Boolean.parseBoolean(property.getValue())).orElse(false)) {
            addFieldMappings(enrollment, COMPUTER_SEARCHABLE_PREFIX, fieldMappings);
        }
        if (enrollment.getEnrollmentProperty(ENROLL_APPLICATIONS)
                .map(property -> Boolean.parseBoolean(property.getValue())).orElse(false)) {
            addFieldMappings(enrollment, APPLICATION_SEARCHABLE_PREFIX, fieldMappings);
        }
        return fieldMappings;
    }

    /**
     * Add filed mappings to the filed mappings list.
     *
     * @param enrollment       contains the list of properties
     * @param searchablePrefix prefix to filter properties
     * @param fieldMappings    list of filed mappings to add
     */
    private void addFieldMappings(Enrollment enrollment, String searchablePrefix,
                                  List<FieldMapping> fieldMappings) {
        for (EnrollmentProperty enrollmentProperty : enrollment.getProperties().stream().
                filter(property -> property.getName().startsWith(searchablePrefix))
                .collect(Collectors.toList())) {
            String[] sections = enrollmentProperty.getName().split("\\.");
            if (sections.length == 3) {
                String type = sections[0].equalsIgnoreCase(COMPUTER_SEARCHABLE_PREFIX) ?
                        HOST_SEARCHABLE_PREFIX :
                        sections[0];
                String attributeType = sections[1];
                String propertyName = sections[2];

                ElementType elementType = elementTypeRepository.findByNameIgnoreCase(type)
                        .orElseThrow(() -> new NoSuchElementException(String.format("Element type: %s does not exist", type)));
                Property property = propertyRepository
                        .findByParentTypeIdAndNameIgnoreCaseAndDeletedAndTypeEqualsIgnoreCase(elementType.getId(),
                                propertyName, 'N', ElementFieldType.getAttributeTypeFromLabel(attributeType))
                        .orElseThrow(() -> new NoSuchElementException(String.format("Property: %s does not exist for type: %s",
                                propertyName, type)));
                fieldMappings.add(new FieldMapping(enrollment, property, elementType, enrollmentProperty.getValue()));
            }
        }
    }

    /**
     * Sync enrollment data.
     *
     * @param id enrollment id
     * @throws ConsoleException if an error occurred
     */
    @Override
    public void sync(Long id) throws ConsoleException {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
        enrollmentServiceWrapperService.sync(enrollment.getDomainName());
    }

    @Override
    public void upload(Long id, MultipartFile multipartFile, boolean delta) throws IOException {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
        String fileName = String.format("%d_%s.ldif", id, delta ? "delta" : "complete");
        Path dataFile = Paths.get(ccHome, "tools", "enrollment", "data", fileName);
        Files.createDirectories(dataFile.getParent());
        Files.deleteIfExists(dataFile);
        Files.write(dataFile, multipartFile.getBytes());
        enrollment.getEnrollmentProperty(EnrollmentConstants.LDIF_NAME_PROPERTY)
                .ifPresentOrElse(enrollmentProperty -> enrollmentProperty.setValue(fileName),
                        () -> {
                            EnrollmentProperty enrollmentProperty = new EnrollmentProperty();
                            enrollmentProperty.setEnrollment(enrollment);
                            enrollmentProperty.setName(EnrollmentConstants.LDIF_NAME_PROPERTY);
                            enrollmentProperty.setType(EnrollmentPropertyType.ST);
                            enrollmentProperty.setValue(fileName);
                            enrollment.getProperties().add(enrollmentProperty);
                        });
        enrollmentRepository.save(enrollment);
    }

    /**
     * Set enrollment to inactive state and invalidate all elements created by this enrollment.
     *
     * @param id enrollment id to delete.
     * @throws ConsoleException if an error occurred.
     */
    @Override
    public void remove(Long id) throws ConsoleException, IOException, PolicyEditorException {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
        String oldValue = enrollment.getAuditString();
        if (enrollment.getEnrollmentType().equals(EnrollmentType.LDIF)) {
            Files.walk(Paths.get(ccHome, "tools", "enrollment", "data"))
                    .filter(path -> path.toFile().exists())
                    .forEach(file -> {
                        try {
                            Pattern p = Pattern.compile("\\d+");
                            Matcher m = p.matcher(file.getFileName().toString());
                            if (m.find()) {
                                if (m.group().equals(id.toString())) {
                                    Files.delete(file);
                                }
                            }
                        } catch (IOException e) {
                            logger.info("Path not found");
                        }
                    });
        }
        enrollmentServiceWrapperService.remove(enrollment.getDomainName());
        enrollment = enrollmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
        enrollment.setDomainName(String.format("INACTIVE_%d_%s", enrollment.getId(), enrollment.getDomainName()));
        enrollmentRepository.save(enrollment);
        entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.ENROLLMENT_TOOLS.getCode(), id,
                oldValue, enrollment.getAuditString());
        logger.info("Enrollment deleted: {}", id);
    }

    /**
     * Check enrollment sync status.
     *
     * @param id enrollment id to check sync status
     */
    @Override
    public boolean isSyncing(Long id) {
        boolean isSync = enrollmentRepository.findById(id).orElseThrow(NoSuchElementException::new).isSyncing();
        logger.info("Enrollment {} is syncing: {}", id, isSync);
        return isSync;
    }
}
