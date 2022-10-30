package com.nextlabs.destiny.console.services;

import java.lang.reflect.InvocationTargetException;

import org.springframework.data.domain.Page;

import com.nextlabs.destiny.console.dto.dictionary.ElementDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;

/**
 * Enrolled data service.
 *
 * @author Sachindra Dasun
 */
public interface EnrolledDataService {

    SearchFieldsDTO searchFields();

    Page<ElementDTO> search(SearchCriteria searchCriteria);

    ElementDTO findById(Long id) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

}
