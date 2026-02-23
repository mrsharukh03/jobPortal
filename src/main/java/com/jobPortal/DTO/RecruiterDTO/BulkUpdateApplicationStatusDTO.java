package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BulkUpdateApplicationStatusDTO {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String recruiterNotes;

    private LocalDate interviewDate;

    @NotNull(message = "Application IDs cannot be null")
    private List<Long> applicationIds;
}