/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 7, 2015
 *
 */
package com.nextlabs.destiny.console.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.common.TagSearchDTO;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.services.TagLabelService;

import io.swagger.annotations.ApiOperation;

/**
 * REST Controller for Tag Labels
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@RestController
@ApiVersion(1)
@RequestMapping("/config/tags")
public class TagLabelController extends AbstractRestController {

    private static final Logger log = LoggerFactory
            .getLogger(TagLabelController.class);

    @Autowired
    private TagLabelService tagLabelService;

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/add/{tagType}")
    @ApiOperation(value="Creates a new Tag of the given tag type", 
    	notes="Returns a success message along with the new tag's Id, when the tag has been successfully saved.")
    public ConsoleResponseEntity<SimpleResponseDTO> addTag(
            @RequestBody TagDTO tagDTO, @PathVariable("tagType") String tagType) throws ConsoleException {

        log.debug("Request came to add new tag");
        validations.assertNotBlank(tagDTO.getKey(), "key");
        validations.assertNotBlank(tagDTO.getLabel(), "label");
        validations.assertNotBlank(tagDTO.getType(), "type");
        validations.assertMatches(tagType, tagDTO.getType());

        TagLabel tagLabel = new TagLabel(null, tagDTO.getKey().toLowerCase(),
                tagDTO.getLabel(), TagType.getType(tagDTO.getType()),
                Status.get(tagDTO.getStatus()));
        tagLabelService.saveTag(tagLabel);

        SimpleResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), tagLabel.getId());

        log.info("New tag saved successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/modify")
    public ConsoleResponseEntity<ResponseDTO> modifyTag(
            @RequestBody TagDTO tagDTO) throws ConsoleException {

        log.debug("Request came to modify the tag");
        validations.assertNotZero(tagDTO.getId(), "id");
        validations.assertNotBlank(tagDTO.getKey(), "key");
        validations.assertNotBlank(tagDTO.getLabel(), "label");
        validations.assertNotBlank(tagDTO.getType(), "type");

        TagLabel tagLabel = tagLabelService.findById(tagDTO.getId());
        tagLabel.setKey(tagDTO.getKey().toLowerCase());
        tagLabel.setLabel(tagDTO.getLabel());
        tagLabel.setType(TagType.getType(tagDTO.getType()));
        tagLabel.setStatus(Status.get(tagDTO.getStatus()));

        tagLabelService.saveTag(tagLabel);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.modified.code"),
                msgBundle.getText("success.data.modified"));

        log.info("Tag modifed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/remove/{id}")
    public ConsoleResponseEntity<ResponseDTO> removeTag(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came to remove a tag");
        validations.assertNotZero(id, "id");

        tagLabelService.removeTag(id);

        ResponseDTO response = ResponseDTO.create(
                msgBundle.getText("success.data.deleted.code"),
                msgBundle.getText("success.data.deleted"));

        log.info("Tag removed successfully and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public ConsoleResponseEntity<ResponseDTO> getById(
            @PathVariable("id") Long id) throws ConsoleException {

        log.debug("Request came find tag by id, [id: {}]", id);
        validations.assertNotZero(id, "id");

        TagLabel tagLabel = tagLabelService.findById(id);
        if (tagLabel == null) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        TagDTO tagDTO = TagDTO.getDTO(tagLabel);

        ResponseDTO response = SimpleResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"), tagDTO);

        log.info("Requested tag details found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/list/{type}")
    @ApiOperation(value = "Returns list of tags based on tag type.", 
		notes = "Given a tag type, this API returns a list of tags.")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByType(
            @PathVariable("type") String tagType,
            @RequestParam(value = "showHidden", defaultValue = "false", required = false) boolean showHidden,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
            throws ConsoleException {

        log.debug("Request came to find tags by type, [ Type:{} ]", tagType);

        CollectionDataResponseDTO response = findByTypeWithHidden(tagType,
                showHidden, pageNo, pageSize);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private CollectionDataResponseDTO findByTypeWithHidden(String tagType,
            boolean showHidden, int pageNo, int pageSize)
            throws ConsoleException {
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<TagLabel> tagLabelsPage = tagLabelService.findByType(tagType,
                showHidden, pageable);
        if (tagLabelsPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<TagDTO> tagDTOs = new ArrayList<>(
                tagLabelsPage.getNumberOfElements());
        for (TagLabel tagLabel : tagLabelsPage.getContent()) {
            TagDTO tagDTO = TagDTO.getDTO(tagLabel);
            tagDTOs.add(tagDTO);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(tagDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(tagLabelsPage.getTotalPages());
        response.setTotalNoOfRecords(tagLabelsPage.getTotalElements());

        log.info(
                "Requested tag details found and response sent, [No of records :{}]",
                tagDTOs.size());
        return response;
    }

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/list")
    public ConsoleResponseEntity<CollectionDataResponseDTO> findByTypeName(
            @RequestBody TagSearchDTO tagSearchDTO)
            throws ConsoleException {
        validations.assertNotNull(tagSearchDTO.getTag(), "tag");
        validations.assertNotNull(tagSearchDTO.getTag().getType(), "tag->type");
        
        log.debug(
                "Request came to find tags by type and name, [Type:{}, name:{}]",
                tagSearchDTO.getTag().getType(), tagSearchDTO.getTag().getLabel());

        CollectionDataResponseDTO response = null;
        if(!StringUtils.isEmpty(tagSearchDTO.getTag().getLabel())) {
            response = findByName(tagSearchDTO.getTag().getType(), tagSearchDTO.getTag().getLabel(), tagSearchDTO.getPageNo(),
                tagSearchDTO.getPageSize(), tagSearchDTO.isShowHidden());
        } else {
            response = findByTypeWithHidden(tagSearchDTO.getTag().getType(),
                    tagSearchDTO.isShowHidden(),  tagSearchDTO.getPageNo(), tagSearchDTO.getPageSize());
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private CollectionDataResponseDTO findByName(String tagType, String name,
            int pageNo, int pageSize, boolean showHidden)
            throws ConsoleException {

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<TagLabel> tagLabelsPage = tagLabelService
                .findByLabelStartWithAndType(name, tagType, showHidden,
                        pageable);

        if (tagLabelsPage.getNumberOfElements() == 0) {
            throw new NoDataFoundException(
                    msgBundle.getText("no.data.found.code"),
                    msgBundle.getText("no.data.found"));
        }

        List<TagDTO> tagDTOs = new ArrayList<>(
                tagLabelsPage.getNumberOfElements());
        for (TagLabel tagLabel : tagLabelsPage.getContent()) {
            TagDTO tagDTO = TagDTO.getDTO(tagLabel);
            tagDTOs.add(tagDTO);
        }

        CollectionDataResponseDTO response = CollectionDataResponseDTO.create(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(tagDTOs);
        response.setPageSize(pageable.getPageSize());
        response.setPageNo(pageable.getPageNumber());
        response.setTotalPages(tagLabelsPage.getTotalPages());
        response.setTotalNoOfRecords(tagLabelsPage.getTotalElements());

        log.info(
                "Requested tag details found and response sent, [No of records :{}]",
                tagDTOs.size());
        return response;
    }

    @Override
    public Logger getLog() {
        return log;
    }

}
