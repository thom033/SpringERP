package com.example.demo.dto.RH;

import java.util.List;

import lombok.Data;

@Data
public class SalaryStructureDTO {
    String name;
    String company;
    String is_active; // Yes No
    String currency;
    List<SalaryComponentDTO> earnings;
    List<SalaryComponentDTO> deductions;

    String docstatus;
}
