package com.nextlabs.destiny.console.services.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.dao.AgentDao;
import com.nextlabs.destiny.console.model.Agent;
import com.nextlabs.destiny.console.services.AgentSearchService;

/**
 * @author Sachindra Dasun
 */
@Service
public class AgentSearchServiceImpl implements AgentSearchService {

    @Autowired
    private AgentDao agentDao;

    @Override
    public List<Agent> find(List<String> types, String value) {
        return agentDao.find(types, value);
    }

    @Override
    public Map<Long, Agent> findByIds(List<Long> ids) {
        List<Agent> agents = agentDao.findByIds(ids);
        if (!CollectionUtils.isEmpty(agents)) {
            return agents.stream().collect(Collectors.toMap(Agent::getId, Function.identity()));
        }
        return null;
    }
}
