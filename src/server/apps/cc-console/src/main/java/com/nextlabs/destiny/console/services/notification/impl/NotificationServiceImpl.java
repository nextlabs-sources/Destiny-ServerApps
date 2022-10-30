package com.nextlabs.destiny.console.services.notification.impl;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.ApplicationUserDao;
import com.nextlabs.destiny.console.dao.SuperApplicationUserDao;
import com.nextlabs.destiny.console.dto.authentication.ActiveUserStore;
import com.nextlabs.destiny.console.dto.notification.NotificationLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.SuperApplicationUser;
import com.nextlabs.destiny.console.model.notification.AppUserNotification;
import com.nextlabs.destiny.console.model.notification.Notification;
import com.nextlabs.destiny.console.model.notification.SuperAppUserNotification;
import com.nextlabs.destiny.console.model.notification.UserNotificationId;
import com.nextlabs.destiny.console.repositories.AppUserNotificationRepository;
import com.nextlabs.destiny.console.repositories.NotificationRepository;
import com.nextlabs.destiny.console.repositories.SuperAppUserNotificationRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.notification.NotificationSearchService;
import com.nextlabs.destiny.console.services.notification.NotificationService;
import com.nextlabs.destiny.console.services.notification.console.ConsoleNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

/**
 * Service to manage user notification
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private NotificationSearchService notificationSearchService;
    private AppUserNotificationRepository appUserNotificationRepository;
    private SuperAppUserNotificationRepository superAppUserNotificationRepository;
    private ConsoleNotificationService consoleNotificationService;
    private ApplicationUserSearchRepository applicationUserSearchRepository;
    private ActiveUserStore activeUserStore;
    private SuperApplicationUserDao superApplicationUserDao;
    private ApplicationUserDao applicationUserDao;

    @Override
    public void saveAndNotifyUser(List<ApplicationUser> recipients, Notification notification) throws ConsoleException {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }
        notificationRepository.save(notification);
        for (ApplicationUser recipient: recipients) {
            if (recipient.getUsername().equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
                SuperApplicationUser applicationUser = superApplicationUserDao.findById(recipient.getId());
                SuperAppUserNotification superAppUserNotification = new SuperAppUserNotification(applicationUser, notification);
                superAppUserNotification.setActive(true);
                superAppUserNotificationRepository.save(superAppUserNotification);
                if (activeUserStore.getUsers().contains(applicationUser.getUsername())) {
                    consoleNotificationService.notifyUser(applicationUser.getUsername(),
                            NotificationLite.getLite(superAppUserNotification, applicationUserSearchRepository));
                }
                notificationSearchService.reIndexNotification(superAppUserNotification);
            } else {
                ApplicationUser applicationUser = applicationUserDao.findById(recipient.getId());
                AppUserNotification appUserNotification = new AppUserNotification(applicationUser, notification);
                appUserNotification.setActive(true);
                appUserNotificationRepository.save(appUserNotification);
                if (activeUserStore.getUsers().contains(applicationUser.getUsername())) {
                    consoleNotificationService.notifyUser(applicationUser.getUsername(),
                            NotificationLite.getLite(appUserNotification, applicationUserSearchRepository));
                }
                notificationSearchService.reIndexNotification(appUserNotification);
            }
        }
    }

    @Override
    public void markAsRead(Long notificationId) throws ConsoleException {
        PrincipalUser currentUser = getCurrentUser();
        UserNotificationId id = new UserNotificationId(currentUser.getUserId(), notificationId);
        if (currentUser.getUsername().equalsIgnoreCase(ApplicationUser.SUPER_USERNAME)) {
            SuperAppUserNotification superAppUserNotification = superAppUserNotificationRepository.findById(id)
                    .orElseThrow(() -> new ConsoleException(String.format("Notification not found: %s", id)));
            superAppUserNotification.setNotified(true);
            superAppUserNotificationRepository.save(superAppUserNotification);
            notificationSearchService.reIndexNotification(superAppUserNotification);

        } else {
            AppUserNotification appUserNotification = appUserNotificationRepository.findById(id)
                    .orElseThrow(() -> new ConsoleException(String.format("Notification not found: %s", id)));
            appUserNotification.setNotified(true);
            appUserNotificationRepository.save(appUserNotification);
            notificationSearchService.reIndexNotification(appUserNotification);
        }
    }

    public static List<ApplicationUser> getNotificationRecipients(String requiredAuthority, Long initiatedBy, Long policyAuthorId,
                                                                  ApplicationUserSearchRepository appUserSearchRepository) throws ServerException {
        ApplicationUser initiatedByUser = appUserSearchRepository.findById(initiatedBy)
                .orElseThrow(() -> new ServerException(String.format("User not found, User ID: %d", initiatedBy)));
        ApplicationUser policyAuthor = appUserSearchRepository.findById(policyAuthorId)
                .orElseThrow(() -> new ServerException(String.format("Policy author not found, User ID: %d", policyAuthorId)));
        Iterable<ApplicationUser> applicationUserList = appUserSearchRepository.findAll();
        List<ApplicationUser> recipientList = StreamSupport.stream(applicationUserList.spliterator(), true)
                .filter(applicationUser -> applicationUser.hasAccess(requiredAuthority) &&
                        !applicationUser.equals(initiatedByUser))
                .collect(Collectors.toList());
        if (!initiatedByUser.equals(policyAuthor) && recipientList.stream().noneMatch(policyAuthor::equals)) {
            recipientList.add(policyAuthor);
        }
        return recipientList;
    }

    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Autowired
    public void setNotificationSearchService(NotificationSearchService notificationSearchService) {
        this.notificationSearchService = notificationSearchService;
    }

    @Autowired
    public void setAppUserNotificationRepository(AppUserNotificationRepository appUserNotificationRepository) {
        this.appUserNotificationRepository = appUserNotificationRepository;
    }

    @Autowired
    public void setConsoleNotificationService(ConsoleNotificationService consoleNotificationService) {
        this.consoleNotificationService = consoleNotificationService;
    }

    @Autowired
    public void setApplicationUserSearchRepository(ApplicationUserSearchRepository applicationUserSearchRepository) {
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    @Autowired
    public void setSuperAppUserNotificationRepository(SuperAppUserNotificationRepository superAppUserNotificationRepository) {
        this.superAppUserNotificationRepository = superAppUserNotificationRepository;
    }

    @Autowired
    public void setActiveUserStore(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @Autowired
    public void setSuperApplicationUserDao(SuperApplicationUserDao superApplicationUserDao) {
        this.superApplicationUserDao = superApplicationUserDao;
    }

    @Autowired
    public void setApplicationUserDao(ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }
}
