package com.hrplatform.recruitment.service;

import com.hrplatform.recruitment.model.Job;
import com.hrplatform.recruitment.repository.JobRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        job.setCreatedDate(LocalDateTime.now());
        job.setUpdatedDate(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public Optional<Job> getJobById(String jobId) {
        return jobRepository.findById(jobId);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByStatus(String status) {
        return jobRepository.findByStatus(status);
    }

    public List<Job> getJobsByDepartment(String department) {
        return jobRepository.findByDepartment(department);
    }

    public Job updateJob(String jobId, Job jobDetails) {
        Optional<Job> existingJob = jobRepository.findById(jobId);
        if (existingJob.isPresent()) {
            Job job = existingJob.get();
            job.setTitle(jobDetails.getTitle());
            job.setDescription(jobDetails.getDescription());
            job.setDepartment(jobDetails.getDepartment());
            job.setLocation(jobDetails.getLocation());
            job.setEmploymentType(jobDetails.getEmploymentType());
            job.setSalaryMin(jobDetails.getSalaryMin());
            job.setSalaryMax(jobDetails.getSalaryMax());
            job.setStatus(jobDetails.getStatus());
            job.setUpdatedDate(LocalDateTime.now());
            return jobRepository.save(job);
        }
        throw new RuntimeException("Job not found with id: " + jobId);
    }

    public void deleteJob(String jobId) {
        jobRepository.deleteById(jobId);
    }
}

