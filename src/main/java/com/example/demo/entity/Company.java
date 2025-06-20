package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabCompany")
public class Company {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "abbr")
    private String abbr;

    @Column(name = "default_currency")
    private String defaultCurrency;

    @Column(name = "country")
    private String country;

    @Column(name = "default_holiday_list")
    private String defaultHolidayList;

    public Company() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAbbr() { return abbr; }
    public void setAbbr(String abbr) { this.abbr = abbr; }

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDefaultHolidayList() { return defaultHolidayList; }
    public void setDefaultHolidayList(String defaultHolidayList) { this.defaultHolidayList = defaultHolidayList; }
}
