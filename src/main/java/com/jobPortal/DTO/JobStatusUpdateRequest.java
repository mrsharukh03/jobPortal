package com.jobPortal.DTO;

import com.jobPortal.Enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusUpdateRequest {

    @NotNull(message = "Job ID must not be null")
    private Long jobId;

    @NotNull(message = "Job status must not be null")
    private JobStatus jobStatus;
}
