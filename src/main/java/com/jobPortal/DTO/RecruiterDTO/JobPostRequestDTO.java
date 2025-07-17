package com.jobPortal.DTO.RecruiterDTO;
import com.jobPortal.Enums.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class JobPostRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Job type is required")
    private JobType type;

    @NotBlank(message = "Category is required")
    private String category;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private int salary;

    @NotBlank(message = "Experience required field is mandatory")
    private String experienceRequired;

    @Future(message = "Last date to apply must be in the future")
    private Date lastDateToApply;

    @NotEmpty(message = "At least one skill must be selected")
    private List<Long> skills;
}
