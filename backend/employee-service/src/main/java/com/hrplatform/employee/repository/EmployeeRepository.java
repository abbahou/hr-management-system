package com.hrplatform.employee.repository;

import com.hrplatform.employee.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentId(String departmentId);
    List<Employee> findByStatus(String status);
    List<Employee> findByPosition(String position);
}

