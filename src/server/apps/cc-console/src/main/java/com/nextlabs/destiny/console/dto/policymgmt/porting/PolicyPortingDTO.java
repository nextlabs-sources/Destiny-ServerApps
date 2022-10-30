/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt.porting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.enums.ImportMechanism;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

/**
 *
 * DTO for policy and its related components porting
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyPortingDTO implements Serializable {

    private static final long serialVersionUID = -861641148075706963L;

    private List<PolicyModel> policyModels;
    private List<ComponentDTO> components;
    private PolicyTree policyTree;
    private List<String> policyFolders;
    private List<String> componentFolders;
    private List<Long> importedPolicyIds;
    private transient Set<Long> importedPolicyIdSet;
    private transient Set<Long> importedComponentIdSet;
    private transient Set<Long> importedPolicyModelIdSet;
    private transient Set<String> importedPolicyFolderSet;
    private transient Set<String> importedComponentFolderSet;
    private boolean overrideDuplicates = false;
    private boolean nonBlockingError = false;
    private ImportMechanism mechanism = ImportMechanism.PARTIAL;

    private Map<Long, List<Long>> componentToSubCompMap;

    public List<PolicyModel> getPolicyModels() {
        if (policyModels == null) {
            policyModels = new ArrayList<>();
        }
        return policyModels;
    }

    public void setPolicyModels(List<PolicyModel> policyModels) {
        this.policyModels = policyModels;
    }

    public List<ComponentDTO> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    public void setComponents(List<ComponentDTO> components) {
        this.components = components;
    }

    public Map<Long, List<Long>> getComponentToSubCompMap() {
        if (componentToSubCompMap == null) {
            componentToSubCompMap = new HashMap<>();
        }
        return componentToSubCompMap;
    }

    public void setComponentToSubCompMap(
            Map<Long, List<Long>> componentToSubCompMap) {
        this.componentToSubCompMap = componentToSubCompMap;
    }
    
    public List<Long> getImportedPolicyIds() {
		if (importedPolicyIds == null) {
			importedPolicyIds = new ArrayList<>();
		}
		return importedPolicyIds;
	}

	public void setImportedPolicyIds(List<Long> importedPolicyIds) {
		this.importedPolicyIds = importedPolicyIds;
	}

    public PolicyTree getPolicyTree() {
        if (policyTree == null) {
            policyTree = new PolicyTree();
        }
        return policyTree;
    }

    public void setPolicyTree(PolicyTree policyTree) {
        this.policyTree = policyTree;
    }

    public List<String> getPolicyFolders() {
        if (policyFolders == null) {
            policyFolders = new ArrayList<>();
        }
        return policyFolders;
    }

    public void setPolicyFolders(List<String> policyFolders) {
        this.policyFolders = policyFolders;
    }

    public List<String> getComponentFolders() {
        if (componentFolders == null) {
            componentFolders = new ArrayList<>();
        }
        return componentFolders;
    }

    public void setComponentFolders(List<String> componentFolders) {
        this.componentFolders = componentFolders;
    }

    public boolean isOverrideDuplicates() {
		return overrideDuplicates;
	}

	public void setOverrideDuplicates(boolean overrideDuplicates) {
		this.overrideDuplicates = overrideDuplicates;
	}

	public boolean hasNonBlockingError() {
		return nonBlockingError;
	}

	public void setNonBlockingError(boolean nonBlockingError) {
		this.nonBlockingError = nonBlockingError;
	}

    public ImportMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(ImportMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public Set<Long> getImportedPolicyIdSet() {
        return importedPolicyIdSet;
    }

    public void setImportedPolicyIdSet(Set<Long> importedPolicyIdSet) {
        this.importedPolicyIdSet = importedPolicyIdSet;
    }

    public Set<Long> getImportedComponentIdSet() {
        return importedComponentIdSet;
    }

    public void setImportedComponentIdSet(Set<Long> importedComponentIdSet) {
        this.importedComponentIdSet = importedComponentIdSet;
    }

    public Set<Long> getImportedPolicyModelIdSet() {
        return importedPolicyModelIdSet;
    }

    public void setImportedPolicyModelIdSet(Set<Long> importedPolicyModelIdSet) {
        this.importedPolicyModelIdSet = importedPolicyModelIdSet;
    }

    public Set<String> getImportedPolicyFolderSet() {
        return importedPolicyFolderSet;
    }

    public void setImportedPolicyFolderSet(Set<String> importedPolicyFolderSet) {
        this.importedPolicyFolderSet = importedPolicyFolderSet;
    }

    public Set<String> getImportedComponentFolderSet() {
        return importedComponentFolderSet;
    }

    public void setImportedComponentFolderSet(Set<String> importedComponentFolderSet) {
        this.importedComponentFolderSet = importedComponentFolderSet;
    }

    @Override
	public String toString() {
		return String.format("PolicyPortingDTO [policyModels=%s, components=%s, policyTree=%s, overrideDuplicates=%s]",
				this.getPolicyModels().size(), this.getComponents().size(), policyTree.getRoot().getChildren().size(),
				overrideDuplicates);
	}
	
}
