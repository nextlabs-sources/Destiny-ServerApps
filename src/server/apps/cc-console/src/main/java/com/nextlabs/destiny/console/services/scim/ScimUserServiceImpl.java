/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 25, 2020
 *
 */
package com.nextlabs.destiny.console.services.scim;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bettercloud.scim2.common.Path;
import com.bettercloud.scim2.common.annotations.Attribute;
import com.bettercloud.scim2.common.exceptions.BadRequestException;
import com.bettercloud.scim2.common.exceptions.ResourceConflictException;
import com.bettercloud.scim2.common.exceptions.ScimException;
import com.bettercloud.scim2.common.messages.ErrorResponse;
import com.bettercloud.scim2.common.messages.ListResponse;
import com.bettercloud.scim2.common.messages.PatchOperation;
import com.bettercloud.scim2.common.messages.PatchRequest;
import com.bettercloud.scim2.common.types.AttributeDefinition;
import com.bettercloud.scim2.common.types.Meta;
import com.bluejungle.dictionary.DictionaryPath;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.console.dao.dictionary.LeafElementDao;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.model.dictionary.Element;
import com.nextlabs.destiny.console.model.dictionary.ElementType;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.model.dictionary.LeafElement;
import com.nextlabs.destiny.console.model.dictionary.Property;
import com.nextlabs.destiny.console.model.dictionary.TypeField;
import com.nextlabs.destiny.console.model.dictionary.Updates;
import com.nextlabs.destiny.console.model.scim.BulkMethodType;
import com.nextlabs.destiny.console.model.scim.BulkOperation;
import com.nextlabs.destiny.console.model.scim.BulkRequest;
import com.nextlabs.destiny.console.model.scim.BulkResponseOperation;
import com.nextlabs.destiny.console.model.scim.UserResource;
import com.nextlabs.destiny.console.repositories.dictionary.ElementRepository;
import com.nextlabs.destiny.console.repositories.dictionary.ElementTypeRepository;
import com.nextlabs.destiny.console.repositories.dictionary.EnrollmentRepository;
import com.nextlabs.destiny.console.repositories.dictionary.LeafElementsRepository;
import com.nextlabs.destiny.console.repositories.dictionary.PropertyRepository;
import com.nextlabs.destiny.console.repositories.dictionary.TypeFieldsRepository;
import com.nextlabs.destiny.console.repositories.dictionary.UpdatesRepository;
import com.nextlabs.destiny.console.services.DPSProxyService;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;

/**
 *
 */
