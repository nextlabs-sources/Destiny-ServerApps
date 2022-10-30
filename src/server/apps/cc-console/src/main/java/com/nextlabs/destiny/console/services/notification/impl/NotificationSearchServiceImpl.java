package com.nextlabs.destiny.console.services.notification.impl;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.notification.NotificationLite;
import com.nextlabs.destiny.console.enums.SearchFieldType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.notification.AppUserNotification;
import com.nextlabs.destiny.console.model.notification.SuperAppUserNotification;
import com.nextlabs.destiny.console.model.notification.UserNotificationId;
import com.nextlabs.destiny.console.repositories.AppUserNotificationRepository;
import com.nextlabs.destiny.console.repositories.SuperAppUserNotificationRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.NotificationSearchRepository;
import com.nextlabs.destiny.console.services.notification.NotificationSearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

/**
 * Notification Search Service
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 */
@Service
public class NotificationSearchServiceImpl implements NotificationSearchService {
    private static final Logger log = LoggerFactory.getLogger(NotificationSearchServiceImpl.class);

    private NotificationSearchRepository notificationSearchRepository;
    private AppUserNotificationRepository appUserNotificationRepository;
    private SuperAppUserNotificationRepository superAppUserNotificationRepository;
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Override
    public Page<NotificationLite> findNotificationsByCriteria(SearchCriteria criteria) throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            SearchField searchField = new SearchField();
            searchField.setField("addressedUserId");
            searchField.setType(SearchFieldType.SINGLE_EXACT_MATCH);
            searchField.setValue(new StringFieldValue(getCurrentUser().getUserId()));
            criteria.getFields().add(searchField);
            Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());
            return findByCriteria(criteria, pageable);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while finding notifications by given criteria", e);
        }
    }

    @Override
    public void reIndexAllNotifications() throws ConsoleException {
        UserNotificationId id = null;
        try {
            notificationSearchRepository.deleteAll();

            long startTime = System.currentTimeMillis();
            Optional<List<AppUserNotification>> optionalAppUserNotifications = appUserNotificationRepository.findByActiveIsTrue();
            int size = 0;
            if (optionalAppUserNotifications.isPresent()) {
                List<AppUserNotification> appUserNotifications = optionalAppUserNotifications.get();
                size = appUserNotifications.size();
                for (AppUserNotification appUserNotification : appUserNotifications) {
                    id = appUserNotification.getId();
                    reIndexNotification(appUserNotification);
                }
            }
            Optional<List<SuperAppUserNotification>> optionalSuperAppUserNotifications = superAppUserNotificationRepository.findByActiveIsTrue();
            if (optionalSuperAppUserNotifications.isPresent()) {
                List<SuperAppUserNotification> userNotifications = optionalSuperAppUserNotifications.get();
                size += userNotifications.size();
                for (SuperAppUserNotification userNotification : userNotifications) {
                    id = userNotification.getId();
                    reIndexNotification(userNotification);
                }
            }
            long endTime = System.currentTimeMillis();
            log.info(
                    "Notification re-indexing successful, No of re-indexes :{}, Time taken:{}ms",
                    size, (endTime - startTime));
        } catch (Exception e) {
            throw new ConsoleException(
                    String.format("Error encountered in re-indexing notifications, [%s] ", id),
                    e);
        }
    }

    @Override
    public void reIndexNotification(AppUserNotification notification) throws ConsoleException {
        try {
            notificationSearchRepository.save(NotificationLite
                    .getLite(notification, applicationUserSearchRepository));
        } catch (Exception e){
            throw new ConsoleException(
                    String.format("Error encountered in re-indexing app user notification, [%s] ", notification.getId()),
                    e);
        }
    }

    @Override
    public void reIndexNotification(SuperAppUserNotification notification) throws ConsoleException {
        try {
            notificationSearchRepository.save(NotificationLite
                    .getLite(notification, applicationUserSearchRepository));
        } catch (Exception e){
            throw new ConsoleException(
                    String.format("Error encountered in re-indexing super app user notification, [%s] ", notification.getId()),
                    e);
        }
    }

    private Page<NotificationLite> findByCriteria(SearchCriteria criteria,
                                                            Pageable pageable) throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);

            Query searchQuery = withSorts(nativeQuery.build(),
                    criteria.getSortFields());

            log.debug("notifications search query :{},", query);
            Page<NotificationLite> policyListPage = notificationSearchRepository.search(searchQuery);

            log.info("notification list page :{}, No of elements :{}",
                    policyListPage.getTotalPages(),
                    policyListPage.getNumberOfElements());
            return policyListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find notifications by given criteria", e);
        }
    }

    @Autowired
    public void setNotificationSearchRepository(NotificationSearchRepository notificationSearchRepository) {
        this.notificationSearchRepository = notificationSearchRepository;
    }

    @Autowired
    public void setAppUserNotificationRepository(AppUserNotificationRepository appUserNotificationRepository) {
        this.appUserNotificationRepository = appUserNotificationRepository;
    }

    @Autowired
    public void setApplicationUserSearchRepository(ApplicationUserSearchRepository applicationUserSearchRepository) {
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    @Autowired
    public void setSuperAppUserNotificationRepository(SuperAppUserNotificationRepository superAppUserNotificationRepository) {
        this.superAppUserNotificationRepository = superAppUserNotificationRepository;
    }
}
