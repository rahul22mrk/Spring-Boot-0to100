package com.gahub.server.jpa_relationships.onetomany;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Department Repository - OneToMany side
 * Simple JPA Repository - no custom queries needed for basic operations
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
