package com.mindfulspending.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToMany(mappedBy = "category")
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<CategoryAllowance> categoryAllowances = new ArrayList<>();
}