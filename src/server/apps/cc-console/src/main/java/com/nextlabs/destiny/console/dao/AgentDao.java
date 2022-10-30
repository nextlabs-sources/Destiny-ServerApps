package com.nextlabs.destiny.console.dao;

import java.util.List;

import com.nextlabs.destiny.console.model.Agent;

/**
 * @author Sachindra Dasun
 */
public interface AgentDao extends GenericDao<Agent, Long> {

    List<Agent> find(List<String> types, String value);

    List<Agent> findByIds(List<Long> ids);

}
