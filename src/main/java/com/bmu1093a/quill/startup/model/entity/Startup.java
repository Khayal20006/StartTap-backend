package com.bmu1093a.quill.startup.model.entity;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.vacancy.model.entity.Vacancy;
import com.bmu1093a.quill.startup.model.entity.enumeration.StartupCategory;
import com.bmu1093a.quill.startup.model.entity.enumeration.StartupStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tagline;
    private String description;

    @Enumerated(EnumType.STRING)
    private StartupCategory category;

    @Enumerated(EnumType.STRING)
    private StartupStage stage;
    private String website;
    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "startup", cascade = CascadeType.ALL)
    private List<Vacancy> vacancies;


    @PrePersist
    public void prePersist() {
        isActive = true;
    }


}
