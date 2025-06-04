package com.example.demo.dto.RH;

import java.util.List;

import lombok.Data;

@Data
public class SalaryComponentDTO {
    public String salary_component;
    public String salary_component_abbr;
    public String type;
    public String formula;
    public String company;

    public String amount_based_on_formula;
    public String depends_on_payment_days;

    public List<SalaryComponentAccountDTO> accounts;

    public SalaryComponentDTO(){}

    public SalaryComponentDTO(String salary_component, String salary_component_abbr, String type, String formula, String amount_based_on_formula, String depends_on_payment_days, String company, List<SalaryComponentAccountDTO> accounts) {
        this.salary_component = salary_component;
        this.salary_component_abbr = salary_component_abbr;
        this.type = type;
        this.formula = formula;
        this.amount_based_on_formula = amount_based_on_formula;
        this.depends_on_payment_days = depends_on_payment_days;
        this.company = company;
        this.accounts = accounts;
    }
}
