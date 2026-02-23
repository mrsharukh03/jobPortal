package com.jobPortal.DTO.RecruiterDTO;

import com.jobPortal.Enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateApplicationStatusDTO {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String recruiterNotes;

    private LocalDate interviewDate;
}
