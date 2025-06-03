package com.example.demo.dto.RH;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SalaryStructureAssignmentDTO {
    String employee_ref;
    String salary_structure;
    LocalDate from_date;
    String base;

    String currency;
    String company;
    String employee;
}
