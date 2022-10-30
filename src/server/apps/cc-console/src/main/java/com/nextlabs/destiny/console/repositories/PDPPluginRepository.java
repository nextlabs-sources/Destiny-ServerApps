package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.enums.PDPPluginStatus;
import com.nextlabs.destiny.console.model.PDPPlugin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PDPPluginRepository extends JpaRepository<PDPPlugin, Long> {
    List<PDPPlugin> findByNameIgnoreCaseAndStatusNot(String name, PDPPluginStatus status);
    List<PDPPlugin> findByNameIgnoreCaseAndIdNotAndStatusNot(String name, Long id, PDPPluginStatus status);
    List<PDPPlugin> findByStatusNot(PDPPluginStatus status);
}
