package com.mindfulspending.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class SpendingPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spendingPeriodId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column
    private BigDecimal budgetAmount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "spendingPeriod")
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "spendingPeriod")
    private List<CategoryAllowance> categoryAllowances = new ArrayList<>();
}