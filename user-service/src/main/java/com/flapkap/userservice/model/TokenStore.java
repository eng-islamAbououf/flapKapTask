package com.flapkap.userservice.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TokenStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long id;

    @Column
    private String token;
    @Column
    private Long userId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expired_date")
    private Date expiredDate;
    @Column
    private Boolean notUsed;


}