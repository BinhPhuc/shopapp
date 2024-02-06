package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 100, nullable = false)
    private String providerId;

    @Column(length = 20, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
