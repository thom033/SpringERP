package com.example.demo.dto.RH;

import lombok.Data;

@Data
public class CompanyDTO {
    String name;
    String company_name;
    String abbr;
    String default_currency;
    String country;

    String default_holiday_list;

    public CompanyDTO(){
        this.default_holiday_list = "Holiday Test";
    }
}
