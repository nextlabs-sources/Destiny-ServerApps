package com.nextlabs.destiny.console.services.policyworkflow.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.policyworkflow.WorkflowRequestCommentLite;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.EntityWorkflowRequestStatus;
import com.nextlabs.destiny.console.enums.NotificationType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.WorkflowRequestLevelStatus;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.notification.Notification;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policyworkflow.EntityWorkflowRequest;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowLevel;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestComment;
import com.nextlabs.destiny.console.model.policyworkflow.WorkflowRequestLevel;
import com.nextlabs.destiny.console.repositories.EntityWorkflowRequestRepository;
import com.nextlabs.destiny.console.repositories.WorkflowLevelRepository;
import com.nextlabs.destiny.console.repositories.WorkflowRequestCommentRepository;
import com.nextlabs.destiny.console.repositories.WorkflowRequestLevelRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.search.repositories.WorkflowRequestCommentSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.notification.NotificationService;
import com.nextlabs.destiny.console.services.notification.impl.NotificationServiceImpl;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.services.policy.impl.PolicyMgmtServiceImpl;
import com.nextlabs.destiny.console.services.policyworkflow.PolicyWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.nextlabs.destiny.console.services.notification.impl.NotificationServiceImpl.getNotificationRecipients;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUserId;

@Service
public class PolicyWorkflowServiceImpl implements PolicyWorkflowService {

    private EntityWorkflowRequestRepository entityWorkflowRequestRepository;
    private WorkflowRequestCommentSearchRepository workflowRequestCommentSearchRepository;
    private ApplicationUserSearchRepository applicationUserSearchRepository;
    private WorkflowRequestCommentRepository workflowRequestCommentRepository;
    private EntityAuditLogDao entityAuditLogDao;
    private PolicySearchRepository policySearchRepository;
    private WorkflowRequestLevelRepository workflowRequestLevelRepository;
    private NotificationService notificationService;
    private MessageBundleService msgBundle;
    private PolicySearchService policySearchService;
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;
    private WorkflowLevelRepository workflowLevelRepository;
    private PolicyMgmtServiceImpl policyMgmtService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitWorkflowNested(Long policyId) throws ConsoleException, ServerException {
        List<PolicyLite> draftSubPolicies = new ArrayList<>();
        getNestedDraftSubPolicies(draftSubPolicies, policyId);
        for (PolicyLite policyLite: draftSubPolicies) {
            submitWorkflow(policyLite.getId());
        }
        submitWorkflow(policyId);
    }

    private void submitWorkflow(Long policyId) throws ServerException, ConsoleException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(policyId);
        if (devEntity.getActiveWorkflowRequest() == null) {
            EntityWorkflowRequest entityWorkflowRequest = new EntityWorkflowRequest();
            entityWorkflowRequest.setOldPql(devEntity.getApprovedPql());
            entityWorkflowRequest.setUpdatedPQL(devEntity.getPql());
            entityWorkflowRequest.setStatus(EntityWorkflowRequestStatus.PENDING);
            entityWorkflowRequest.setDevEntityType(DevEntityType.POLICY);
            WorkflowRequestLevel workflowRequestLevel = getNextWorkflowRequestLevel(null);
            entityWorkflowRequest.getRequestLevels().add(workflowRequestLevel);
            entityWorkflowRequest.setActiveWorkflowRequestLevel(workflowRequestLevel);
            devEntity.getEntityWorkflowRequests().add(entityWorkflowRequest);
            devEntity.setActiveWorkflowRequest(entityWorkflowRequest);
        } else {
            EntityWorkflowRequest entityWorkflowRequest = devEntity.getActiveWorkflowRequest();
            WorkflowRequestLevel workflowRequestLevel = entityWorkflowRequest.getActiveWorkflowRequestLevel();
            if (workflowRequestLevel.getStatus() != WorkflowRequestLevelStatus.REQUESTED_AMENDMENT) {
                throw new ServerException("Invalid policy workflow state - A policy workflow in REQUESTED_AMENDMENT status expected");
            }
            workflowRequestLevel.setStatus(WorkflowRequestLevelStatus.PENDING);
        }

        devEntityMgmtService.save(devEntity);
        sendWorkflowNotification(devEntity, 1);
        PolicyDTO policy = policyMgmtService.findById(devEntity.getId());

