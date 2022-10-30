package com.nextlabs.destiny.configservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextlabs.destiny.configservice.entities.SecureStore;

/**
 * Secure store repository.
 *
 * @author Sachindra Dasun
 */
public interface SecureStoreRepository extends JpaRepository<SecureStore, Long> {

}
