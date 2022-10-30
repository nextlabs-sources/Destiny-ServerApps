/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 26 May 2016
 *
 */
package com.nextlabs.destiny.console.services.delegadmin.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.dto.delegadmin.DelegateRuleLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationComponentLite;
import com.nextlabs.destiny.console.dto.delegadmin.DelegationRuleDTO;
import com.nextlabs.destiny.console.dto.delegadmin.porting.DelegationPortingDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.search.repositories.DelegateModelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegationComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.DelegationRuleSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.delegadmin.DelegateRulePortingService;
import com.nextlabs.destiny.console.services.delegadmin.DelegationRuleMgmtService;

/**
 *
 * Implementation of the {@link DelegateRulePortingService}
 *
 * @author aishwarya
 * @since 8.0
 *
 */
@Service
public class DelegateRulePortingServiceImpl implements DelegateRulePortingService {

	private static final Logger log = LoggerFactory.getLogger(DelegateRulePortingServiceImpl.class);

	@Resource
	private DelegationRuleSearchRepository daRuleRepository;

	@Resource
	private DelegateModelSearchRepository daModelSearchRepository;

	@Resource
	private DelegationComponentSearchRepository daComponentSearchRepository;

	@Autowired
	private DelegationRuleMgmtService daRuleMgmtService;

	@Autowired
	private ConfigurationDataLoader configDataLoader;

