package com.nextlabs.destiny.cc.installer.models;

import com.nextlabs.destiny.cc.installer.enums.Task;

public class ProgressMessage {

    private boolean installError;
    private Task task;

    public ProgressMessage(Task task, boolean installError) {
        this.task = task;
        this.installError = installError;
    }

    public Task getTask() {
        return task;
    }

    public boolean getInstallError() {
        return installError;
    }

}
