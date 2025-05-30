package com.example.demo.dto.ERP;

import lombok.Data;

@Data
public class StockEntryDetailDTO {
    private String item_code;
    private double qty;
    private double transfer_qty;
    private String uom;
    private String stock_uom;
    private double conversion_factor;
}
