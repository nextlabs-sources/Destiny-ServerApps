/*
 * Copyright 2019 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 6, 2019
 *
 */
package com.nextlabs.destiny.console.utils;

public enum SystemCodes {

    DATA_SAVED_SUCCESS("success.data.saved"),
    DATA_MODIFIED_SUCCESS("success.data.modified"),
    DATA_DELETED_SUCCESS("success.data.deleted"),
    DATA_FOUND_SUCCESS("success.data.found"),
    DATA_LOADED_SUCCESS("success.data.loaded"),
    DATA_REINDEXED_SUCCESS("success.data.reindexed"),
    DATA_DEPLOYED_SUCCESS("success.data.deployed"),
    DATA_UNDEPLOYED_SUCCESS("success.data.undeployed"),
    DATA_VALIDATED_SUCCESS("success.data.validated"),
    ENROLLMENT_DATA_SYNC_STARTED_SUCCESS("success.enrollment.data.sync.started"),
    FILE_EXPORT_SUCCESS("success.file.export"),
    FILE_IMPORT_SUCCESS("success.file.import"),
    TASK_EXECUTION_ERROR("task.execution.error"),
    TASK_INVALID_INPUT("invalid.task.input");

    public static final String CODE_FORMAT = "%s.code";
    private final String code;
    private final String messageKey;

    SystemCodes(String messageKey) {
        this.messageKey = messageKey;
        this.code = String.format(CODE_FORMAT, messageKey);
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
