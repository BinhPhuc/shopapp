package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "tokens")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 255, nullable = false)
    private String token;

    @Column(name = "token_type", length = 50, nullable = false)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    private boolean revoked;

    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
