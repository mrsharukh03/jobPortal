package com.jobPortal.DTO.JobSeekerDTO;

import com.jobPortal.Enums.MarriageStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonalDetailDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must be 10-12 digits")
    private String phone;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    private LocalDate dob;

    @NotNull(message = "Marriage Status is required")
    private MarriageStatus marriageStatus;

    @NotBlank(message = "Current Location is required")
    private String currentLocation;
}
