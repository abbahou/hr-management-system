package com.hrplatform.recruitment.controller;

import com.hrplatform.recruitment.model.JobApplication;
import com.hrplatform.recruitment.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplication> submitApplication(@RequestBody JobApplication application) {
        JobApplication submittedApplication = jobApplicationService.submitApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(submittedApplication);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable String applicationId) {
        return jobApplicationService.getApplicationById(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        List<JobApplication> applications = jobApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByJobId(@PathVariable String jobId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByJobId(jobId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByCandidateId(@PathVariable String candidateId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByCandidateId(candidateId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplication>> getApplicationsByStatus(@PathVariable String status) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}/candidate/{candidateId}")
    public ResponseEntity<JobApplication> getApplicationByJobAndCandidate(
            @PathVariable String jobId,
            @PathVariable String candidateId) {
        return jobApplicationService.getApplicationByJobAndCandidate(jobId, candidateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplication> updateApplicationStatus(
            @PathVariable String applicationId,
            @RequestParam String status,
            @RequestParam String reviewedBy) {
        JobApplication updatedApplication = jobApplicationService.updateApplicationStatus(applicationId, status, reviewedBy);
        return ResponseEntity.ok(updatedApplication);
    }

    @PatchMapping("/{applicationId}/score")
    public ResponseEntity<JobApplication> updateApplicationScore(
            @PathVariable String applicationId,
            @RequestParam Double score) {
        JobApplication updatedApplication = jobApplicationService.updateApplicationScore(applicationId, score);
        return ResponseEntity.ok(updatedApplication);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteApplication(@PathVariable String applicationId) {
        jobApplicationService.deleteApplication(applicationId);
        return ResponseEntity.noContent().build();
    }
}

