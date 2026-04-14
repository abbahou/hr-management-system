package com.hrplatform.recruitment.service;

import com.hrplatform.recruitment.model.Candidate;
import com.hrplatform.recruitment.repository.CandidateRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public Candidate createCandidate(Candidate candidate) {
        candidate.setCreatedDate(LocalDateTime.now());
        candidate.setUpdatedDate(LocalDateTime.now());
        return candidateRepository.save(candidate);
    }

    public Optional<Candidate> getCandidateById(String candidateId) {
        return candidateRepository.findById(candidateId);
    }

    public Optional<Candidate> getCandidateByEmail(String email) {
        return candidateRepository.findByEmail(email);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> getCandidatesByExperience(Integer yearsOfExperience) {
        return candidateRepository.findByYearsOfExperienceGreaterThanEqual(yearsOfExperience);
    }

    public Candidate updateCandidate(String candidateId, Candidate candidateDetails) {
        Optional<Candidate> existingCandidate = candidateRepository.findById(candidateId);
        if (existingCandidate.isPresent()) {
            Candidate candidate = existingCandidate.get();
            candidate.setFirstName(candidateDetails.getFirstName());
            candidate.setLastName(candidateDetails.getLastName());
            candidate.setEmail(candidateDetails.getEmail());
            candidate.setPhoneNumber(candidateDetails.getPhoneNumber());
            candidate.setResume(candidateDetails.getResume());
            candidate.setLinkedInUrl(candidateDetails.getLinkedInUrl());
            candidate.setPortfolio(candidateDetails.getPortfolio());
            candidate.setYearsOfExperience(candidateDetails.getYearsOfExperience());
            candidate.setCurrentCompany(candidateDetails.getCurrentCompany());
            candidate.setCurrentPosition(candidateDetails.getCurrentPosition());
            candidate.setUpdatedDate(LocalDateTime.now());
            return candidateRepository.save(candidate);
        }
        throw new RuntimeException("Candidate not found with id: " + candidateId);
    }

    public void deleteCandidate(String candidateId) {
        candidateRepository.deleteById(candidateId);
    }
}

