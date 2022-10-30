package com.nextlabs.destiny.console.services.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.dictionary.ElementDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.enums.EnrollmentElementType;
import com.nextlabs.destiny.console.model.dictionary.ElementType;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.model.dictionary.LeafElement;
import com.nextlabs.destiny.console.model.dictionary.TypeField;
import com.nextlabs.destiny.console.repositories.dictionary.ElementRepository;
import com.nextlabs.destiny.console.repositories.dictionary.ElementTypeRepository;
import com.nextlabs.destiny.console.repositories.dictionary.EnrollmentRepository;
import com.nextlabs.destiny.console.repositories.dictionary.TypeFieldsRepository;
import com.nextlabs.destiny.console.services.EnrolledDataService;

/**
 * Enrolled Data Service Implementation
 *
 * @author Sachindra Dasun
 */
@Service
public class EnrolledDataServiceImpl implements EnrolledDataService {

    private static final Logger logger = LoggerFactory.getLogger(EnrolledDataServiceImpl.class);
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private ElementTypeRepository elementTypeRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private TypeFieldsRepository typeFieldsRepository;

    @Override
    public SearchFieldsDTO searchFields() {
        SearchFieldsDTO searchFields = new SearchFieldsDTO();

        searchFields.setType(SinglevalueFieldDTO.create("type", "Type"));
        for (ElementType elementType : elementTypeRepository.findAll()) {
            if(EnrollmentElementType.CLIENT_INFO.name().equals(elementType.getName())
                    || EnrollmentElementType.SITE.name().equals(elementType.getName())) {
                continue;
            }
            searchFields.getTypeOptions().add(MultiFieldValuesDTO.create(String.valueOf(elementType.getId()), elementType.getName()));
        }
        searchFields.getTypeOptions().add(MultiFieldValuesDTO.create("-1", EnrollmentElementType.GROUP.name()));

        searchFields.setEnrollment(SinglevalueFieldDTO.create("enrollment", "Enrollment"));
        for (Enrollment enrollment : enrollmentRepository.findByIsActiveIsTrue(Sort.by("domainName"))) {
            searchFields.getEnrollmentOptions()
                    .add(MultiFieldValuesDTO.create(String.valueOf(enrollment.getId()), enrollment.getDomainName()));
        }

        return searchFields;
    }

    @Override
    public Page<ElementDTO> search(SearchCriteria searchCriteria) {
        logger.debug("Search Criteria :[{}]", searchCriteria);
        Map<Long, String> allTypes = elementTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ElementType::getId, ElementType::getName));
        List<Long> types = null;
        List<Long> enrollments = null;
        String text = "";
        Long groupId = null;
        if (!searchCriteria.getFields().isEmpty()) {
            for (SearchField f : searchCriteria.getFields()) {
                StringFieldValue stringFieldValue = (StringFieldValue) f.getValue();
                switch (f.getField()) {
                    case "type": {
                        types = ((List<String>) stringFieldValue.getValue()).stream()
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                        break;
                    }
                    case "enrollment": {
                        enrollments = ((List<String>) stringFieldValue.getValue()).stream()
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                        break;
                    }
                    case "title": {
                        text = (String) stringFieldValue.getValue();
                        break;
                    }
                    case "group": {
                        String group = (String) stringFieldValue.getValue();
                        if (group != null) {
                            groupId = Long.valueOf(group);
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unexpected value: " + f.getField());
                    }
                }
            }
        }
        return elementRepository.findByCriteria(enrollments, types, text, groupId,
                PageRequest.of(searchCriteria.getPageNo(), searchCriteria.getPageSize(), Sort.by("id")))
                .map(element -> new ElementDTO(element, allTypes, null));
    }

    @Override
    public ElementDTO findById(Long id) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        com.nextlabs.destiny.console.model.dictionary.Element element = elementRepository.findById(id).orElse(null);
        if (element == null) {
            return null;
        }
        Map<Long, String> allTypes = elementTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ElementType::getId, ElementType::getName));

        Map<String, String> attributes = null;
        if (element.getLeafElement() != null) {
            Map<String, String> fieldMappings = getFieldMapping().get(element.getLeafElement().getTypeId());
            attributes = getAttributes(element.getLeafElement(), fieldMappings);
        }
        return new ElementDTO(element, allTypes, attributes);
    }

    private Map<Long, Map<String, String>> getFieldMapping() {
        List<TypeField> typeFields = typeFieldsRepository.findByDeletedIs('N');
        Map<Long, Map<String, String>> filedMapping = new HashMap<>();
        for (TypeField typeField : typeFields) {
            filedMapping.computeIfAbsent(typeField.getParentTypeId(), (key -> new HashMap<>()))
                    .put(typeField.getMapping().toLowerCase(), typeField.getLabel());
        }
        return filedMapping;
    }

    private Map<String, String> getAttributes(LeafElement leafElement, Map<String, String> fieldMappings)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, String> attributes = new TreeMap<>();
        for (int i = 0; i <= 49; i++) {
            String fieldName = String.format("string%02d", i);
            String value = BeanUtils.getProperty(leafElement, fieldName);
            if (value != null) {
                attributes.put(fieldMappings.get(fieldName), value);
            }
        }
        for (int i = 0; i <= 19; i++) {
            String fieldName = String.format("number%02d", i);
            String value = BeanUtils.getProperty(leafElement, fieldName);
            if (value != null) {
                attributes.put(fieldMappings.get(fieldName), value);
            }
        }
        for (int i = 0; i <= 19; i++) {
            String fieldName = String.format("date%02d", i);
            String value = BeanUtils.getProperty(leafElement, fieldName);
            if (value != null) {
                attributes.put(fieldMappings.get(fieldName.toLowerCase()), value);
            }
        }
        for (int i = 0; i <= 3; i++) {
            String fieldName = String.format("numArray%02d", i);
            String[] value = BeanUtils.getArrayProperty(leafElement, fieldName);
            if (value != null) {
                attributes.put(fieldMappings.get(fieldName), Arrays.toString(value));
            }
        }
        return attributes;
    }

}
