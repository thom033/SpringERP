package com.example.demo.dto.ERP;

import lombok.Data;

@Data
public class UpdatePriceRequest {
    private String itemName;  // name du QuotationItemDTO
    private String itemCode;
    private Double newRate;
}