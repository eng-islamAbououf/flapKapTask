package com.flapkap.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    private String username;
    private String password;

    @Column(nullable = false)
    private Integer deposit = 0; // in cents

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private boolean suspended = false;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}