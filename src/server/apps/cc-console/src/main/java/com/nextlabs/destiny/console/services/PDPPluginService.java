package com.nextlabs.destiny.console.services;

import com.nextlabs.destiny.console.dto.plugin.PDPPluginDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.NotUniqueException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.PDPPlugin;
import com.nextlabs.destiny.console.model.PDPPluginFile;

import java.util.List;

public interface PDPPluginService {

    PDPPlugin save(PDPPluginDTO pluginDTO) throws ConsoleException, ServerException;

    List<PDPPluginDTO> findAll() throws ConsoleException, ServerException;

    PDPPluginDTO findById(Long id) throws ConsoleException, ServerException;

    PDPPlugin modify(PDPPluginDTO pluginDTO) throws ServerException;

    void delete(List<Long> ids) throws ConsoleException, ServerException;

    void deploy(List<Long> ids) throws ConsoleException, ServerException;

    void deactivate(List<Long> ids) throws ConsoleException, ServerException;

    PDPPluginFile getFile(Long pluginId, Long fileId) throws ConsoleException, ServerException;
}
