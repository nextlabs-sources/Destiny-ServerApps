package com.nextlabs.destiny.console.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

    Optional<Component> findByType(String type);

}
