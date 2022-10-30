package com.nextlabs.destiny.console.services.policyworkflow.impl;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import com.nextlabs.destiny.console.repositories.WorkflowRequestCommentRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.WorkflowRequestCommentSearchRepository;
import com.nextlabs.destiny.console.services.policyworkflow.WorkflowRequestCommentSearchService;
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
public class WorkflowRequestCommentSearchServiceImpl implements WorkflowRequestCommentSearchService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowRequestCommentSearchServiceImpl.class);

    private WorkflowRequestCommentSearchRepository workflowRequestCommentSearchRepository;
    private WorkflowRequestCommentRepository workflowRequestCommentRepository;
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Override
    public Page<WorkflowRequestCommentLite> findWorkflowRequestCommentsByCriteria(SearchCriteria criteria) throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            Pageable pageable = PageRequest.of(criteria.getPageNo(), criteria.getPageSize());
            return findByCriteria(criteria, pageable);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while finding Workflow request comments by given criteria", e);
        }
    }

    @Override
    public void reIndexAllWorkflowRequestComments() throws ConsoleException {
        long id = 0L;
        try {
            workflowRequestCommentSearchRepository.deleteAll();

            long startTime = System.currentTimeMillis();
            Optional<List<WorkflowRequestComment>> optionalWorkflowRequestComments = workflowRequestCommentRepository.findAllByActivePolicies();
            int size = 0;
            if (optionalWorkflowRequestComments.isPresent()) {
                List<WorkflowRequestComment> workflowRequestComments = optionalWorkflowRequestComments.get();
                size = workflowRequestComments.size();
                for (WorkflowRequestComment workflowRequestComment : workflowRequestComments) {
                    id = workflowRequestComment.getId();
                    reIndexWorkflowRequestComment(workflowRequestComment);
                }
            }
            long endTime = System.currentTimeMillis();
            log.info(
                    "Workflow request comment re-indexing successful, No of re-indexes :{}, Time taken:{}ms",
                    size, (endTime - startTime));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing Workflow request comments, [ Env Id :"
                            + id + "] ",
                    e);
        }
    }

    @Override
    public void reIndexWorkflowRequestComment(WorkflowRequestComment workflowRequestComment) throws ConsoleException {
        try {
            workflowRequestCommentSearchRepository.save(WorkflowRequestCommentLite
                    .getLite(workflowRequestComment, applicationUserSearchRepository));
        } catch (Exception e){
            throw new ConsoleException(
                    "Error encountered in re-indexing Workflow request comment, [ Comment Id :"
                            + workflowRequestComment.getId() + "] ",
                    e);
        }
    }

    private Page<WorkflowRequestCommentLite> findByCriteria(SearchCriteria criteria,
                                                       Pageable pageable) throws ConsoleException {
        try {
            log.debug("Search Criteria :[{}]", criteria);
            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);
            NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable);

            Query searchQuery = withSorts(nativeQuery.build(),
                    criteria.getSortFields());

            log.debug("Workflow request comments search query :{},", query);
            Page<WorkflowRequestCommentLite> policyListPage = workflowRequestCommentSearchRepository
                    .search(searchQuery);

            log.info("Workflow request comment list page :{}, No of elements :{}",
                    policyListPage.getTotalPages(),
                    policyListPage.getNumberOfElements());
            return policyListPage;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find Workflow request comments by given criteria", e);
        }
    }

    @Autowired
    public void setWorkflowRequestCommentSearchRepository(WorkflowRequestCommentSearchRepository workflowRequestCommentSearchRepository) {
        this.workflowRequestCommentSearchRepository = workflowRequestCommentSearchRepository;
    }

    @Autowired
    public void setWorkflowRequestCommentRepository(WorkflowRequestCommentRepository workflowRequestCommentRepository) {
        this.workflowRequestCommentRepository = workflowRequestCommentRepository;
    }

    @Autowired
    public void setApplicationUserSearchRepository(ApplicationUserSearchRepository applicationUserSearchRepository) {
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }
}
