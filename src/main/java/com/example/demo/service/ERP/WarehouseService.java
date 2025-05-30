package com.example.demo.service.ERP;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.ERP.WarehouseDTO;
import com.fasterxml.jackson.databind.JsonNode;


@Service
public class WarehouseService {

    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public WarehouseService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<WarehouseDTO> getWarehouse(String sid) throws Exception{
        String doctype = "Warehouse";
        String fields = "[\"name\" , \"company\"]";
        String filter = "[]";

        String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter;

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
    
        List<WarehouseDTO> warehouses = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode warehouse : data) {
                WarehouseDTO dto = new WarehouseDTO();
                dto.setWarehouse_name(getTextValue(warehouse, "name"));
                dto.setCompany(getTextValue(warehouse, "company"));
                warehouses.add(dto);
            }
        }
        return warehouses;
    }

    public void createWarehouse(String sid, WarehouseDTO warehouseDTO) throws Exception {
        String url = baseUrl + "/api/resource/Warehouse";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir le DTO en JSON automatiquement
        HttpEntity<WarehouseDTO> entity = new HttpEntity<>(warehouseDTO, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        System.out.println("Response: " + response.getBody().toPrettyString());
    }


    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}
