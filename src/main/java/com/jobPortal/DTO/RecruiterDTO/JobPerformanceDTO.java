package com.jobPortal.DTO.RecruiterDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobPerformanceDTO {

    private Long jobId;
    private String jobTitle;
    private String status;

    private long views;
    private long applications;
    private long shortlisted;
    private long interviews;
    private long selected;

    private double conversionRate;      // selected / applications
    private double avgAiScore;
}