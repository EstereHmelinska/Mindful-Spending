package com.mindfulspending.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<SpendingPeriod> spendingPeriods = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses = new ArrayList<>();
}