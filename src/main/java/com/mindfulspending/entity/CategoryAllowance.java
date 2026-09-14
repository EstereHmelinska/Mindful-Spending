package com.mindfulspending.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"category_id", "spending_period_id"})})
public class CategoryAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryAllowanceId;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "spending_period_id", nullable = false)
    private SpendingPeriod spendingPeriod;
}