/*
 * Copyright 2019 by Nextlabs Inc.
 *
 * All rights reserved worldwide. Created on Dec 13, 2019
 *
 */
package com.nextlabs.destiny.console.services.tool.impl;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bluejungle.framework.utils.StringUtils;
import com.bluejungle.pf.destiny.formatter.DomainObjectFormatter;
import com.bluejungle.pf.destiny.lifecycle.DevelopmentStatus;
import com.bluejungle.pf.destiny.lifecycle.EntityType;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.bluejungle.pf.domain.destiny.subject.Location;
import com.mchange.util.DuplicateElementException;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.common.MultiFieldValuesDTO;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SinglevalueFieldDTO;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.SearchFieldsDTO;
import com.nextlabs.destiny.console.dto.tool.LocationDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.handlers.PolicyLifeCycleHandler;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.policy.visitors.Attribute;
import com.nextlabs.destiny.console.policy.visitors.PredicateData;
import com.nextlabs.destiny.console.repositories.PolicyDevelopmentEntityRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;

@Service
public class ImportLocationServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(ImportLocationServiceImpl.class);

    @Autowired
    private PolicyDevelopmentEntityRepository devEntityRepository;

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Autowired
    private PolicyLifeCycleHandler policyLifeCycleHandler;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Autowired
    protected MessageBundleService msgBundle;

    public static final String LOCATION_CONDITION_KEY = "location";

    public List<LocationDTO> findLocations() throws PQLException {
        List<PolicyDevelopmentEntity> entityList = devEntityRepository.findByTypeAndStatus(
                DevEntityType.LOCATION.getKey(), PolicyDevelopmentStatus.APPROVED.getKey());

        List<LocationDTO> locationList = new ArrayList<>();

        for (PolicyDevelopmentEntity entity : entityList) {
            LocationDTO loc = new LocationDTO(entity);
            locationList.add(loc);
        }
        return locationList;
    }

    public Long saveLocation(LocationDTO loc) throws ConsoleException, PQLException {
        LocationDTO locDTO = saveEntity(loc);
        deployEntity(locDTO);
        return locDTO.getParentId();
    }

    public void updateLocation(LocationDTO loc) throws ConsoleException, PQLException {
        deployEntity(saveEntity(loc));
    }

    /**
     * Deletes a location. Also checks if the location is being referenced in policies or
     * components.
     * 
     * @param loc location to be deleted
     * @return true if the location is deleted, false otherwise.
     */
    public boolean deleteLocation(LocationDTO loc) throws PQLException, ConsoleException {
        boolean referenced = isLocationReferenced(loc);
        if (!referenced) {
            unDeployEntity(loc);
            entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                    AuditableEntity.ENROLLMENT_TOOLS.getCode(), loc.getParentId(),
                    loc.toAuditString(), null);
            return true;
        }
        return false;
    }

    /**
     * Bulk delete of locations
     * 
     * @param locList the list of the locations to be deleted.
     * @return the number of locations deleted. There might be some locations not deleted due to
     * them being referenced in policies and components.
     */
    public int bulkDeleteLocation(List<LocationDTO> locList) throws PQLException, ConsoleException {
        int deletedNum = 0;
        for (LocationDTO loc : locList) {
            if (deleteLocation(loc)) {
                deletedNum++;
            }
        }
        return deletedNum;
    }

    // TODO Check if DomainObjectFormatter is useful
    public String getEmptyPQL(LocationDTO loc) {
        EntityType type = EntityType.LOCATION;
        return type.emptyPql(loc.getName());

        /*
         * return "id " + loc.getParentId() +
         * " STATUS APPROVED CREATOR \"0\" ACCESS_POLICY ACCESS_CONTROL PBAC FOR TRUE ON ADMIN BY user.name = resource.dso.owner DO ALLOW ALLOWED_ENTITIES "
         * + "HIDDEN LOCATION " + loc.getName() + " = " + loc.getValue();
         * 
         * 
         * return "id null status approved creator \"0\" " + "ACCESS_POLICY " + "ACCESS_CONTROL " +
         * "PBAC FOR * ON ADMIN BY PRINCIPAL.USER.NAME = RESOURCE.DSO.OWNER DO ALLOW " +
         * "ALLOWED_ENTITIES " + "HIDDEN location " + loc.getName() + " = " + loc.getValue();
         */

    }

    public String getAccessPolicy() {
        return "ACCESS_POLICY " + "ACCESS_CONTROL "
                + "PBAC FOR * ON ADMIN BY PRINCIPAL.USER.NAME = RESOURCE.DSO.OWNER DO ALLOW "
                + "ALLOWED_ENTITIES ";
    }

    public LocationDTO saveEntity(LocationDTO loc)
            throws ConsoleException, PQLException, IllegalArgumentException {

        LocationDTO oldLocDTO = null;
        PolicyDevelopmentEntity devEntity;

        // saving
        if (loc.getParentId() == null) {
            if (isDuplicate(loc.getName())) {
                throw new DuplicateElementException(
                        loc.getName() + " is already present in the list.");
            }
            devEntity = new PolicyDevelopmentEntity();
            devEntity.setTitle(loc.getName());
            devEntity.setType(DevEntityType.LOCATION.getKey());
            devEntity.setPql(getEmptyPQL(loc));
            devEntity.setApPql(getAccessPolicy());
            devEntity.setCreatedDate(System.currentTimeMillis());
            devEntity.setLastUpdatedDate(System.currentTimeMillis());
            devEntity.setOwner(getCurrentUser().getUserId());
            devEntity = devEntityMgmtService.save(devEntity);
        } else { // updating
            devEntity = devEntityRepository.findById(loc.getParentId()).get();
            oldLocDTO = new LocationDTO(devEntity);
        }

        LocationPQLHelper locHelper = new LocationPQLHelper();
        String pql = locHelper.getPQL(devEntity, loc);
        devEntity.setPql(pql);
        devEntity.setStatus(PolicyDevelopmentStatus.APPROVED.getKey());
        devEntity.setHidden(true);
        devEntity = devEntityMgmtService.save(devEntity);

        loc = new LocationDTO(devEntity);
        entityAuditLogDao.addEntityAuditLog(
                oldLocDTO == null ? AuditAction.CREATE : AuditAction.UPDATE,
                AuditableEntity.ENROLLMENT_TOOLS.getCode(), loc.getParentId(),
                oldLocDTO == null ? null : oldLocDTO.toAuditString(), loc.toAuditString());

        return loc;

    }

    public void deployEntity(LocationDTO loc) throws ConsoleException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(loc.getParentId());
        policyLifeCycleHandler.deployEntity(devEntity);
    }

    public void unDeployEntity(LocationDTO loc) throws ConsoleException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(loc.getParentId());
        policyLifeCycleHandler.unDeployEntity(devEntity, PolicyDevelopmentStatus.DELETED);
    }

    public boolean isLocationReferenced(LocationDTO loc) throws PQLException {
        /*
         * 1. Fetch all components 2. For each component, get the attributes. 3. For each attribute,
         * check the LHS for 'location' and RHS for the location's name.
         */
        List<PolicyDevelopmentEntity> componentList = getAllComponents();
        String pql;
        PredicateData predicateData;

        for (PolicyDevelopmentEntity de : componentList) {
            pql = de.getPql();
            if (pql.toLowerCase().contains(LOCATION_CONDITION_KEY.toLowerCase())) {
                DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);
                IDSpec spec = domBuilder.processSpec();
                predicateData = ComponentPQLHelper.create().getPredicates(spec, pql,
                        PolicyModelType.SUBJECT.name());

                List<Attribute> attributes = predicateData.getAttributes();
                for (Attribute attr : attributes) {
                    if ((attr.getLhs().equals(LOCATION_CONDITION_KEY))
                            && (StringUtils.isMatch(attr.getRhs(),
                                    loc.getName()))) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public List<PolicyDevelopmentEntity> getAllComponents() {
        List<String> allowedStatuses = new ArrayList();
        allowedStatuses.add(PolicyDevelopmentStatus.APPROVED.getKey());
        allowedStatuses.add(PolicyDevelopmentStatus.DRAFT.getKey());
        return devEntityRepository.findByTypeAndStatusInAndHidden(DevEntityType.COMPONENT.getKey(),
                allowedStatuses, 'N');
    }

    private class LocationPQLHelper {

        public String getPQL(PolicyDevelopmentEntity dev, LocationDTO loc) {
            DomainObjectFormatter formatter = new DomainObjectFormatter();
            Location location = new Location(loc.getName(), loc.getValue());
            formatter.formatLocation(getDescriptor(dev), location);
            return formatter.getPQL();

        }

        // TODO Access policy?

        public DomainObjectDescriptor getDescriptor(PolicyDevelopmentEntity dev) {
            if (dev == null) {
                return null;
            }
            DomainObjectDescriptor dod =
                    new DomainObjectDescriptor(dev.getId(), dev.getName(), dev.getOwner(), null,
                            EntityType.LOCATION, dev.getDescription(), DevelopmentStatus.APPROVED,
                            dev.getVersion(), new Date(dev.getLastUpdatedDate()),
                            new Date(dev.getCreatedDate()), new Date(dev.getLastModified()),
                            dev.getModifiedBy(), null, null, true, true, false);
            return dod;
        }
    }

    public Page<LocationDTO> search(@NotNull
    SearchCriteria criteria) throws PQLException {

        log.debug("Search Criteria :[{}]", criteria);

        String text = "";
        if (criteria.getFields().size() > 0) {
            SearchField field = criteria.getFields().get(0);
            StringFieldValue fieldValues = (StringFieldValue) field.getValue();
            text = (String) fieldValues.getValue();
        }
        List<PolicyDevelopmentEntity> entityList =
                devEntityRepository.findByTypeAndStatusAndTitleContainingIgnoreCase(
                        DevEntityType.LOCATION.getKey(),
                        PolicyDevelopmentStatus.APPROVED.getKey(), text,
                        getSort(criteria.getSortFields()));

        Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());

        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = ((pageable.getPageNumber() + 1) * pageable.getPageSize());
        int size = entityList.size();

        List<PolicyDevelopmentEntity> locSubList =
                entityList.subList(start, end > size ? size : end);

        Page<PolicyDevelopmentEntity> lList =
                new PageImpl<PolicyDevelopmentEntity>(locSubList, pageable, size);

        Page<LocationDTO> locPage =
                lList.map(new Function<PolicyDevelopmentEntity, LocationDTO>() {

                    @Override
                    public LocationDTO apply(PolicyDevelopmentEntity entity) {
                        try {
                            return new LocationDTO(entity);
                        } catch (PQLException e) {
                            return null;
                        }
                    }

                });

        return locPage;

    }

    public Sort getSort(List<SortField> sortList) {
        SortField sField = sortList.get(0);
        Sort.Order order =
                new Sort.Order(sField.getOrder().equalsIgnoreCase("DESC") ? Sort.Direction.DESC
                        : Sort.Direction.ASC, sField.getField()).ignoreCase();
        return Sort.by(order);
    }

    public SearchFieldsDTO searchFields() {
        SearchFieldsDTO searchFields = new SearchFieldsDTO();

        // sort by options
        searchFields.setSort(SinglevalueFieldDTO.create("sortBy", "Sort By"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("lastUpdatedDate", "Last Updated", "DESC"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("title", "Name (A to Z)", "ASC"));
        searchFields.getSortOptions()
                .add(MultiFieldValuesDTO.create("title", "Name (Z to A)", "DESC"));

        return searchFields;

    }

    public void validateImportFile(byte[] data)
            throws ParseException, IOException {
        validateFile(getFile(data));
    }

    public void validateFile(File file)
            throws ParseException, FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] split = line.split("\\s+");
            if (split.length == 2) {
                // removing surronding quotes
                split[1] = split[1].replaceAll("^\"", "").replaceAll("\"$", "");
                if (!isValidIP(split[1])) {
                    log.debug(split[1] + "is not a valid ip.");
                    throw new ParseException(split[1] + " is not a valid ip.", 0);
                }
            } else {
                log.debug(line + " is not in the expected format");
                throw new ParseException(line + " is not in the expected format.", 0);
            }
        }

        scanner.close();
    }

    /**
     * 1. Creates a file from the give byte array 2. Parses the file to check for errors 3. If no
     * errors are found, each location is imported.
     * 
     * @throws IOException
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws PQLException
     * @throws ConsoleException
     */
    public int bulkImportLocation(byte[] data)
            throws FileNotFoundException, IOException, ConsoleException, PQLException {
        HashMap<String, String> locMap = parseFile(getFile(data));
        Location loc;
        for (Map.Entry<String, String> entry : locMap.entrySet()) {
            loc = new Location(entry.getKey(), entry.getValue());
            saveLocation(new LocationDTO(null, loc));
        }
        return locMap.size();
    }

    /**
     * Generates a temporary file from the given byte array.
     */
    public File getFile(byte[] data) throws IOException {
        File nFile = File.createTempFile("cc_location_import_", ".txt");
        FileOutputStream fos = new FileOutputStream(nFile);
        fos.write(data);
        fos.close();
        return nFile;
    }

    /**
     * Parses the whole file first to check for errors.
     * 
     * @param file A file containing the details of the locations to be imported
     * @return map a map location names and corresponding ips to be imported
     * @throws ParseException
     * @throws FileNotFoundException
     */
    public HashMap<String, String> parseFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        HashMap<String, String> map = new HashMap<String, String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] split = line.split("\\s+");
            if (split.length == 2) {
                // removing surronding quotes
                split[1] = split[1].replaceAll("^\"", "").replaceAll("\"$", "");
                if (isValidIP(split[1])) {
                    map.put(split[0], split[1]);
                }
            }
        }

        scanner.close();
        return map;

    }

    /**
     * Validates the given ip or ip range
     */
    public boolean isValidIP(String ip) {
        String regex =
                "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\/([0-9]|[1-2][0-9]|3[0-2]))?$";
        return ip.matches(regex);
    }

    public boolean isDuplicate(String name) {
        List<PolicyDevelopmentEntity> entityList = devEntityRepository
                .findByTypeAndStatusAndTitleIgnoreCase(DevEntityType.LOCATION.getKey(),
                        PolicyDevelopmentStatus.APPROVED.getKey(), name);
        return entityList.size() > 0;
    }
}
