package com.hrplatform.recruitment.repository;

import com.hrplatform.recruitment.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    Optional<Candidate> findByEmail(String email);
    List<Candidate> findByCurrentCompany(String currentCompany);
    List<Candidate> findByYearsOfExperienceGreaterThanEqual(Integer years);
}

