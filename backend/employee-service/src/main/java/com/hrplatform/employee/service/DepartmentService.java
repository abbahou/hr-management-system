package com.hrplatform.employee.service;

import com.hrplatform.employee.model.Department;
import com.hrplatform.employee.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {
        department.setCreatedDate(LocalDateTime.now());
        department.setUpdatedDate(LocalDateTime.now());
        return departmentRepository.save(department);
    }

    public Optional<Department> getDepartmentById(String departmentId) {
        return departmentRepository.findById(departmentId);
    }

    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getDepartmentsByStatus(String status) {
        return departmentRepository.findByStatus(status);
    }

    public List<Department> getDepartmentsByLocation(String location) {
        return departmentRepository.findByLocation(location);
    }

    public Department updateDepartment(String departmentId, Department departmentDetails) {
        Optional<Department> existingDepartment = departmentRepository.findById(departmentId);
        if (existingDepartment.isPresent()) {
            Department department = existingDepartment.get();
            department.setName(departmentDetails.getName());
            department.setDescription(departmentDetails.getDescription());
            department.setLocation(departmentDetails.getLocation());
            department.setManagerId(departmentDetails.getManagerId());
            department.setBudgetAllocated(departmentDetails.getBudgetAllocated());
            department.setStatus(departmentDetails.getStatus());
            department.setUpdatedDate(LocalDateTime.now());
            return departmentRepository.save(department);
        }
        throw new RuntimeException("Department not found with id: " + departmentId);
    }

    public void deleteDepartment(String departmentId) {
        departmentRepository.deleteById(departmentId);
    }
}

