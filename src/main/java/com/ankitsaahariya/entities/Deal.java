package com.ankitsaahariya.entities;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jdk.jfr.Enabled;
import lombok.*;

@Enabled
@AllArgsConstructor@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer discount;

    @OneToOne
    private HomeCategory category;
}
