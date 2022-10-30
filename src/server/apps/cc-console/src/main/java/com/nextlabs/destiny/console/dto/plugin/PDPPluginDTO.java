package com.nextlabs.destiny.console.dto.plugin;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.enums.PDPPluginFileType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.PDPPlugin;
import com.nextlabs.destiny.console.model.PDPPluginFile;
import com.nextlabs.destiny.console.utils.JsonUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PDPPluginDTO
    implements Auditable {

    private long id;

    private String name;

    private String description;

    private String status;

    private long activeFrom;

    private long activeTo;

    private long createdDate;

    private long modifiedDate;

    private MultipartFile mainJar;

    private MultipartFile properties;

    private List<MultipartFile> externalJars;

    private List<MultipartFile> externalFiles;

    private List<PDPPluginFileDTO> pdpPluginFileDTOs;

    private List<Long> filesToRemove;

    // Indicate user clicked on Save vs Save and Deploy
    private boolean deploy;

    // Indicate current PDP plugin status at the backend to display back in UI
    private boolean deployed;

    // PDP deployment status String to display
    private String deploymentStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public long getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(long activeTo) {
        this.activeTo = activeTo;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public MultipartFile getMainJar() {
        return mainJar;
    }

    public void setMainJar(MultipartFile mainJar) {
        this.mainJar = mainJar;
    }

    public MultipartFile getProperties() {
        return properties;
    }

    public void setProperties(MultipartFile properties) {
        this.properties = properties;
    }

    public List<MultipartFile> getExternalJars() {
        if(externalJars == null) {
            externalJars = new ArrayList<>();
        }

        return externalJars;
    }

    public void setExternalJars(List<MultipartFile> externalJars) {
        this.externalJars = externalJars;
    }

    public List<MultipartFile> getExternalFiles() {
        if(externalFiles == null) {
            externalFiles = new ArrayList<>();
        }

        return externalFiles;
    }

    public void setExternalFiles(List<MultipartFile> externalFiles) {
        this.externalFiles = externalFiles;
    }


    public List<PDPPluginFileDTO> getPdpPluginFileDTOs() {
        if(pdpPluginFileDTOs == null) {
            pdpPluginFileDTOs = new ArrayList<>();
        }

        return pdpPluginFileDTOs;
    }

    public void setPdpPluginFileDTOs(List<PDPPluginFileDTO> pdpPluginFileDTOs) {
        this.pdpPluginFileDTOs = pdpPluginFileDTOs;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public List<Long> getFilesToRemove() {
        return filesToRemove;
    }

    public void setFilesToRemove(List<Long> filesToRemove) {
        this.filesToRemove = filesToRemove;
    }

    public static PDPPluginDTO getDTO(PDPPlugin plugin) {
        PDPPluginDTO pluginDTO = null;

        if(plugin != null) {
            pluginDTO = new PDPPluginDTO();

            pluginDTO.setId(plugin.getId());
            pluginDTO.setName(plugin.getName());
            pluginDTO.setDescription(plugin.getDescription());
            pluginDTO.setStatus(plugin.getStatus().name());
            if(plugin.getActiveFrom() != null)
                pluginDTO.setActiveFrom(plugin.getActiveFrom());
            if(plugin.getActiveTo() != null)
                pluginDTO.setActiveTo(plugin.getActiveTo());
            pluginDTO.setCreatedDate(plugin.getCreatedDate());
            pluginDTO.setModifiedDate(plugin.getModifiedDate());

            long now = System.currentTimeMillis();
            if(now >= pluginDTO.getActiveFrom()
                    && now <= pluginDTO.getActiveTo()) {
                pluginDTO.setDeployed(true);
                pluginDTO.setDeploymentStatus("Deployed");
            } else {
                pluginDTO.setDeployed(false);
                pluginDTO.setDeploymentStatus("Inactive");
            }

            for(PDPPluginFile pluginFile: plugin.getPluginFiles()) {
                PDPPluginFileDTO pluginFileDTO = new PDPPluginFileDTO();

                pluginFileDTO.setId(pluginFile.getId());
                pluginFileDTO.setName(pluginFile.getName());
                pluginFileDTO.setType(pluginFile.getType().name());
                pluginFileDTO.setModifiedDate(pluginFile.getModifiedDate());

                pluginDTO.getPdpPluginFileDTOs().add(pluginFileDTO);
            }
        }

        return pluginDTO;
    }

    @Override
    public String toAuditString()
            throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();
            audit.put("Name", name);
            audit.put("Description", description);
            audit.put("Status", status);
            audit.put("Active From", activeFrom);
            audit.put("Active To", activeTo);
            if(mainJar != null)
                audit.put("Main Jar", mainJar.getName());

            if(properties != null)
                audit.put("Properties", properties.getName());

            if(externalJars != null) {
                List<String> externalJarsList = new ArrayList<>();
                for (MultipartFile externalJar : externalJars) {
                    externalJarsList.add(externalJar.getName());
                }
                audit.put("3rd Party Libraries", externalJarsList.toString());
            }

            if(externalFiles != null) {
                List<String> externalFilesList = new ArrayList<>();
                for (MultipartFile externalFile : externalFiles) {
                    externalFilesList.add(externalFile.getName());
                }
                audit.put("Other Files", externalFilesList.toString());
            }

            if(pdpPluginFileDTOs != null
                    && !pdpPluginFileDTOs.isEmpty()) {
                List<String> externalJarsList = new ArrayList<>();
                List<String> externalFilesList = new ArrayList<>();

                for(PDPPluginFileDTO pluginFileDTO : pdpPluginFileDTOs) {
                    if(PDPPluginFileType.PRIMARY_JAR.name().equals(pluginFileDTO.getType())) {
                        audit.put("Main Jar", pluginFileDTO.getName());
                    } else if(PDPPluginFileType.PROPERTIES.name().equals(pluginFileDTO.getType())) {
                        audit.put("Properties", pluginFileDTO.getName());
                    } else if(PDPPluginFileType.THIRD_PARTY_JAR.name().equals(pluginFileDTO.getType())) {
                        externalJarsList.add(pluginFileDTO.getName());
                    } else {
                        externalFilesList.add(pluginFileDTO.getName());
                    }
                }

                if(!externalJarsList.isEmpty()) {
                    audit.put("3rd Party Libraries", externalJarsList.toString());
                }

                if(!externalFilesList.isEmpty()) {
                    audit.put("Other Files", externalFilesList.toString());
                }
            }

            return JsonUtil.toJsonString(audit);
        } catch(Exception e) {
            throw new ConsoleException(e);
        }
    }
}
