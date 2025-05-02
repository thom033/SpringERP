package com.dashboard.Dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseInvoice {
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
    private String posting_date;
    private double total;
    private double net_total;
    private double tax_withholding_net_total;
    private String taxes_and_charges_added;
    private String taxes_and_charges_deducted;
    private double total_taxes_and_charges;
    private double grand_total;
    private double rounding_adjustment;
    private double rounded_total;
    private double total_advance;
    private double outstanding_amount;
    private double discount_amount;
    private double paid_amount;
    private double write_off_amount;
    private String status;
    private String title;
    private String supplier;
    private String supplier_name;
    private double base_grand_total;
    private String due_date;
    private String company;
    private String currency;
    private boolean is_return;
    private String release_date;
    private boolean on_hold;
    private String represents_company;
    private boolean is_internal_supplier;
    private String party_account_currency;
    private int _comment_count;
}
