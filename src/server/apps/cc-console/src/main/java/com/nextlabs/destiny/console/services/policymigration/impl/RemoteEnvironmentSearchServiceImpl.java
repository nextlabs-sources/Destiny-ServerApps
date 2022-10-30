package com.nextlabs.destiny.console.services.policymigration.impl;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.policymgmt.RemoteEnvironmentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.repositories.RemoteEnvironmentRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.RemoteEnvironmentSearchRepository;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentSearchService;
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


@Service
public class RemoteEnvironmentSearchServiceImpl implements RemoteEnvironmentSearchService {

    private static final Logger log = LoggerFactory.getLogger(RemoteEnvironmentSearchServiceImpl.class);

    private RemoteEnvironmentRepository remoteEnvironmentRepository;

    private RemoteEnvironmentSearchRepository remoteEnvironmentSearchRepository;

    private ApplicationUserSearchRepository appUserSearchRepository;

    @Override
    public Page<RemoteEnvironmentLite> findAllRemoteEnvironments() {
        return null;
    }

    @Override
    public void reIndexAllRemoteEnvironments() throws ConsoleException {
        long id = 0L;
        try {
            remoteEnvironmentSearchRepository.deleteAll();

            long startTime = System.currentTimeMillis();
            Optional<List<RemoteEnvironment>> optionalRemoteEnvironments = remoteEnvironmentRepository.findByIsActiveTrue();
            int size = 0;
            if (optionalRemoteEnvironments.isPresent()) {
                List<RemoteEnvironment> remoteEnvironments = optionalRemoteEnvironments.get();
                size = remoteEnvironments.size();
                for (RemoteEnvironment remoteEnvironment : remoteEnvironments) {
                    id = remoteEnvironment.getId();
                    reIndexRemoteEnvironment(remoteEnvironment);
                }
            }
            long endTime = System.currentTimeMillis();
            log.info(
                    "Remote Environment re-indexing successfull, No of re-indexes :{}, Time taken:{}ms",
                    size, (endTime - startTime));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing Remote Environments, [ Env Id :"
                            + id + "] ",
                    e);
        }
    }

    @Override
    public Page<RemoteEnvironmentLite> findRemoteEnvironmentByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());
            return findByCriteria(criteria, pageable);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policies by given criteria", e);
        }
    }

    private Page<RemoteEnvironmentLite> findByCriteria(SearchCriteria criteria,
                                            Pageable pageable) throws ConsoleException {
        try {

            log.debug("Search Criteria :[{}]", criteria);

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);

            Query searchQuery = withSorts(nativeQuery.build(),
                    criteria.getSortFields());

            log.debug("Policy search query :{},", query);
            Page<RemoteEnvironmentLite> policyListPage = remoteEnvironmentSearchRepository
                    .search(searchQuery);

            log.info("Policy list page :{}, No of elements :{}",
                    policyListPage.getTotalPages(),
                    policyListPage.getNumberOfElements());
            return policyListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find remote environments by given criteria", e);
        }
    }

    @Override
    public void reIndexRemoteEnvironment(RemoteEnvironment entity) throws ConsoleException {
        long id = 0L;
        try {
            RemoteEnvironmentLite remoteEnvironmentLite = RemoteEnvironmentLite.getLite(entity, appUserSearchRepository);
            id = remoteEnvironmentLite.getId();
            remoteEnvironmentSearchRepository.save(remoteEnvironmentLite);
        } catch (Exception e){
            throw new ConsoleException(
                    "Error encountered in re-indexing Remote Environment, [ Env Id :"
                            + id + "] ",
                    e);
        }
    }

    @Autowired
    public void setRemoteEnvironmentRepository(RemoteEnvironmentRepository remoteEnvironmentRepository) {
        this.remoteEnvironmentRepository = remoteEnvironmentRepository;
    }

    @Autowired
    public void setRemoteEnvironmentSearchRepository(RemoteEnvironmentSearchRepository remoteEnvironmentSearchRepository) {
        this.remoteEnvironmentSearchRepository = remoteEnvironmentSearchRepository;
    }

    @Autowired
    public void setAppUserSearchRepository(ApplicationUserSearchRepository appUserSearchRepository) {
        this.appUserSearchRepository = appUserSearchRepository;
    }
}
