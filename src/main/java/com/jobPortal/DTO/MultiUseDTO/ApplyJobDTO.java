package com.jobPortal.DTO.MultiUseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJobDTO {

    @NotNull(message = "Job ID is required.")
    @Positive(message = "Job ID must be a positive number.")
    private Long jobId;

    @NotBlank(message = "Resume URL is required.")
    @Pattern(regexp = "^(http|https)://.*$", message = "Resume URL must be a valid URL.")
    private String resumeUrl;

    @Size(max = 1000, message = "Cover letter must not exceed 1000 characters.")
    private String coverLetter;
}
