package com.dashboard.Dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotation {
    private String name;
    private String owner;
    private String creation;
    private String modified;
    private String modified_by;
    private String _user_tags;
    private String _comments;
    private String _assign;
    private String _liked_by;
    private int docstatus;
    private int idx;
    private String status;
    private String transaction_date;
    private String valid_till;
    private double total;
    private double net_total;
    private String taxes_and_charges_added;
    private String taxes_and_charges_deducted;
    private double total_taxes_and_charges;
    private double discount_amount;
    private double grand_total;
    private double rounding_adjustment;
    private double rounded_total;
    private String title;
    @JsonProperty("supplier")
    private Supplier supplier; // Ensure this maps to the Supplier object
    private double base_grand_total;
    private String company;
    private String currency;
}
