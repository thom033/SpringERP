package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalaryDetail")
public class SalaryDetail {

    @Id
    @Column(name = "salary_component")
    private String salaryComponent;

    @Column(name = "amount")
    private double amount;

    public SalaryDetail() {}

    public String getSalaryComponent() { return salaryComponent; }
    public void setSalaryComponent(String salaryComponent) { this.salaryComponent = salaryComponent; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
