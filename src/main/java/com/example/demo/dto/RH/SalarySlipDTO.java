package com.example.demo.dto.RH;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class SalarySlipDTO {
    String name;
    String employee;
    String employee_name;
    String company;
    LocalDate posting_date;
    LocalDate start_date;
    LocalDate end_date;
    String payroll_frequency;
    String currency;
    double exchange_rate;
    String salary_structure;
    double total_working_days;
    double payment_days;
    double net_pay;
    double total_earnings;
    double total_deduction;
    double gross_pay;
    List<SalaryDetailDTO> earnings;
    List<SalaryDetailDTO> deductions;

    public SalarySlipDTO() {
        // Default constructor
    }
    
    public SalarySlipDTO(String employee, LocalDate posting_date, String salary_structure, String company) {
        this.employee = employee;
        this.posting_date = posting_date;
        this.start_date = posting_date.withDayOfMonth(1);
        this.end_date = posting_date.withDayOfMonth(posting_date.lengthOfMonth());
        this.payroll_frequency = "Monthly";
        this.salary_structure = salary_structure;
        this.currency = "ALL";
        this.exchange_rate = 1.0;
        this.total_working_days = posting_date.lengthOfMonth();
        this.payment_days = total_working_days;
        this.company = company;
    }

}
