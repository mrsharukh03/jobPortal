package com.jobPortal.Model.Seeker;

import com.jobPortal.Model.Users.Seeker;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String jobTitle;
    private String companyName;
    private Date startDate;
    private Date endDate;
    private String location;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "seeker_id")
    private Seeker seeker;
}
