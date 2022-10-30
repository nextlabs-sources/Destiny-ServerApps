package com.nextlabs.destiny.cc.installer.services.impl;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.models.ProgressMessage;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation to notify the progress.
 *
 * @author Sachindra Dasun
 */
@Service
public class ProgressServiceImpl implements ProgressService {

    private static final String CLI_COLOR_GREEN = "\u001B[32m";
    private static final String CLI_COLOR_RESET = "\u001B[0m";
    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);
    private Map<String, Set<Task>> availableTaskGroups;
    @Autowired
    private CcProperties ccProperties;
    private Task currentTask;
    private boolean error;
    private SimpMessagingTemplate simpMessagingTemplate;
    private boolean stopped;
    private Map<String, Set<Task>> trackTaskGroups;

    // simpMessagingTemplate is available/ required only in InstallerWebApplication context
    public ProgressServiceImpl(@Autowired(required = false) SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostConstruct
    public void initializeTaskGroups() {
        trackTaskGroups = new LinkedHashMap<>();
        availableTaskGroups = new LinkedHashMap<>();
        List<Task> tasks = Arrays.asList(Task.values());
        tasks.forEach(task -> {
            Set<Task> taskSet = availableTaskGroups.get(task.getGroup());
            if (taskSet != null) {
                taskSet.add(task);
            } else {
                taskSet = new HashSet<>();
                taskSet.add(task);
                availableTaskGroups.put(task.getGroup(), taskSet);
            }
        });
    }

    private void trackTaskGroupProgress() {
        Task completedTask = this.currentTask;
        Set<Task> trackedTaskSet = trackTaskGroups.get(completedTask.getGroup());
        if (trackedTaskSet == null) {
            trackedTaskSet = new HashSet<>();
            trackedTaskSet.add(completedTask);
            trackTaskGroups.put(completedTask.getGroup(), trackedTaskSet);
        } else {
            trackedTaskSet.add(completedTask);
        }
        Set<Task> allTasksInSet = availableTaskGroups.get(completedTask.getGroup());
        for (Task task : allTasksInSet) {
            if (!trackedTaskSet.contains(task)) {
                return;
            }
        }
        // task group completed, send progress
        sendProgress();
    }

    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Return the current task performed by the installer.
     *
     * @return the current task
     */
    @Override
    public ProgressMessage getCurrentProgress() {
        if (currentTask == null) {
            // if currentTask is not set, return first task
            return new ProgressMessage(Task.VALIDATE, error);
        }
        return new ProgressMessage(currentTask, error);
    }

    /**
     * Set the current task.
     *
     * @param currentTask current task
     */
    @Override
    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
        trackTaskGroupProgress();
        int progress = this.currentTask.getProgress();
        String taskDescription = this.currentTask.getTaskDescription();
        logger.debug("{}: {}", taskDescription, progress);
    }

    public void sendProgress() {
        if (simpMessagingTemplate != null) {
            simpMessagingTemplate.convertAndSend("/topic/progress", this.getCurrentProgress());
        }
    }

    /**
     * Start CLI progress display.
     */
    @Async
    public void start() {
        error = false;
        stopped = false;
        if (!ccProperties.isWebInstaller()) {
            String task = "installation";
            if (CommandLineOptionsHelper.has(CommandOption.UNINSTALL)) {
                task = "uninstall";
            } else if (ccProperties.getRunningMode() == RunningMode.UPGRADE) {
                task = "upgrade";
            }
            logger.info(INSTALLER_CONSOLE_MARKER, "Do not close this window while the {} task is in progress.", task);
        }
        System.out.println();
        while (!stopped) {
            if (currentTask != null) {
                StringBuilder progress = new StringBuilder();
                for (int i = 0; i < currentTask.getProgress(); i += 2) {
                    progress.append("=");
                }
                progress.append(">");
                if (SystemUtils.IS_OS_LINUX) {
                    progress.insert(0, CLI_COLOR_GREEN);
                    progress.append(CLI_COLOR_RESET);
                }
                System.out.print(String.format("\r[%-51s] %2d%% %-50s", progress, currentTask.getProgress(),
                        currentTask.getTaskDescription()));
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error("Error", e);
            }
        }
        System.out.println();
    }

    public Set<String> getAvailableTaskGroups() {
        return availableTaskGroups.keySet();
    }

    /**
     * Stop progress display.
     *
     * @throws InterruptedException if an error occurred
     */
    @Override
    public void stop() throws InterruptedException {
        Thread.sleep(600);
        stopped = true;
    }

}
