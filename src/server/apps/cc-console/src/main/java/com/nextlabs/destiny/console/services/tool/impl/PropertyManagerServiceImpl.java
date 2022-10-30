/*
 * Copyright 2019 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Dec 13, 2019
 *
 */
package com.nextlabs.destiny.console.services.tool.impl;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.mchange.util.DuplicateElementException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.dto.tool.PropertyDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.ElementFieldType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidOperationException;
import com.nextlabs.destiny.console.model.dictionary.ElementType;
import com.nextlabs.destiny.console.model.dictionary.Property;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.visitors.Attribute;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.repositories.PolicyDevelopmentEntityRepository;
import com.nextlabs.destiny.console.repositories.dictionary.ElementTypeRepository;
import com.nextlabs.destiny.console.repositories.dictionary.PropertyRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;

@Service
public class PropertyManagerServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(PropertyManagerServiceImpl.class);

    private static final List<String> TYPES;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PolicyDevelopmentEntityRepository devEntityRepository;

    @Autowired
    private ElementTypeRepository elementTypeRepo;

    @Autowired
    protected MessageBundleService msgBundle;

    static {
        TYPES = List.of(
                com.nextlabs.destiny.console.enums.ElementType.APPLICATION.name().toLowerCase(),
                com.nextlabs.destiny.console.enums.ElementType.HOST.name().toLowerCase(),
                com.nextlabs.destiny.console.enums.ElementType.USER.name().toLowerCase());
    }

    public List<PropertyDTO> findAll() throws ConsoleException {
        return propertyRepository.findByDeleted('N', Sort.by("id")).stream()
                .map(property -> new PropertyDTO(property,
                        elementTypeRepo.findById(property.getParentTypeId())
                                .map(elementType -> elementType.getName().toLowerCase())
                                .orElseThrow(NoSuchElementException::new)))
                .collect(Collectors.toList());
    }
    
    public SearchFieldsDTO searchFields() throws ConsoleException {
        SearchFieldsDTO searchFields = new SearchFieldsDTO();
        searchFields.setType(SinglevalueFieldDTO.create("entityType",
                msgBundle.getText("policy.mgmt.search.fields.modelTypes")));
        TYPES.stream().map(StringUtils::capitalize)
                .forEach(type -> searchFields.getTypeOptions().add(MultiFieldValuesDTO.create(type, type)));
        searchFields.setStatus(SinglevalueFieldDTO.create("dataType",
                msgBundle.getText("policy.mgmt.search.fields.modelTypes")));
        for (ElementFieldType type : ElementFieldType.values()) {
            searchFields.getStatusOptions()
                    .add(MultiFieldValuesDTO.create(type.getAttributeType(), type.getLabel()));
        }
        searchFields.setSort(SinglevalueFieldDTO.create("sortBy", "Sort By"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("parentTypeId", "Entity Type", "ASC"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("label", "Display Name (A to Z)", "ASC"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("label", "Display Name (Z to A)", "DESC"));
        return searchFields;
    }

    public Long save(PropertyDTO propertyDTO) throws PQLException {
        long parentTypeId = elementTypeRepo.findByNameIgnoreCase(propertyDTO.getParentName())
                .map(ElementType::getId)
                .orElseThrow(NoSuchElementException::new);
        propertyDTO.setParentTypeId(parentTypeId);
        if (isDuplicate(propertyDTO.getId(), propertyDTO.getName(),
                propertyDTO.getParentTypeId())) {
            throw new DuplicateElementException(String.format("A property with logical name \"%s\" is already present " +
                            "in the list (including deleted properties) for entity type \"%s\".",
                    propertyDTO.getName(), propertyDTO.getParentName()));
        }
        if (propertyDTO.getId() == null) {
            propertyDTO.setMapping(calculateMapping(propertyDTO));
            propertyDTO.setDeleted('N');
        } else {
            Property oldProperty = propertyRepository.findById(propertyDTO.getId())
                    .orElseThrow(() -> new NoSuchElementException(String.format("Property with id %d was not found",
                            propertyDTO.getId())));
            if (isPropertyReferenced(oldProperty.getName())) {
                throw new InvalidOperationException(
                        msgBundle.getText("property.update.failed.code"),
                        msgBundle.getText("property.update.failed"));
            }
            propertyDTO.setVersion(oldProperty.getVersion());
            propertyDTO.setMapping(oldProperty.getMapping());
            propertyDTO.setDeleted(oldProperty.getDeleted());
        }
        Property property = propertyRepository.save(new Property(propertyDTO));
        return property.getId();
    }

    /**
     * Deletes a Property. Also checks if the Property is being referenced in policies or
     * components.
     * 
     * @param propertyDTO Property to be deleted
     * @return true if the Property is deleted, false otherwise.
     * @throws ConsoleException if an error occurred
     */
    public boolean delete(PropertyDTO propertyDTO) throws PQLException, ConsoleException {
        boolean referenced = isPropertyReferenced(propertyDTO.getName());
        if (!referenced) {
            Property property = propertyRepository.findById(propertyDTO.getId())
                    .orElseThrow(() -> new NoSuchElementException(String.format("Property with id %d was not found",
                            propertyDTO.getId())));
            property.setDeleted('Y');
            propertyRepository.save(property);
            return true;
        }
        return false;
    }

    /**
     * Bulk delete of Properties
     * 
     * @param propertyList the list of the Properties to be deleted.
     * @return the number of Properties deleted. There might be some Properties not deleted due to
     * them being referenced in policies and components.
     * @throws ConsoleException if an error occurred
     */
    public int bulkDelete(List<PropertyDTO> propertyList) throws PQLException, ConsoleException {
        int deletedNum = 0;
        for (PropertyDTO property : propertyList) {
            if (delete(property)) {
                deletedNum++;
            }
        }
        return deletedNum;
    }

    public boolean isPropertyReferenced(String name)
            throws PQLException {
        List<PolicyDevelopmentEntity> componentList = devEntityRepository.findByTypeAndStatusInAndHidden(
                DevEntityType.COMPONENT.getKey(),
                List.of(PolicyDevelopmentStatus.APPROVED.getKey(), PolicyDevelopmentStatus.DRAFT.getKey()), 'N');
        for (PolicyDevelopmentEntity de : componentList) {
            String pql = de.getPql();
            if (pql.toLowerCase().contains(name.toLowerCase())) {
                DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
                IDSpec spec = domBuilder.processSpec();
                PredicateData predicateData = ComponentPQLHelper.create().getPredicates(spec, pql,
                        PolicyModelType.SUBJECT.name());
                List<Attribute> attributes = predicateData.getAttributes();
                for (Attribute attr : attributes) {
                    if (attr.getLhs().equalsIgnoreCase(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Page<PropertyDTO> search(@NotNull SearchCriteria criteria) {
        logger.debug("Search Criteria :[{}]", criteria);
        List<String> dataTypes = null;
        List<Long> entityTypeIds = null;
        String text = "";
        for (SearchField f : criteria.getFields()) {
            StringFieldValue stringFieldValue = (StringFieldValue) f.getValue();
            switch (f.getField()) {
                case "type":
                    entityTypeIds = elementTypeRepo.findByNameInIgnoreCase((List<String>) stringFieldValue.getValue())
                            .stream()
                            .map(ElementType::getId)
                            .collect(Collectors.toList());
                    break;
                case "status":
                    dataTypes = (List<String>) stringFieldValue.getValue();
                    break;
                case "title":
                    text = (String) stringFieldValue.getValue();
                    break;
            }
        }
        if (entityTypeIds == null) {
            entityTypeIds = elementTypeRepo.findByNameInIgnoreCase(TYPES)
                    .stream()
                    .map(ElementType::getId)
                    .collect(Collectors.toList());
        }
        if (dataTypes == null) {
            dataTypes = Arrays.stream(ElementFieldType.values())
                    .map(ElementFieldType::getAttributeType)
                    .collect(Collectors.toList());
        }
        Map<Long, String> typeIdToNameMap = elementTypeRepo.findByNameInIgnoreCase(TYPES).stream()
                .collect(Collectors.toMap(ElementType::getId,
                        elementType -> StringUtils.capitalize(elementType.getName().toLowerCase())));
        return propertyRepository.findByParentTypeIdInAndTypeInAndLabelContainingIgnoreCaseAndDeleted(
                entityTypeIds, dataTypes, text, 'N',
                PageRequest.of(criteria.getPageNo(), criteria.getPageSize(), getSort(criteria.getSortFields())))
                .map(property -> new PropertyDTO(property, typeIdToNameMap.get(property.getParentTypeId())));
    }

    public Sort getSort(List<SortField> sortList) {
        SortField sField = sortList.get(0);
        Sort.Order order =
                new Sort.Order(sField.getOrder().equalsIgnoreCase("DESC") ? Sort.Direction.DESC
                        : Sort.Direction.ASC, sField.getField()).ignoreCase();
        return Sort.by(order);
    }

    public String calculateMapping(PropertyDTO propertyDTO) {
        String mappingBase = ElementFieldType.getMappingBaseFromLabel(propertyDTO.getType());
        Property maxProperty = propertyRepository
                .findTopByParentTypeIdAndMappingContainingIgnoreCaseOrderByMappingDesc(
                        propertyDTO.getParentTypeId(), mappingBase);
        int baseCount = 0;
        if (maxProperty != null) {
            String maxMapping = maxProperty.getMapping();
            baseCount = Integer.parseInt(maxMapping.substring(maxMapping.length() - 2)) + 1;
        }
        int maxTypesCount = 20;
        if(mappingBase != null) {
            switch (mappingBase.toLowerCase()) {
                case "string":
                    maxTypesCount = 50;
                    break;
                case "numarray":
                    maxTypesCount = 4;
                    break;
                default:
                    maxTypesCount = 20;
            }
        }
        if (baseCount > (maxTypesCount - 1)) {
            throw new InvalidOperationException(msgBundle.getText("property.max.exceeded.code"),
                    msgBundle.getText("property.max.exceeded", propertyDTO.getType() + ""));
        }
        return mappingBase + (baseCount / 10) + (baseCount % 10);
    }

    public boolean isDuplicate(Long id, String name, Long parentId) {
        List<Property> duplicateList =
                propertyRepository.findByNameIgnoreCaseAndParentTypeId(name, parentId);
        return !(duplicateList.isEmpty() || duplicateList.get(0).getId().equals(id));
    }

}
