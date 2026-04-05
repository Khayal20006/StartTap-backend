package com.bmu1093a.quill.job.model.entity;

import com.bmu1093a.quill.auth.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private User employer;

    private LocalDateTime createdAt;

    private Boolean isActive;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}