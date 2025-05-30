// src/main/java/com/example/demo/dto/SupplierDTO.java
package com.example.demo.dto.ERP;

import lombok.Data;

@Data
public class SupplierDTO {
    private String name;
    private String supplier_name;
    private String supplier_group;
    private String supplier_type;
    private String country;
}