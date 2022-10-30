package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorConfigRepository extends JpaRepository<OperatorConfig, Long> {

}
