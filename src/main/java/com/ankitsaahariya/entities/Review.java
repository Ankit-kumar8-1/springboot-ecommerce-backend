package com.ankitsaahariya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private Product product;

    @ManyToOne
    private UserEntity user;


    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
