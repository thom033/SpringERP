package com.example.demo.dto.ERP;

import java.util.List;

import lombok.Data;

@Data
public class StockEntryDTO {
    private String naming_series;
    private String name;
    private String stock_entry_type;
    private String company;
    private List<StockEntryDetailDTO> items;
}
