package com.nextlabs.destiny.cc.installer.services;

import java.util.Set;

import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.models.ProgressMessage;

/**
 * Service to notify the progress.
 *
 * @author Sachindra Dasun
 */
public interface ProgressService {

    ProgressMessage getCurrentProgress();

    void setCurrentTask(Task currentTask);

    void start();

    void stop() throws InterruptedException;

    void sendProgress();

    void setError(boolean error);

    Set<String> getAvailableTaskGroups();

}