        entityAuditLogDao.addEntityAuditLog(AuditAction.SUBMIT_FOR_REVIEW,
                DevEntityType.DELEGATION_POLICY.equals(policy.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(),
                devEntity.getId(), null, policy.toAuditString());
        policySearchService.reIndexPolicies(policy);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approveWorkflowRequestLevel(Long policyId) throws ConsoleException, ServerException {
        if (policyMgmtService.isSubPolicyNotApproved(policyId)) {
            throw new ConsoleException("Sub policies have to be approved first in order to approve the parent policy");
        }
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(policyId);
        EntityWorkflowRequest entityWorkflowRequest = devEntity.getActiveWorkflowRequest();
        if (entityWorkflowRequest == null) {
            throw new ConsoleException("An active policy workflow request doesn't exist");
        }
        WorkflowRequestLevel workflowRequestLevel = entityWorkflowRequest.getActiveWorkflowRequestLevel();
        validateWorkflowLevelAccess(workflowRequestLevel.getWorkflowLevel().getLevelOrder());

        if(workflowRequestLevel.getStatus() != WorkflowRequestLevelStatus.PENDING) {
            throw new ServerException("Invalid policy workflow state - Only policy workflows in pending status could be approved");
        }

        workflowRequestLevel.setStatus(WorkflowRequestLevelStatus.APPROVED);
        workflowRequestLevel.setApprovedBy(getCurrentUserId());
        workflowRequestLevel.setApprovedTime(new Date());
        if (workflowRequestLevel.getWorkflowLevel().getLevelOrder() == workflowLevelRepository.count()) {
            entityWorkflowRequest.setStatus(EntityWorkflowRequestStatus.APPROVED);
            devEntity.setApprovedPql(devEntity.getPql());
        } else {
            WorkflowRequestLevel nextWorkflowRequestLevel = getNextWorkflowRequestLevel(workflowRequestLevel.getWorkflowLevel().getLevelOrder());
            entityWorkflowRequest.getRequestLevels().add(nextWorkflowRequestLevel);
            entityWorkflowRequest.setActiveWorkflowRequestLevel(nextWorkflowRequestLevel);
            devEntity.setActiveWorkflowRequest(entityWorkflowRequest);
        }
        devEntityMgmtService.save(devEntity);
        sendWorkflowNotification(devEntity, workflowRequestLevel.getWorkflowLevel().getLevelOrder());
        PolicyDTO policyDTO = policyMgmtService.findById(devEntity.getId());
        entityAuditLogDao.addEntityAuditLog(AuditAction.APPROVE,
                DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(),
                devEntity.getId(), null, policyDTO.toAuditString());
        policySearchService.reIndexPolicies(policyDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void returnWorkflowRequestLevel(Long policyId) throws ConsoleException, ServerException {
        PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(policyId);
        EntityWorkflowRequest entityWorkflowRequest = devEntity.getActiveWorkflowRequest();
        if (entityWorkflowRequest == null) {
            throw new ConsoleException("An active policy workflow request doesn't exist");
        } else if (entityWorkflowRequest.getStatus() != EntityWorkflowRequestStatus.PENDING) {
            throw new ConsoleException("Only the workflow requests in pending status can be returned for amendment");
        }
        WorkflowRequestLevel workflowRequestLevel = entityWorkflowRequest.getActiveWorkflowRequestLevel();
        validateWorkflowLevelAccess(workflowRequestLevel.getWorkflowLevel().getLevelOrder());
        workflowRequestLevel.setStatus(WorkflowRequestLevelStatus.REQUESTED_AMENDMENT);
        devEntityMgmtService.save(devEntity);
        sendWorkflowNotification(devEntity, workflowRequestLevel.getWorkflowLevel().getLevelOrder());
        PolicyDTO policyDTO = policyMgmtService.findById(devEntity.getId());
        entityAuditLogDao.addEntityAuditLog(AuditAction.RETURN_TO_AUTHOR,
                DevEntityType.DELEGATION_POLICY.equals(policyDTO.getCategory()) ? AuditableEntity.DELEGATE_ADMIN.getCode() : AuditableEntity.POLICY.getCode(),
                devEntity.getId(), null, policyDTO.toAuditString());
        policySearchService.reIndexPolicies(policyDTO);
    }

    private void validateWorkflowLevelAccess(int levelOrder) throws ServerException {
        PrincipalUser principal = getCurrentUser();
        String requiredAuthority = String.format(DelegationModelActions.MANAGE_POLICY_WORKFLOW_LEVEL, levelOrder);
        if(principal.getAuthorities()
                .parallelStream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredAuthority))) {
            throw new ServerException("Insufficient permissions for the user to perform the policy workflow action");
        }
    }

    private void sendWorkflowNotification(PolicyDevelopmentEntity entity, int levelOrder) throws ConsoleException, ServerException {
        EntityWorkflowRequest entityWorkflowRequest = entityWorkflowRequestRepository.findActiveEntityWorkflowRequest(entity.getId());
        WorkflowRequestLevel workflowRequestLevel = workflowRequestLevelRepository
                .findByEntityWorkflowRequestIdAndStatusIn(entityWorkflowRequest.getId(),
                        WorkflowRequestLevelStatus.PENDING,
                        WorkflowRequestLevelStatus.REQUESTED_AMENDMENT);
        String message = null;
        Long initiatedBy = null;
        if (entityWorkflowRequest.getStatus() == EntityWorkflowRequestStatus.APPROVED && workflowRequestLevel == null) {
            workflowRequestLevel = workflowRequestLevelRepository
                    .findByEntityWorkflowRequestIdAndStatusIn(entityWorkflowRequest.getId(),
                            WorkflowRequestLevelStatus.APPROVED);
        }
        switch (workflowRequestLevel.getStatus()) {
            case PENDING: {
                message = msgBundle.getText("notification.policy.workflow.review.pending", entity.getNameFromTitle());
                initiatedBy = entityWorkflowRequest.getOwnerId();
                break;
            }
            case APPROVED: {
                message = msgBundle.getText("notification.policy.workflow.review.approved", entity.getNameFromTitle());
                initiatedBy = workflowRequestLevel.getApprovedBy();
                break;
            }
            case REQUESTED_AMENDMENT: {
                message = msgBundle.getText("notification.policy.workflow.review.returned", entity.getNameFromTitle());
                initiatedBy = workflowRequestLevel.getLastUpdatedBy();
                break;
            }
        }
        Notification notification = new Notification();
        notification.setContent(message);
        notification.setEntityId(entity.getId());
        notification.setNotificationType(NotificationType.CONSOLE);
        notification.setDevEntityType(DevEntityType.POLICY);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("policyName", entity.getNameFromTitle());
        metadata.put("workflowStatus", workflowRequestLevel.getStatus().name());
        notification.setMetadata(new Gson().toJson(metadata, new TypeToken<HashMap<String, String>>(){}.getType()));
        String requiredAuthority = String.format(DelegationModelActions.MANAGE_POLICY_WORKFLOW_LEVEL, levelOrder);
        notificationService.saveAndNotifyUser(
                NotificationServiceImpl.getNotificationRecipients(requiredAuthority, initiatedBy, entityWorkflowRequest.getOwnerId(), applicationUserSearchRepository),
                notification);
    }

    private void sendNotification(EntityWorkflowRequest workflowRequest, PolicyLite policyLite) throws ServerException, ConsoleException {
        PrincipalUser principalUser = getCurrentUser();
        String message = principalUser.getDisplayName() + " commented on Policy " + policyLite.getName();
        Notification notification = new Notification();
        notification.setContent(message);
        notification.setEntityId(workflowRequest.getDevelopmentId());
        notification.setNotificationType(NotificationType.CONSOLE);
        notification.setDevEntityType(DevEntityType.POLICY);

        WorkflowRequestLevel workflowRequestLevel = workflowRequestLevelRepository.findByEntityWorkflowRequestId(workflowRequest.getId());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("policyName", policyLite.getName());
        metadata.put("workflowStatus", workflowRequestLevel.getStatus().name());
        notification.setMetadata(new Gson().toJson(metadata, new TypeToken<HashMap<String, String>>(){}.getType()));
        String requiredAuthority = String.format(DelegationModelActions.MANAGE_POLICY_WORKFLOW_LEVEL, workflowRequestLevel.getWorkflowLevel().getLevelOrder());
        notificationService.saveAndNotifyUser(
                getNotificationRecipients(requiredAuthority, principalUser.getUserId(), workflowRequest.getOwnerId(), applicationUserSearchRepository),
                notification);
    }

    private WorkflowRequestLevel getNextWorkflowRequestLevel(Integer levelOrder) {
        WorkflowRequestLevel workflowRequestLevel = new WorkflowRequestLevel();
        List<WorkflowLevel> workflowLevels = workflowLevelRepository.findAllByOrderByLevelOrderAsc();

        workflowRequestLevel.setStatus(WorkflowRequestLevelStatus.PENDING);
        if (levelOrder == null){
            workflowRequestLevel.setWorkflowLevel(workflowLevels.get(0));
        } else if (levelOrder < workflowLevels.size() - 1){
            workflowRequestLevel.setWorkflowLevel(workflowLevels.get(levelOrder + 1));
        } else {
            return null;
        }
        return workflowRequestLevel;
    }

    @Transactional
    @Override
    public void addComment(WorkflowRequestCommentDTO workflowRequestCommentDTO) throws ConsoleException, ServerException {
        EntityWorkflowRequest entityWorkflowRequest = entityWorkflowRequestRepository
                .findById(workflowRequestCommentDTO.getWorkflowRequestId())
                .orElseThrow(() ->
                        new ConsoleException(String.format("Unable to find Workflow request with ID %d",
                                workflowRequestCommentDTO.getWorkflowRequestId())));

        WorkflowRequestComment parentComment = null;
        if (workflowRequestCommentDTO.getParentCommentId() != null) {
            Optional<WorkflowRequestComment> optionalParentComment = workflowRequestCommentRepository
                    .findById(workflowRequestCommentDTO.getParentCommentId());
            parentComment = optionalParentComment.orElseThrow(() ->
                    new ConsoleException(String.format("Unable to find Workflow request comment with ID %d",
                            workflowRequestCommentDTO.getParentCommentId())));
        }
        PolicyLite policyLite = policySearchRepository.findById(entityWorkflowRequest.getDevelopmentId())
                .orElseThrow(() -> new ServerException(String.format("Indexed policy not found, policy id: %d", entityWorkflowRequest.getDevelopmentId())));

        WorkflowRequestComment workflowRequestComment = WorkflowRequestCommentDTO.setEntityValues(workflowRequestCommentDTO,
                new WorkflowRequestComment(), parentComment);
        workflowRequestComment = workflowRequestCommentRepository.save(workflowRequestComment);
        workflowRequestCommentDTO.setId(workflowRequestComment.getId());
        sendNotification(entityWorkflowRequest, policyLite);
        entityAuditLogDao.addEntityAuditLog(AuditAction.COMMENT,
                AuditableEntity.POLICY.getCode(),
                workflowRequestCommentDTO.getId(), null, workflowRequestCommentDTO.toAuditString(policyLite));
        workflowRequestCommentSearchRepository.save(WorkflowRequestCommentLite.getLite(workflowRequestComment, applicationUserSearchRepository));
    }

    public void getNestedDraftSubPolicies(List<PolicyLite> policyLites, Long parentPolicyId) throws ConsoleException {
        List<PolicyLite> subPolicies = policySearchService.findSubPolicy(parentPolicyId);
        for (PolicyLite policyLite: subPolicies) {
            if (policyLite.getStatus().equalsIgnoreCase(PolicyStatus.DRAFT.name()) && policyLite.getActiveWorkflowRequestLevelStatus() == null) {
                policyLites.add(policyLite);
            }
            getNestedDraftSubPolicies(policyLites, policyLite.getId());
        }
    }

    @Autowired
    public void setEntityWorkflowRequestRepository(EntityWorkflowRequestRepository entityWorkflowRequestRepository) {
        this.entityWorkflowRequestRepository = entityWorkflowRequestRepository;
    }

    @Autowired
    public void setWorkflowRequestCommentRepository(WorkflowRequestCommentRepository workflowRequestCommentRepository) {
        this.workflowRequestCommentRepository = workflowRequestCommentRepository;
    }

    @Autowired
    public void setWorkflowRequestCommentSearchRepository(WorkflowRequestCommentSearchRepository workflowRequestCommentSearchRepository) {
        this.workflowRequestCommentSearchRepository = workflowRequestCommentSearchRepository;
    }

    @Autowired
    public void setApplicationUserSearchRepository(ApplicationUserSearchRepository applicationUserSearchRepository) {
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    @Autowired
    public void setEntityAuditLogDao(EntityAuditLogDao entityAuditLogDao) {
        this.entityAuditLogDao = entityAuditLogDao;
    }

    @Autowired
    public void setPolicySearchRepository(PolicySearchRepository policySearchRepository) {
        this.policySearchRepository = policySearchRepository;
    }

    @Autowired
    public void setWorkflowRequestLevelRepository(WorkflowRequestLevelRepository workflowRequestLevelRepository) {
        this.workflowRequestLevelRepository = workflowRequestLevelRepository;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    @Autowired
    public void setPolicySearchService(PolicySearchService policySearchService) {
        this.policySearchService = policySearchService;
    }

    @Autowired
    public void setDevEntityMgmtService(PolicyDevelopmentEntityMgmtService devEntityMgmtService) {
        this.devEntityMgmtService = devEntityMgmtService;
    }

    @Autowired
    public void setWorkflowLevelRepository(WorkflowLevelRepository workflowLevelRepository) {
        this.workflowLevelRepository = workflowLevelRepository;
    }

    @Autowired
    public void setPolicyMgmtService(PolicyMgmtServiceImpl policyMgmtService) {
        this.policyMgmtService = policyMgmtService;
    }
}
