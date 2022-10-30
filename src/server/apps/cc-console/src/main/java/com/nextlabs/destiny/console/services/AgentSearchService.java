package com.nextlabs.destiny.console.services;

import java.util.List;
import java.util.Map;

import com.nextlabs.destiny.console.model.Agent;


/**
 * @author Sachindra Dasun
 */
public interface AgentSearchService {
    List<Agent> find(List<String> types, String value);

    Map<Long, Agent> findByIds(List<Long> ids);
}
