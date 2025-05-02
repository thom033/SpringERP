package com.dashboard.Dashboard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown fields in the JSON response
public class Supplier {
    private String supplier_name;
    private String name;
    private String country;
    private String supplier_type;
    private String language;

    // Default no-argument constructor for Jackson
    public Supplier() {
    }

    @JsonCreator
    public Supplier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
