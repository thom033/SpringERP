package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabSalarySlip")
public class SalarySlip {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "employee")
    private String employee;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "company")
    private String company;

    @Column(name = "posting_date")
    private String postingDate;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "payroll_frequency")
    private String payrollFrequency;

    @Column(name = "currency")
    private String currency;

    @Column(name = "exchange_rate")
    private double exchangeRate;

    @Column(name = "salary_structure")
    private String salaryStructure;

    @Column(name = "total_working_days")
    private double totalWorkingDays;

    @Column(name = "payment_days")
    private double paymentDays;

    @Column(name = "net_pay")
    private double netPay;

    @Column(name = "total_earnings")
    private double totalEarnings;

    @Column(name = "total_deduction")
    private double totalDeduction;

    @Column(name = "gross_pay")
    private double grossPay;

    public SalarySlip() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmployee() { return employee; }
    public void setEmployee(String employee) { this.employee = employee; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPostingDate() { return postingDate; }
    public void setPostingDate(String postingDate) { this.postingDate = postingDate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getPayrollFrequency() { return payrollFrequency; }
    public void setPayrollFrequency(String payrollFrequency) { this.payrollFrequency = payrollFrequency; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }

    public String getSalaryStructure() { return salaryStructure; }
    public void setSalaryStructure(String salaryStructure) { this.salaryStructure = salaryStructure; }

    public double getTotalWorkingDays() { return totalWorkingDays; }
    public void setTotalWorkingDays(double totalWorkingDays) { this.totalWorkingDays = totalWorkingDays; }

    public double getPaymentDays() { return paymentDays; }
    public void setPaymentDays(double paymentDays) { this.paymentDays = paymentDays; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }

    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }

    public double getTotalDeduction() { return totalDeduction; }
    public void setTotalDeduction(double totalDeduction) { this.totalDeduction = totalDeduction; }

    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double grossPay) { this.grossPay = grossPay; }
}
