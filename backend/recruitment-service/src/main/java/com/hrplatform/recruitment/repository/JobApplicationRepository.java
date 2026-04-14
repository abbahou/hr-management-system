package com.hrplatform.recruitment.repository;

import com.hrplatform.recruitment.model.JobApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {
    List<JobApplication> findByJobId(String jobId);
    List<JobApplication> findByCandidateId(String candidateId);
    Optional<JobApplication> findByJobIdAndCandidateId(String jobId, String candidateId);
    List<JobApplication> findByStatus(String status);
}

