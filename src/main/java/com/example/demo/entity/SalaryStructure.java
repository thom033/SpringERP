package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalaryStructure")
public class SalaryStructure {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "company")
    private String company;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "is_default")
    private String isDefault;

    @Column(name = "currency")
    private String currency;

    @Column(name = "docstatus")
    private String docstatus;

    public SalaryStructure() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }

    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDocstatus() { return docstatus; }
    public void setDocstatus(String docstatus) { this.docstatus = docstatus; }
}
