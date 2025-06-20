package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalaryComponent")
public class SalaryComponent {

    @Id
    @Column(name = "salary_component")
    private String salaryComponent;

    @Column(name = "salary_component_abbr")
    private String salaryComponentAbbr;

    @Column(name = "type")
    private String type;

    @Column(name = "formula")
    private String formula;

    @Column(name = "amount_based_on_formula")
    private String amountBasedOnFormula;

    @Column(name = "depends_on_payment_days")
    private String dependsOnPaymentDays;

    @Column(name = "company")
    private String company;

    public SalaryComponent() {}

    public String getSalaryComponent() { return salaryComponent; }
    public void setSalaryComponent(String salaryComponent) { this.salaryComponent = salaryComponent; }

    public String getSalaryComponentAbbr() { return salaryComponentAbbr; }
    public void setSalaryComponentAbbr(String salaryComponentAbbr) { this.salaryComponentAbbr = salaryComponentAbbr; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }

    public String getAmountBasedOnFormula() { return amountBasedOnFormula; }
    public void setAmountBasedOnFormula(String amountBasedOnFormula) { this.amountBasedOnFormula = amountBasedOnFormula; }

    public String getDependsOnPaymentDays() { return dependsOnPaymentDays; }
    public void setDependsOnPaymentDays(String dependsOnPaymentDays) { this.dependsOnPaymentDays = dependsOnPaymentDays; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
