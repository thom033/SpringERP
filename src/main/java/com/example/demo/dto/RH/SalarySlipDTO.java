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
}
