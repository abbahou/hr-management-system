package com.hrplatform.recruitment.service;

import com.hrplatform.recruitment.model.JobApplication;
import com.hrplatform.recruitment.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public JobApplication submitApplication(JobApplication application) {
        application.setAppliedDate(LocalDateTime.now());
        application.setUpdatedDate(LocalDateTime.now());
        return jobApplicationRepository.save(application);
    }

    public Optional<JobApplication> getApplicationById(String applicationId) {
        return jobApplicationRepository.findById(applicationId);
    }

    public List<JobApplication> getApplicationsByJobId(String jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    public List<JobApplication> getApplicationsByCandidateId(String candidateId) {
        return jobApplicationRepository.findByCandidateId(candidateId);
    }

    public Optional<JobApplication> getApplicationByJobAndCandidate(String jobId, String candidateId) {
        return jobApplicationRepository.findByJobIdAndCandidateId(jobId, candidateId);
    }

    public List<JobApplication> getApplicationsByStatus(String status) {
        return jobApplicationRepository.findByStatus(status);
    }

    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    public JobApplication updateApplicationStatus(String applicationId, String status, String reviewedBy) {
        Optional<JobApplication> existingApp = jobApplicationRepository.findById(applicationId);
        if (existingApp.isPresent()) {
            JobApplication application = existingApp.get();
            application.setStatus(status);
            application.setReviewedBy(reviewedBy);
            application.setReviewedDate(LocalDateTime.now());
            application.setUpdatedDate(LocalDateTime.now());
            return jobApplicationRepository.save(application);
        }
        throw new RuntimeException("Application not found with id: " + applicationId);
    }

    public JobApplication updateApplicationScore(String applicationId, Double score) {
        Optional<JobApplication> existingApp = jobApplicationRepository.findById(applicationId);
        if (existingApp.isPresent()) {
            JobApplication application = existingApp.get();
            application.setScore(score);
            application.setUpdatedDate(LocalDateTime.now());
            return jobApplicationRepository.save(application);
        }
        throw new RuntimeException("Application not found with id: " + applicationId);
    }

    public void deleteApplication(String applicationId) {
        jobApplicationRepository.deleteById(applicationId);
    }
}

