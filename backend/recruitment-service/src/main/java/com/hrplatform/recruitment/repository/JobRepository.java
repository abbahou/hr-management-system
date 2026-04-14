package com.hrplatform.recruitment.repository;

import com.hrplatform.recruitment.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByStatus(String status);
    List<Job> findByDepartment(String department);
    List<Job> findByCreatedBy(String createdBy);
}

