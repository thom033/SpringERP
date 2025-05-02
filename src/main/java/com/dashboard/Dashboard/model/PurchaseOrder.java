package com.dashboard.Dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrder {
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
    private String transaction_date;
    private double total;
    private double net_total;
    private double tax_withholding_net_total;
    private String taxes_and_charges_added;
    private String taxes_and_charges_deducted;
    private double total_taxes_and_charges;
    private double grand_total;
    private double rounding_adjustment;
    private double rounded_total;
    private double advance_paid;
    private double discount_amount;
    private String status;
    private double per_billed;
    private double per_received;
    private String supplier_name;
    private double base_grand_total;
    private String company;
    private String currency;
    private String supplier;
    private String advance_payment_status;
    private String party_account_currency;
    private int _comment_count;
}
