package com.nextlabs.destiny.console.services.impl;

import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.plugin.PDPPluginDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.PDPPluginFileType;
import com.nextlabs.destiny.console.enums.PDPPluginStatus;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.PDPPlugin;
import com.nextlabs.destiny.console.model.PDPPluginFile;
import com.nextlabs.destiny.console.repositories.PDPPluginRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.PDPPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class PDPPluginServiceImpl
                implements PDPPluginService {
    private static final Long ACTIVE_TO = 253402271999000L;

    @Autowired
    private PDPPluginRepository pluginRepository;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Autowired
    private MessageBundleService msgBundle;

    @Override
    public PDPPlugin save(PDPPluginDTO pluginDTO)
                    throws ConsoleException, ServerException {
        List<PDPPlugin> existing = pluginRepository.findByNameIgnoreCaseAndStatusNot(pluginDTO.getName(), PDPPluginStatus.DELETED);

        if(!existing.isEmpty()) {
            throw new NotUniqueException(msgBundle.getText("duplicate.pdp.plugin.code"),
                            msgBundle.getText("duplicate.pdp.plugin.message"));
        }

        try {
            long now = System.currentTimeMillis();
            PDPPlugin plugin = new PDPPlugin();
            plugin.setName(pluginDTO.getName());
            plugin.setDescription(pluginDTO.getDescription());
            if (pluginDTO.isDeploy()) {
                plugin.setStatus(PDPPluginStatus.DEPLOYED);
                plugin.setActiveFrom(now);
                plugin.setActiveTo(ACTIVE_TO);
            } else {
                plugin.setStatus(PDPPluginStatus.DRAFT);
            }
            plugin.setCreatedDate(now);
            plugin.setModifiedDate(now);

            plugin.getPluginFiles().add(getPluginFileEntity(plugin, pluginDTO.getMainJar(),
                            PDPPluginFileType.PRIMARY_JAR));
            plugin.getPluginFiles().add(getPluginFileEntity(plugin, pluginDTO.getProperties(),
                            PDPPluginFileType.PROPERTIES));
            for (MultipartFile extJar : pluginDTO.getExternalJars()) {
                plugin.getPluginFiles().add(getPluginFileEntity(plugin, extJar, PDPPluginFileType.THIRD_PARTY_JAR));
            }
            for (MultipartFile extFile : pluginDTO.getExternalFiles()) {
                plugin.getPluginFiles().add(getPluginFileEntity(plugin, extFile, PDPPluginFileType.OTHER));
            }

            pluginRepository.save(plugin);
            entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.PDP_PLUGIN.getCode(), plugin.getId(), null,
                            pluginDTO.toAuditString());

            return plugin;
        } catch(Exception err) {
            throw new ServerException(err);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PDPPluginDTO> findAll()
                    throws ConsoleException, ServerException {
        List<PDPPluginDTO> pluginDTOs = new ArrayList<>();
        List<PDPPlugin> plugins = pluginRepository.findByStatusNot(PDPPluginStatus.DELETED);

        for(PDPPlugin plugin : plugins) {
            pluginDTOs.add(PDPPluginDTO.getDTO(plugin));
        }

        return pluginDTOs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PDPPluginDTO findById(Long id)
                    throws ConsoleException, ServerException {
        PDPPlugin plugin = pluginRepository.findById(id).orElse(null);
        return PDPPluginDTO.getDTO(plugin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PDPPlugin modify(PDPPluginDTO pluginDTO)
                    throws ServerException {
        List<PDPPlugin> otherPlugins = pluginRepository.findByNameIgnoreCaseAndIdNotAndStatusNot(pluginDTO.getName(), pluginDTO.getId(), PDPPluginStatus.DELETED);
        if (!otherPlugins.isEmpty()) {
            throw new NotUniqueException(msgBundle.getText("duplicate.pdp.plugin.code"),
                            msgBundle.getText("duplicate.pdp.plugin.message"));
        }

        try {
            Optional<PDPPlugin> existing = pluginRepository.findById(pluginDTO.getId());

            if (existing.isPresent()) {
                Long now = System.currentTimeMillis();
                PDPPlugin plugin = existing.get();

                String snapshot = PDPPluginDTO.getDTO(plugin).toAuditString();
                plugin.setName(pluginDTO.getName());
                plugin.setDescription(pluginDTO.getDescription());
                plugin.setModifiedDate(now);

                if (pluginDTO.getProperties() != null || pluginDTO.getMainJar() != null) {
                    for (PDPPluginFile pluginFile : plugin.getPluginFiles()) {
                        if (pluginDTO.getProperties() != null && PDPPluginFileType.PROPERTIES.equals(pluginFile.getType())) {
                            pluginFile.setName(pluginDTO.getProperties().getOriginalFilename());
                            pluginFile.setContent(pluginDTO.getProperties().getBytes());
                            pluginFile.setModifiedDate(now);
                        }

                        if (pluginDTO.getMainJar() != null && PDPPluginFileType.PRIMARY_JAR.equals(pluginFile.getType())) {
                            pluginFile.setName(pluginDTO.getMainJar().getOriginalFilename());
                            pluginFile.setContent(pluginDTO.getMainJar().getBytes());
                            pluginFile.setModifiedDate(now);
                        }
                    }
                }

                if (plugin.getActiveFrom() == null || plugin.getActiveFrom() == 0) {
                    if (pluginDTO.getFilesToRemove() != null
                            && !pluginDTO.getFilesToRemove().isEmpty()) {
                        for (Long fileIdToRemove : pluginDTO.getFilesToRemove()) {
                            Iterator<PDPPluginFile> fileIterator =
                                    plugin.getPluginFiles().iterator();
                            while (fileIterator.hasNext()) {
                                PDPPluginFile pdpPluginFile = fileIterator.next();
                                if (pdpPluginFile.getId().equals(fileIdToRemove)) {
                                    pdpPluginFile.setPlugin(null);
                                    fileIterator.remove();
                                    break;
                                }
                            }
                        }
                    }

                    for (MultipartFile extJar : pluginDTO.getExternalJars()) {
                            plugin.getPluginFiles().add(getPluginFileEntity(plugin, extJar,
                                    PDPPluginFileType.THIRD_PARTY_JAR));
                    }

                    for (MultipartFile extFile : pluginDTO.getExternalFiles()) {
                            plugin.getPluginFiles().add(getPluginFileEntity(plugin, extFile,
                                    PDPPluginFileType.OTHER));
                    }
                }

                if (pluginDTO.isDeploy()) {
                    plugin.setStatus(PDPPluginStatus.DEPLOYED);
                    plugin.setActiveFrom(now);
                    plugin.setActiveTo(ACTIVE_TO);
                } else {
                    plugin.setStatus(PDPPluginStatus.DRAFT);
                }

                pluginRepository.save(plugin);
                entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE, AuditableEntity.PDP_PLUGIN.getCode(), plugin.getId(), snapshot,
                                PDPPluginDTO.getDTO(plugin).toAuditString());

                return plugin;
            }

            throw new NoDataFoundException(msgBundle.getText("no.data.found.code"), msgBundle.getText("no.data.found"));
        } catch(Exception err) {
            throw new ServerException(err);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids)
                    throws ConsoleException, ServerException {
        List<PDPPlugin> plugins = pluginRepository.findAllById(ids);

        Long now = System.currentTimeMillis();
        for(PDPPlugin plugin : plugins) {
            if(!PDPPluginStatus.DELETED.equals(plugin.getStatus())) {
                String snapshot = PDPPluginDTO.getDTO(plugin).toAuditString();

                plugin.setStatus(PDPPluginStatus.DELETED);
                plugin.setActiveTo(now);
                plugin.setModifiedDate(now);

                for(PDPPluginFile file : plugin.getPluginFiles()) {
                    file.setPlugin(null);
                }
                plugin.getPluginFiles().clear();

                pluginRepository.save(plugin);

                entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.PDP_PLUGIN.getCode(), plugin.getId(), snapshot, null);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deploy(List<Long> ids) throws ConsoleException, ServerException {
        List<PDPPlugin> plugins = pluginRepository.findAllById(ids);

        Long now = System.currentTimeMillis();
        for(PDPPlugin plugin : plugins) {
            if(PDPPluginStatus.DRAFT == plugin.getStatus() || PDPPluginStatus.INACTIVE == plugin.getStatus()) {
                String snapshot = PDPPluginDTO.getDTO(plugin).toAuditString();

                plugin.setStatus(PDPPluginStatus.DEPLOYED);
                plugin.setModifiedDate(now);
                plugin.setActiveFrom(now);
                plugin.setActiveTo(ACTIVE_TO);
                pluginRepository.save(plugin);

                entityAuditLogDao.addEntityAuditLog(AuditAction.DEPLOY, AuditableEntity.PDP_PLUGIN.getCode(),
                                plugin.getId(), snapshot, PDPPluginDTO.getDTO(plugin).toAuditString());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivate(List<Long> ids) throws ConsoleException {
        List<PDPPlugin> plugins = pluginRepository.findAllById(ids);

        Long now = System.currentTimeMillis();
        for(PDPPlugin plugin : plugins) {
            if(PDPPluginStatus.DEPLOYED == plugin.getStatus()) {
                String snapshot = PDPPluginDTO.getDTO(plugin).toAuditString();
                plugin.setStatus(PDPPluginStatus.INACTIVE);
                plugin.setModifiedDate(now);
                plugin.setActiveTo(now);
                pluginRepository.save(plugin);

                entityAuditLogDao.addEntityAuditLog(AuditAction.UNDEPLOY, AuditableEntity.PDP_PLUGIN.getCode(),
                                plugin.getId(), snapshot, PDPPluginDTO.getDTO(plugin).toAuditString());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PDPPluginFile getFile(Long pluginId, Long fileId) throws ConsoleException, ServerException {
        Optional<PDPPlugin> plugin = pluginRepository.findById(pluginId);

        if(plugin.isPresent()) {
            for(PDPPluginFile pluginFile : plugin.get().getPluginFiles()) {
                if(pluginFile.getId().equals(fileId)) {
                    return pluginFile;
                }
            }
        }

        throw new ConsoleException("PDP plugin file not found.");
    }

    private PDPPluginFile getPluginFileEntity(PDPPlugin plugin, MultipartFile file, PDPPluginFileType type)
            throws IOException {
        PDPPluginFile pluginFile = new PDPPluginFile();
        pluginFile.setPlugin(plugin);
        pluginFile.setType(type);
        pluginFile.setName(file.getOriginalFilename());
        pluginFile.setContent(file.getBytes());
        pluginFile.setModifiedDate(System.currentTimeMillis());

        return pluginFile;
    }
}
