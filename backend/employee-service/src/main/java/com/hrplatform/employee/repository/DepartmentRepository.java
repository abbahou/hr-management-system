package com.hrplatform.employee.repository;

import com.hrplatform.employee.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Optional<Department> findByName(String name);
    List<Department> findByStatus(String status);
    List<Department> findByLocation(String location);
}

