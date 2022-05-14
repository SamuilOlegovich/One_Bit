package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordHistory {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String password;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "player_id")
    private User user;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
