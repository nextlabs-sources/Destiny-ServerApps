/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 30, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import javax.annotation.Resource;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.AuditLogDao;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.AuditLog;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 *
 * Implementation of the Audit Log Service
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory
            .getLogger(AuditLogServiceImpl.class);

    @Autowired
    private AuditLogDao auditLogDao;

    @Autowired
    protected MessageBundleService msgBundle;

    @Resource
    private ApplicationUserSearchRepository appUserRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AuditLog save(String component, String msgCode, String... msgParams)
            throws ConsoleException {
        AuditLog auditLog = new AuditLog();
        auditLog.setComponent(component);
        auditLog.setMsgCode(msgCode);
        for (String param : msgParams) {
            auditLog.getParams().add(param);
        }

        auditLogDao.create(auditLog);
        log.debug("New audit log saved successfully, [ Id : {}]",
                auditLog.getId());
        return auditLog;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public AuditLog findById(Long id) throws ConsoleException {
        try {
            AuditLog auditLog = auditLogDao.findById(id);

            if (auditLog == null) {
                log.info("Audit log not found for id :{}", id);
                return null;
            }

            appUserRepository.findById(auditLog.getOwnerId())
                    .ifPresent(user -> auditLog.setOwnerDisplayName(user.getDisplayName()));

            auditLog.setActivityMsg(msgBundle.getText(auditLog.getMsgCode(),
                    auditLog.getParamsAsArray()));

            return auditLog;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a AuditLog by id", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<AuditLog> findByUser(Long userId) throws ConsoleException {
        try {
            List<AuditLog> auditLogs = auditLogDao.findByUser(userId);

            ApplicationUser user = null;
            for (AuditLog auditLog : auditLogs) {
                if (user == null) {
                    user = appUserRepository.findById(auditLog.getOwnerId()).orElse(null);
                }
                if (user != null) {
                    auditLog.setOwnerDisplayName(user.getDisplayName());
                }
                auditLog.setActivityMsg(msgBundle.getText(auditLog.getMsgCode(),
                        auditLog.getParamsAsArray()));
            }

            log.debug("Audit Logs found for given user, [ User: {}, Size: {}]",
                    userId, auditLogs.size());
            return auditLogs;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a AuditLogs by user", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<AuditLog> findByComponent(String component)
            throws ConsoleException {
        try {
            List<AuditLog> auditLogs = auditLogDao.findByComponent(component);

            for (AuditLog auditLog : auditLogs) {
                appUserRepository.findById(auditLog.getOwnerId())
                        .ifPresent(user -> auditLog.setOwnerDisplayName(user.getDisplayName()));

                auditLog.setActivityMsg(msgBundle.getText(auditLog.getMsgCode(),
                        auditLog.getParamsAsArray()));
            }

            log.debug(
                    "Audit Logs found for given component, [ Compoent: {}, Size: {}]",
                    component, auditLogs.size());
            return auditLogs;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a AuditLogs by component", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<AuditLog> findByLastXRecords(int rowCount)
            throws ConsoleException {
        try {
            List<AuditLog> auditLogs = auditLogDao.findByLastXRecords(rowCount);

            for (AuditLog auditLog : auditLogs) {
                appUserRepository.findById(auditLog.getOwnerId())
                        .ifPresent(user -> auditLog.setOwnerDisplayName(user.getDisplayName()));
                auditLog.setActivityMsg(msgBundle.getText(auditLog.getMsgCode(),
                        auditLog.getParamsAsArray()));
            }

            log.debug("Last {} Audit Logs found.[Size: {}]", rowCount,
                    auditLogs.size());
            return auditLogs;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a AuditLogs by last x records",
                    e);
        }
    }

}
