package com.gahub.server.jpa_relationships.onetomany;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Employee Repository - ManyToOne side
 * Custom queries to find employees by department
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Find all employees in a department (Spring Data JPA auto-generates query!)
    List<Employee> findByDepartment(Department department);

    // Find by department id (another way)
    List<Employee> findByDepartmentId(Long departmentId);
}