	@Autowired
	private MessageBundleService msgBundle;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public String export(List<Long> daRuleIds) throws ConsoleException {
		DelegationPortingDTO ruleExport = prepareDataToExport(daRuleIds);

		try {
			ObjectMapper mapper = new ObjectMapper();
			byte[] exportData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(ruleExport);

			String fileName = msgBundle.getText("da.rule.mgmt.export.file.prefix") + System.currentTimeMillis()
					+ ".bin";
			
			String fileLocation = configDataLoader.getPolicyExportsFileLocation() + File.separator + fileName;
			 
			Files.write(Paths.get(fileLocation), exportData);
			log.info("Policy data exported successfully to :{}", fileName);
			return fileName;
		} catch (IOException e) {
			throw new ConsoleException("Error encountered while writing to file,", e);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public String exportAll(List<DelegateRuleLite> daRuleLites) throws ConsoleException {
		log.info("Export all DA Rules processing started...");
		List<Long> ruleIds = new ArrayList<>();
		for (DelegateRuleLite daRuleLite : daRuleLites) {
			ruleIds.add(daRuleLite.getId());
		}
		DelegationPortingDTO policyExport = prepareDataToExport(ruleIds);

		try {
			ObjectMapper mapper = new ObjectMapper();
			byte[] exportData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(policyExport);

			String fileName = msgBundle.getText("da.rule.mgmt.export.file.all.prefix") + System.currentTimeMillis()
					+ ".bin";
			String fileLocation = configDataLoader.getPolicyExportsFileLocation() + File.separator + fileName;
			Files.write(Paths.get(fileLocation), exportData);
			log.info("All DA Rules exported successfully to :{}, [No of Rules: {}]", fileName, ruleIds.size());
			return fileName;
		} catch (IOException e) {
			throw new ConsoleException("Error encountered while writing to file,", e);
		}
	}

	private DelegationPortingDTO prepareDataToExport(List<Long> ruleIds) throws ConsoleException {

		log.info("prepare Data to Export invoked, ruleId = {}", ruleIds.get(0));

		Set<Long> ruleIdsToExport = new TreeSet<>();
		Set<Long> componentIdsToExport = new TreeSet<>();
		Set<Long> delegateModelIdsToExport = new TreeSet<>();

		DelegationPortingDTO ruleExportData = new DelegationPortingDTO();
		Map<Long, String> componentsMap = new HashMap<>();
		Map<Long, String> modelsMap = new HashMap<>();

		for (Long id : ruleIds) {
			DelegateRuleLite ruleLite = daRuleRepository.findById(id).orElse(null);
			if (ruleLite != null) {
				log.info("Rule Lite found, id = {} and name = {}", ruleLite.getId(), ruleLite.getName());
				DelegationRuleDTO ruleDTO = daRuleMgmtService.findById(id);
				log.info("Rule DTO found, id = {} and name = {}", ruleDTO.getId(), ruleDTO.getName());
				ruleIdsToExport.add(ruleDTO.getId());
				ruleExportData.getDelegateRules().add(ruleDTO);
			}
		}

		// get delegation components ids
		for (Long id : ruleIdsToExport) {
			DelegationRuleDTO ruleDTO = daRuleMgmtService.findById(id);

			addCompomentIds(componentIdsToExport, ruleDTO.getActionComponents());
			addCompomentIds(componentIdsToExport, ruleDTO.getResourceComponents());

		}

		// get delegation model ids and populate components map
		for (Long id : componentIdsToExport) {
			daComponentSearchRepository.findById(id)
					.ifPresent(delCompLite -> {
						componentsMap.put(id, delCompLite.getName());
						delegateModelIdsToExport.add(delCompLite.getModelId());
					});
			log.info("No of delegation models to export = {}", delegateModelIdsToExport.size());
		}
		ruleExportData.getDelegateComponents().add(componentsMap);

		// populate models map
		for (Long id : delegateModelIdsToExport) {
			daModelSearchRepository.findById(id).ifPresent(delegModel -> modelsMap.put(id, delegModel.getName()));
		}
		ruleExportData.getDelegateModels().add(modelsMap);

		log.info("Policy export data created successfully, [ Export data summary :{}]", ruleExportData);
		return ruleExportData;

	}

	private void addCompomentIds(Set<Long> componentIdsToExport, List<PolicyComponent> components) {
		for (PolicyComponent component : components) {
			for (ComponentDTO componentDTO : component.getComponents()) {
				componentIdsToExport.add(componentDTO.getId());
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public DelegationPortingDTO validateAndImport(byte[] bytes) throws ConsoleException, CircularReferenceException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			DelegationPortingDTO rulePort = mapper.readValue(bytes, DelegationPortingDTO.class);

			log.info("Da Rule porting data received, {}", rulePort);

			// get the delegation rules
			List<DelegationRuleDTO> delegateRules = rulePort.getDelegateRules();
			log.info("No of delegation rules to import = {}", delegateRules.size());

			// save the rule
			for (DelegationRuleDTO ruleDTO : delegateRules) {
				String ruleName = ruleDTO.getName();
				log.info("Rule Name = {}", ruleName);
				boolean isExists = daRuleMgmtService.isRuleExists(ruleName);
				log.info("Rule already Exists ?  = {}", isExists);
				if (isExists) {
					daRuleMgmtService.remove(ruleDTO.getId());
				}
				// set the correct reference id(s)
				updateRuleReferences(ruleDTO, rulePort);
				daRuleMgmtService.save(ruleDTO);
				log.info("Imported Delegation Rule saved successfully");
			}
			return rulePort;
		} catch (IOException e) {
			throw new ConsoleException("Error encountered while validate and importing data,", e);
		}
	}

    private void updateRuleReferences(DelegationRuleDTO ruleDTO, DelegationPortingDTO rulePort) {

		// get component references
		List<Map<Long, String>> delegateComponents = rulePort.getDelegateComponents();

		// get delegate model references
		List<Map<Long, String>> delegateModels = rulePort.getDelegateModels();

		// update resource references
		for (PolicyComponent policyComp : ruleDTO.getResourceComponents()) {
			List<ComponentDTO> components = policyComp.getComponents();
			for (int index = 0; index < components.size(); index++) {
				ComponentDTO comp = components.get(index);
				Long oldCompId = comp.getId();
				Long newCompId = getNewComponentId(oldCompId, delegateComponents);
				comp.setId(newCompId);
				components.set(index, comp);
			}
		}

		// update model references
		List<ObligationDTO> obligations = ruleDTO.getObligations();
		for (int index = 0; index < obligations.size(); index++) {
			ObligationDTO obligation = obligations.get(0);
			Long oldModelId = obligation.getPolicyModelId();
			Long newId = getNewModelId(oldModelId, delegateModels);
			obligation.setPolicyModelId(newId);
			obligations.set(index, obligation);
		}

	}

	private Long getNewComponentId(Long oldId, List<Map<Long, String>> componentsMap) {
		Long newId = null;
		for (Map<Long, String> compMap : componentsMap) {
			String compName = compMap.get(oldId);
			if (compName == null) {
				continue;
			}

			PageRequest pageable = PageRequest.of(0, 1000);
			Page<DelegationComponentLite> compLitePage = daComponentSearchRepository.findByName(
							QueryParserBase.escape(compName), pageable);
			List<DelegationComponentLite> compLites = compLitePage.getContent();
			if (!compLites.isEmpty() && compLites.size() == 1) {
				newId = compLites.get(0).getId();
			}
		}
		log.info("Old and new Comp Id are, oldId = {}, newId = {} ", oldId, newId);
		return newId;
	}

	private Long getNewModelId(Long oldId, List<Map<Long, String>> modelsMap) {
		Long newId = null;
		for (Map<Long, String> modelMap : modelsMap) {
			String modelName = modelMap.get(oldId);
			if (modelName == null)
				continue;

			PageRequest pageable = PageRequest.of(0, 1000);
			Page<DelegateModel> modelLitePage = daModelSearchRepository.findByName(
							QueryParserBase.escape(modelName), pageable);
			List<DelegateModel> modelLites = modelLitePage.getContent();
			if (!modelLites.isEmpty() && modelLites.size() == 1) {
				newId = modelLites.get(0).getId();
			}
		}
		log.info("Old and new Model Id are, oldId = {}, newId = {} ", oldId, newId);
		return newId;
	}
}