@Service
public class ScimUserServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(ScimUserServiceImpl.class);

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ElementRepository elementRepository;

    @Autowired
    private LeafElementsRepository leafElementRepository;

    @Autowired
    private TypeFieldsRepository typeFieldRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ElementTypeRepository elementTypeRepository;
    
    @Autowired
    private UpdatesRepository updatesRepository;

    @Autowired
    private DPSProxyService dpsService;

    @Autowired
    private LeafElementDao leafElementDao;

    public static final long DATETIME_MAX_TICKS = 253402271999000L; // 9999-12-31 // 23:59:59

    List<Property> properties = new ArrayList<>();

    Long parentId;

    @Value("${scim2.baseUrl}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        parentId = elementTypeRepository.findByNameIgnoreCase("User").map(ElementType::getId)
                .orElseThrow(() -> new RuntimeException("Element type not found"));
        properties.addAll(
                propertyRepository.findByParentTypeIdAndDeleted(parentId, 'N'));

    }

    public UserResource addUser(UserResource user)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            URISyntaxException, ScimException, PolicyEditorException {
        // setting leaf elements does the validation of scim attributes as well
        LeafElement le = new LeafElement();
        setLeafElementFields(user, le);
        Enrollment enrollment = createEnrollment();
        Long elementId = createElement(user, enrollment);
        le.setElementId(elementId);
        le = leafElementRepository.save(le);
        user.setId(le.getElementId().toString());
        user.setMeta(createMetaData(user.getId()));
        
        updateEntry(user.getId());

        updateDictionary();
        return user;

    }

    /**
     * Creates an entry in the dict_enrollment table or returns if SCIM domain name already exists.
     *
     * @return the id of the entity created.
     */
    private Enrollment createEnrollment() {
        Enrollment enrollment = enrollmentRepository.findByDomainNameIgnoreCase(EnrollmentConstants.SCIM_DOMAIN)
                .orElse(null);
        if (enrollment == null) {
            enrollment = new Enrollment();
            enrollment.setDomainName(EnrollmentConstants.SCIM_DOMAIN);
            enrollment.setEnrollmentType(EnrollmentType.SCIM);
            enrollment.setActive(true);
            enrollmentRepository.save(enrollment);
        }
        return enrollment;
    }

    /**
     * Creates an entry in the dict_elements table
     * 
     * @param user user
     * @param enrollment enrollment
     * @throws ResourceConflictException
     */
    private Long createElement(UserResource user, Enrollment enrollment) {
        DictionaryPath path = new DictionaryPath(
                new String[] {enrollment.getDomainName(), user.getPrincipalName()});
        String key = Base64.getMimeEncoder().encodeToString(user.getPrincipalName().getBytes());


        Element e = new Element(enrollment, key, user.getPrincipalName(),
                user.getDisplayName(), path.toFilterString(false), path.hashCode(), 'Y',
                System.currentTimeMillis(), DATETIME_MAX_TICKS);
        
        e = elementRepository.save(e);
        e.setOriginalId(e.getId());
        e = elementRepository.save(e);
        return e.getId();
    }

    private String getMapping(String fieldName) {
        String mapping = "";
        // To get the column mapping
        List<TypeField> typeFields = typeFieldRepository
                .findByParentTypeIdAndDeletedAndNameIgnoreCase(parentId, 'N', fieldName);

        if (!typeFields.isEmpty()) {
            mapping = typeFields.get(0).getMapping();
        }

        return mapping;

    }

    private Meta createMetaData(String userId) throws URISyntaxException {
        Meta meta = new Meta();
        meta.setCreated(new GregorianCalendar());
        meta.setLastModified(new GregorianCalendar());
        meta.setLocation(new URI(baseUrl + "/Users/" + userId));
        meta.setResourceType("User");
        meta.setVersion("1.0");

        return meta;

    }

    public UserResource getUser(Long id)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException {
        return getUserFromLeafElement(getLeafElement(id));
    }

    public UserResource getUserFromLeafElement(LeafElement le) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, URISyntaxException {
        UserResource user = new UserResource();
        user.setId(le.getElementId().toString());

        String mapping = "";
        String fName = "";

        Method m;
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field f : fields) {
            fName = f.getName();
            mapping = getMapping(fName);
            fName = fName.substring(0, 1).toUpperCase() + fName.substring(1);

            if (!mapping.isEmpty()) {
                m = UserResource.class.getDeclaredMethod("set" + fName, String.class);
                Object o = PropertyUtils.getProperty(le, mapping.toLowerCase());
                if (o != null) {
                    m.invoke(user, o.toString());
                }
            }
        }
        user.setMeta(createMetaData(user.getId()));
        return user;
    }

    public ListResponse<UserResource> getUsers()
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, URISyntaxException, ScimException {
        List<UserResource> userList = new ArrayList<>();
        List<Long> leafElementIds = fetchScimLeafElementsIds();

        for(Long id : leafElementIds) {
            userList.add(getUser(id));
        }

        return new ListResponse<>(userList.size(), userList, 1, userList.size());
    }
    
    /**
     * 1. Get Scim enrollment 2. Get all elements with enrollment id in above.
     */
    public List<Long> fetchScimLeafElementsIds() {
        List<Element> elements =
                elementRepository.findByEnrollmentIdAndActiveToGreaterThan(getScimEnrollment(),
                        System.currentTimeMillis());
        
        return elements.stream().map(Element::getId).collect(Collectors.toList());
    }

    public void deleteUser(Long userId) throws ScimException, PolicyEditorException {
        // see if leaf element exists. if not throw error.
        Element element = elementRepository
                .findByIdAndEnrollmentIdAndActiveToGreaterThan(userId, getScimEnrollment(),
                        System.currentTimeMillis())
                .orElse(null);
        LeafElement e = leafElementRepository.findById(userId).orElse(null);
        if (element == null || e == null) {
            throw ScimException.createException(HttpStatus.NOT_FOUND.value(), String.format("User %s not found", userId));
        } else {
            element.setActiveTo(System.currentTimeMillis());
            elementRepository.save(element);

            // if all the scim enrolled resources are deleted:
            // 1. expire the update
            // 2. set the enrollment to not active
            if (fetchScimLeafElementsIds().isEmpty()) {
                Enrollment enrollment = element.getEnrollment();
                if (enrollment != null) {
                    expireUpdateEntry(enrollment);
                    enrollment.setActive(false);
                    enrollmentRepository.save(enrollment);
                }
            }
        }
        updateDictionary();
    }

    public UserResource updateUser(UserResource userResource) throws ScimException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException, PolicyEditorException {
        Long id = Long.parseLong(userResource.getId());
        LeafElement le = getLeafElement(id);
        setLeafElementFields(userResource, le);
        le = leafElementRepository.save(le);

        Element element = elementRepository.findById(id).orElse(null);
        if (element != null) {
            element.setDisplayName(userResource.getDisplayName());
            elementRepository.save(element);
        }
        userResource.setId(le.getElementId().toString());
        userResource.setMeta(createMetaData(userResource.getId()));
        updateEntry(userResource.getId());
        updateDictionary();
        return userResource;
    }

    private void setLeafElementFields(UserResource userResource, LeafElement le) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ScimException, URISyntaxException {
        UserResource currentUserResource = null;
        boolean isUpdate = StringUtils.isNotBlank(userResource.getId());
        if (isUpdate){
            currentUserResource = getUser(Long.parseLong(userResource.getId()));
        }
        String mapping;
        Method m;
        Field[] fields = userResource.getClass().getDeclaredFields();
        for (Field scimAttributeField : fields) {
            mapping = getMapping(scimAttributeField.getName());
            if (!mapping.isEmpty()) {
                Object newValue = PropertyUtils.getProperty(userResource, scimAttributeField.getName());
                Object currentValue = currentUserResource == null ?
                        null : PropertyUtils.getProperty(currentUserResource, scimAttributeField.getName());
                validateScimAttribute(scimAttributeField, newValue, currentValue, isUpdate);
                m = LeafElement.class.getDeclaredMethod("set" + mapping, String.class);
                if (newValue != null) {
                    m.invoke(le, newValue.toString());
                } else {
                    m.invoke(le, new Object[]{ null });
                }

            }

        }
        le.setTypeId(parentId);
    }

    private void validateScimAttribute(Field scimAttributeField, Object newValue, Object currentValue,
                                       boolean isUpdate) throws BadRequestException,
            ResourceConflictException {
        Attribute attrDef = scimAttributeField.getAnnotation(Attribute.class);
        if (attrDef.isRequired() && (newValue == null || StringUtils.isEmpty(newValue.toString()))){
            throw BadRequestException.invalidSyntax(String.format("%s is a required attribute",
                    scimAttributeField.getName()));
        } else if (attrDef.mutability() == AttributeDefinition.Mutability.IMMUTABLE
                && currentValue != null
                && !StringUtils.equals(currentValue.toString(), newValue.toString())) {
            throw BadRequestException.mutability(String.format("%s is immutable", scimAttributeField.getName()));
        } else if (attrDef.uniqueness() != AttributeDefinition.Uniqueness.NONE){
            List<LeafElement> leafElements = null;
            if (newValue != null) {
                leafElements = leafElementDao.filterActive(getMapping(scimAttributeField.getName()).toLowerCase(), newValue.toString());
            }
            if (leafElements != null && !leafElements.isEmpty()) {
                if (isUpdate && currentValue != null && !StringUtils.equals(currentValue.toString(), newValue.toString())) {
                    throw new ResourceConflictException(String.format("%s is already present", scimAttributeField.getName()),
                            "uniqueness", null);
                } else if (!isUpdate) {
                    throw new ResourceConflictException(String.format("%s is already present", scimAttributeField.getName()),
                            "uniqueness", null);
                }
            }
        }
    }

    public UserResource updateUser(Long userId, PatchRequest req) throws JsonProcessingException,
            ScimException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, URISyntaxException, NoSuchFieldException, PolicyEditorException {

        LeafElement le = getLeafElement(userId);
        
        performPatchOperations(le, req.getOperations());
        
        leafElementRepository.save(le);

        UserResource user = getUser(userId);

        updateEntry(user.getId());

        Element element = elementRepository.findById(userId).orElse(null);
        if (element != null) {
            element.setDisplayName(user.getDisplayName());
            elementRepository.save(element);
        }

        updateDictionary();
        return user;
    }

    /**
     * Performs the given patch operations on the LeafElement object
     * 
     * @throws BadRequestException if the path is invalid.
     */
    public void performPatchOperations(LeafElement le, List<PatchOperation> ops)
            throws JsonProcessingException, ScimException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            NoSuchFieldException, URISyntaxException {
        for (PatchOperation o : ops) {
            Path path = o.getPath();
            String value = o.getValue(String.class);

            if (!isValidPath(path)) {
                throw BadRequestException
                        .invalidPath(String.format("%s is an invalid path", path.toString()));
            }

            updateLeafElement(le, path.toString(), value);
        }
    }

    /**
     * Checks if the given path is valid.
     * 
     * @return true if the path is valid; false if the path is null, empty or is a non-existent
     * property
     */
    public boolean isValidPath(Path path) {
        if (path == null || path.toString().isEmpty())
            return false;

        for (Property p : properties) {
            if (path.toString().equalsIgnoreCase(p.getName())) {
                return true;
            }
        }
        return false;
    }



    /**
     * Updates the given attribute of the LeafElement object with the given newValue;
     */
    public void updateLeafElement(LeafElement le, String attribute, String newValue)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, ScimException, NoSuchFieldException, URISyntaxException {

        String mapping = "";
        Method m;

        UserResource user = getUserFromLeafElement(le);
        Field scimAttributeField = UserResource.class.getDeclaredField(attribute);
        Object currentValue = PropertyUtils.getProperty(user, scimAttributeField.getName());
        validateScimAttribute(scimAttributeField, newValue, currentValue, true);

        attribute = attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
        mapping = getMapping(attribute);

        if (!mapping.isEmpty()) {
            m = LeafElement.class.getDeclaredMethod("set" + mapping, String.class);
            // value of patch operation cannot be null
            m.invoke(le, newValue);
        }
    }

    /**
     * Checks if DICT_ELEMENT and DICT_LEAF_ELEMENT entry are still valid and not deleted.
     */
    public LeafElement getLeafElement(Long userId) throws ScimException {
        Optional<Element> elementOp =
                elementRepository.findByIdAndEnrollmentIdAndActiveToGreaterThan(userId,
                        getScimEnrollment(),
                        System.currentTimeMillis());
        Optional<LeafElement> leafElementOptional = leafElementRepository.findById(userId);
        if (!elementOp.isPresent() || !leafElementOptional.isPresent()) {
            throw ScimException.createException(HttpStatus.NOT_FOUND.value(),
                    String.format("User %s not found", userId));
        }

        return leafElementOptional.get();

    }

    public List<BulkResponseOperation> bulkRequest(BulkRequest br)
            throws PolicyEditorException {
        List<BulkOperation> bulkOps = br.getOperations();
        List<BulkResponseOperation> bulkResponseOps = new ArrayList<>();
        
        long failOnErrors = br.getFailErrorsNum();
        long errorCount = 0;
        UserResource u;
        for(BulkOperation o : bulkOps) {
            if (failOnErrors > 0 && errorCount >= failOnErrors) {
                return bulkResponseOps;

            } else {

                try {
                    switch (o.getMethod()) {
                        case DELETE:
                            deleteUser(Long.parseLong(o.getPath()));
                            bulkResponseOps.add(
                                    new BulkResponseOperation(o.getPath(),
                                            BulkMethodType.DELETE, o.getBulkId(), HttpStatus.NO_CONTENT.value(), null));
                            break;
                        case PATCH:
                            u = updateUser(Long.parseLong(o.getPath()),
                                    o.getData(PatchRequest.class));
                            bulkResponseOps.add(
                                    new BulkResponseOperation(u.getMeta().getLocation().toString(),
                                            BulkMethodType.PATCH, o.getBulkId(), HttpStatus.OK.value(), null));
                            break;
                        case POST:
                            u = addUser(o.getData(UserResource.class));
                            bulkResponseOps.add(
                                    new BulkResponseOperation(u.getMeta().getLocation().toString(),
                                            BulkMethodType.POST, o.getBulkId(), HttpStatus.CREATED.value(), null));
                            break;
                        case PUT:
                            u = updateUser(o.getData(UserResource.class));
                            bulkResponseOps.add(
                                    new BulkResponseOperation(u.getMeta().getLocation().toString(),
                                            BulkMethodType.PUT, o.getBulkId(), HttpStatus.OK.value(), null));
                            break;
                        default:
                            break;

                    }
                } catch (ScimException e) {
                    errorCount++;
                    bulkResponseOps.add(new BulkResponseOperation(o.getPath(),
                            o.getMethod(), o.getBulkId(), e.getScimError().getStatus(), e.getScimError()));
                } catch (JsonProcessingException | NoSuchMethodException | IllegalAccessException
                        | InvocationTargetException | URISyntaxException
                        | NoSuchFieldException e) {
                    errorCount++;
                    ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST.value());
                    er.setDetail(e.getMessage());
                    bulkResponseOps.add(new BulkResponseOperation(o.getPath(),
                            o.getMethod(), o.getBulkId(), HttpStatus.BAD_REQUEST.value(), er));
                }
            }
        }
        return bulkResponseOps;
    }


    private void updateEntry(String userId) {
        Element e = elementRepository.findById(Long.parseLong(userId)).orElse(null);
        if (e != null) {
            Enrollment enrollment = e.getEnrollment();
            expireUpdateEntry(enrollment);
            createUpdateEntry(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    public void createUpdateEntry(Enrollment enrollment) {
        enrollment.setActive(true);
        enrollment.getUpdates().add(new Updates(enrollment, System.currentTimeMillis(),
                System.currentTimeMillis(),
                'Y', "The SCIM operation is successful", System.currentTimeMillis(),
                DATETIME_MAX_TICKS));
    }

    private void expireUpdateEntry(Enrollment enrollment) {
        long currentTime = System.currentTimeMillis();
        enrollment.getUpdates().forEach(updates -> {
            if(updates.getActiveTo() > currentTime) {
                updates.setActiveTo(currentTime);
            }
        });
    }

    public void updateDictionary() throws PolicyEditorException {
        dpsService.getPolicyEditorClient().updateDictionaryConsistentTime();
    }

    public Long getScimEnrollment() {
        List<Enrollment> enrollments =
                enrollmentRepository.findByEnrollmentType(EnrollmentType.SCIM);
        return (enrollments == null || enrollments.isEmpty()) ? -1L : enrollments.get(0).getId();
    }
}
