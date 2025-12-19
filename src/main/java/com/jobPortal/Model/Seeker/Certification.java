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
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String issuingOrganization;
    private Date issueDate;
    private Date expiryDate;
    private String credentialUrl;

    @ManyToOne
    private Seeker seeker;
}
