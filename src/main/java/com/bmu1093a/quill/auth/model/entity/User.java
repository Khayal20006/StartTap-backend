package com.bmu1093a.quill.auth.model.entity;

import com.bmu1093a.quill.auth.model.enumeration.Role;
import com.bmu1093a.quill.startup.model.entity.Startup;
import com.bmu1093a.quill.vacancy.model.entity.VacancyApplication;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;


    @Column(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    //    @OneToMany(mappedBy = "employer")
//    private List<Job> createdJobs;
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Startup> startups;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VacancyApplication> applications;


}
