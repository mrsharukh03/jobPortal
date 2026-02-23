package com.jobPortal.Mapper;

import com.jobPortal.DTO.JobSearchFilterDTO;
import com.jobPortal.Enums.JobStatus;
import com.jobPortal.Model.JobPost;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    /**
     * Public visible jobs (for seeker / public API)
     * Only active, approved, open, not expired
     */
    public static Specification<JobPost> publicVisible() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("isActive"), true),
                cb.equal(root.get("adminApproved"), true),
                cb.equal(root.get("status"), JobStatus.OPEN),
                cb.or(
                        cb.isNull(root.get("lastDateToApply")),
                        cb.greaterThanOrEqualTo(root.get("lastDateToApply"), LocalDate.now())
                )
        );
    }

    /**
     * Full search with filters
     */
    public static Specification<JobPost> searchJobs(JobSearchFilterDTO filter) {
        return publicVisible().and((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            // Keyword search
            if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("title")), pattern),
                                cb.like(cb.lower(root.get("description")), pattern),
                                cb.like(cb.lower(root.get("companyName")), pattern)
                        )
                );
            }

            // Location
            if (filter.getLocation() != null && !filter.getLocation().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + filter.getLocation().trim().toLowerCase() + "%"));
            }

            // Category (partial match, case-insensitive)
            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("category")),
                        "%" + filter.getCategory().trim().toLowerCase() + "%"));
            }

            // Type
            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            // Salary overlap logic
            Integer min = filter.getMinSalary();
            Integer max = filter.getMaxSalary();

            if (min != null && max != null) {
                if (min > max) {
                    throw new IllegalArgumentException("Min salary cannot be greater than max salary");
                }
                predicates.add(cb.and(
                        cb.greaterThanOrEqualTo(root.get("maxSalary"), min),
                        cb.lessThanOrEqualTo(root.get("minSalary"), max)
                ));
            } else if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxSalary"), min));
            } else if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minSalary"), max));
            }

            // Experience required
            if (filter.getExperienceRequired() != null && !filter.getExperienceRequired().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("experienceRequired"), filter.getExperienceRequired().trim()));
            }

            // Featured
            if (filter.getFeatured() != null) {
                predicates.add(cb.equal(root.get("featured"), filter.getFeatured()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * Jobs for recruiter (all jobs by email)
     */
    public static Specification<JobPost> recruiterJobs(String email) {
        return (root, query, cb) ->
                cb.equal(root.get("recruiter").get("user").get("email"), email);
    }

}