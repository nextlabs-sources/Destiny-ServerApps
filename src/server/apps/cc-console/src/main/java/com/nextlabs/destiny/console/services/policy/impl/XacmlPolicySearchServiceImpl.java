package com.nextlabs.destiny.console.services.policy.impl;

import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.XacmlPolicySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.services.policy.XacmlPolicySearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.nextlabs.destiny.console.enums.DevEntityType.XACML_POLICY;

/**
 *
 * Policy Search Criteria Service implementation
 *
 * @author Mohammed Sainal Shah
 * @since 9.5
 *
 */
@Service
public class XacmlPolicySearchServiceImpl implements XacmlPolicySearchService {

    private static final Logger log = LoggerFactory.getLogger(XacmlPolicySearchServiceImpl.class);

    @Resource
    XacmlPolicySearchRepository xacmlPolicySearchRepository;

    @Resource
    private ApplicationUserSearchRepository appUserSearchRepository;

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Override
    public Page<XacmlPolicyLite> findAllXacmlPolicies() {
        return xacmlPolicySearchRepository.findAll(Pageable.unpaged());
    }

    @Override
    public void reIndexAllXacmlPolicies() throws ConsoleException {
        long id = 0l;
        try {
            xacmlPolicySearchRepository.deleteAll();

            long startTime = System.currentTimeMillis();
            List<PolicyDevelopmentEntity> devEntities = policyDevelopmentEntityDao
                    .findActiveRecordsByType(XACML_POLICY.getKey());

            for (PolicyDevelopmentEntity devEntity : devEntities) {
                id = devEntity.getId();
                reIndexXacmlPolicy(devEntity);
            }
            long endTime = System.currentTimeMillis();
            log.info(
                    "Xacml Policy re-indexing successfull, No of re-indexes :{}, Time taken:{}ms",
                    devEntities.size(), (endTime - startTime));
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in re-indexing Xacml Policies, [ Policy Id :"
                            + id + "] ",
                    e);
        }
    }

    @Override
    public void reIndexXacmlPolicy(PolicyDevelopmentEntity entity) throws ConsoleException {
        long id = 0L;
        try {
            XacmlPolicyLite xacmlPolicyLite = XacmlPolicyLite.getLite(entity, appUserSearchRepository);
            id = xacmlPolicyLite.getId();
            xacmlPolicySearchRepository.save(xacmlPolicyLite);
        } catch (Exception e){
            throw new ConsoleException(
                    "Error encountered in re-indexing Xacml Policies, [ Policy Id :"
                            + id + "] ",
                    e);
        }
    }
}
