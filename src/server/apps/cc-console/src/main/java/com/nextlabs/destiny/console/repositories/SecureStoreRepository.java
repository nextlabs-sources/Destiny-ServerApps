package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.SecureStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecureStoreRepository extends JpaRepository<SecureStore, Long> {

    Optional<SecureStore> findByName(String fileName);

}
