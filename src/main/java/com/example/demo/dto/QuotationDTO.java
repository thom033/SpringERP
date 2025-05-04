package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuotationDTO {
    private String name;
    private String transaction_date;
    private String supplier;
    private String supplier_name;
    private String status;
    private Double total;          
    private String currency;       
    private String valid_till;     
    private List<QuotationItemDTO> items;
}
