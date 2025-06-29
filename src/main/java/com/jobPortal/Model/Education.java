package com.jobPortal.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;           // e.g., B.Tech, MBA
    private String fieldOfStudy;     // e.g., Computer Science, Finance
    private String collegeName;
    private String country;

    private int startYear;
    private int endYear;
    private String gradeType;        // e.g., CGPA, Percentage
    private String gradeValue;       // e.g., 8.2, or 78%

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
