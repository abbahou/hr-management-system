package com.hrplatform.employee.service;

import com.hrplatform.employee.model.Employee;
import com.hrplatform.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        employee.setCreatedDate(LocalDateTime.now());
        employee.setUpdatedDate(LocalDateTime.now());
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByDepartment(String departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public List<Employee> getEmployeesByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }

    public Employee updateEmployee(String employeeId, Employee employeeDetails) {
        Optional<Employee> existingEmployee = employeeRepository.findById(employeeId);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
            employee.setFirstName(employeeDetails.getFirstName());
            employee.setLastName(employeeDetails.getLastName());
            employee.setEmail(employeeDetails.getEmail());
            employee.setPhoneNumber(employeeDetails.getPhoneNumber());
            employee.setDepartmentId(employeeDetails.getDepartmentId());
            employee.setPosition(employeeDetails.getPosition());
            employee.setEmploymentType(employeeDetails.getEmploymentType());
            employee.setSalary(employeeDetails.getSalary());
            employee.setHireDate(employeeDetails.getHireDate());
            employee.setDateOfBirth(employeeDetails.getDateOfBirth());
            employee.setAddress(employeeDetails.getAddress());
            employee.setCity(employeeDetails.getCity());
            employee.setState(employeeDetails.getState());
            employee.setCountry(employeeDetails.getCountry());
            employee.setPostalCode(employeeDetails.getPostalCode());
            employee.setStatus(employeeDetails.getStatus());
            employee.setUpdatedDate(LocalDateTime.now());
            return employeeRepository.save(employee);
        }
        throw new RuntimeException("Employee not found with id: " + employeeId);
    }

    public void deleteEmployee(String employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}

