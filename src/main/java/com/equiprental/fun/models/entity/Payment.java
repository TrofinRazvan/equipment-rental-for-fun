package com.equiprental.fun.models.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "amount")
    private double amount;
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(name = "payment_method")
    private String paymentMethod;
}