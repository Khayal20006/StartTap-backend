package com.bmu1093a.quill.vacancy.model.entity;

import com.bmu1093a.quill.startup.model.entity.Startup;
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
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private BigDecimal salary;

//    @ManyToOne
//    @JoinColumn(name = "employer_id")
//    private User employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id")
    private Startup startup;

    private LocalDateTime createdAt;

    private Boolean isActive;

    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancyApplication> applications;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}