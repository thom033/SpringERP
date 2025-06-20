package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalaryStructureAssignment")
public class SalaryStructureAssignment {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "employee_ref")
    private String employeeRef;

    @Column(name = "salary_structure")
    private String salaryStructure;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "base")
    private String base;

    @Column(name = "currency")
    private String currency;

    @Column(name = "company")
    private String company;

    @Column(name = "employee")
    private String employee;

    public SalaryStructureAssignment() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmployeeRef() { return employeeRef; }
    public void setEmployeeRef(String employeeRef) { this.employeeRef = employeeRef; }

    public String getSalaryStructure() { return salaryStructure; }
    public void setSalaryStructure(String salaryStructure) { this.salaryStructure = salaryStructure; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getEmployee() { return employee; }
    public void setEmployee(String employee) { this.employee = employee; }
}
