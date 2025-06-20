package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalaryComponentAccount")
public class SalaryComponentAccount {

    @Id
    @Column(name = "account")
    private String account;

    @Column(name = "company")
    private String company;

    public SalaryComponentAccount() {}

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
