package com.example.demo.service.ERP;

import com.example.demo.dto.ERP.QuotationDTO;
import com.example.demo.dto.ERP.SupplierDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplService {
    
    @Value("${erpnext.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;

    public SupplService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SupplierDTO> getSuppliers(String sid) {
        String url = baseUrl + "/api/resource/Supplier?fields=[\"*\"]";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );

        List<SupplierDTO> suppliers = new ArrayList<>();
        
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode supplier : data) {
                SupplierDTO dto = new SupplierDTO();
                dto.setName(getTextValue(supplier, "name"));
                dto.setSupplier_name(getTextValue(supplier, "supplier_name"));
                dto.setSupplier_group(getTextValue(supplier, "supplier_group"));
                dto.setSupplier_type(getTextValue(supplier, "supplier_type"));
                dto.setCountry(getTextValue(supplier, "country"));
                suppliers.add(dto);
            }
        }
        
        return suppliers;
    }

    public List<String> getItems(String sid) {
        String url = baseUrl + "/api/resource/Item?fields=[\"name\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );

        List<String> items = new ArrayList<>();
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode item : data) {
                items.add(getTextValue(item, "name"));
            }
        }

        return items;
    }

    public List<String> getWarehouses(String sid) {
        String url = baseUrl + "/api/resource/Warehouse?fields=[\"name\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );

        List<String> warehouses = new ArrayList<>();
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode warehouse : data) {
                warehouses.add(getTextValue(warehouse, "name"));
            }
        }
         
        return warehouses;
    }

    public JsonNode createSupplierQuotation(String sid, QuotationDTO quotation){

        ResponseEntity<JsonNode> response = null;

        try {
            String url = baseUrl + "/api/resource/Supplier Quotation";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper(); 
            String json = mapper.writeValueAsString(quotation);
            JsonNode supplierQuotationData = mapper.readTree(json);

            HttpEntity<JsonNode> entity = new HttpEntity<>(supplierQuotationData, headers);

            response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                JsonNode.class
            );

            return response.getBody();
        } catch (Exception e) {
            // TODO: handle exception
        }
        

        return response.getBody();
    }

    public JsonNode createSupplierQuotation(String sid, JsonNode supplierQuotationData) {
        String url = baseUrl + "/api/resource/Supplier Quotation";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JsonNode> entity = new HttpEntity<>(supplierQuotationData, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            JsonNode.class
        );

        return response.getBody();
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}