package com.hrplatform.employee.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "departments")
public class Department {

    @Id
    private String departmentId;
    private String name;
    private String description;
    private String location;
    private String managerId;
    private Integer budgetAllocated;
    private String status; // ACTIVE, INACTIVE
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Department() {
    }

    public Department(String name, String description, String location) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = "ACTIVE";
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public Integer getBudgetAllocated() { return budgetAllocated; }
    public void setBudgetAllocated(Integer budgetAllocated) { this.budgetAllocated = budgetAllocated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}

