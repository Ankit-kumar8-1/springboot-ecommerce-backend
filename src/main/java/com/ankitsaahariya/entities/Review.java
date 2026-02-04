package com.ankitsaahariya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reviewText;

    @Column(nullable = false)
    private String rating;

    @ElementCollection
    private List<String> productImage;

    @JsonIgnore
    @ManyToOne
    @Column(nullable = false)
    private Product product;

    @ManyToOne
    @Column(nullable = false)
    private UserEntity user;


    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
