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

import com.example.demo.dto.ERP.StockEntryDTO;
import com.example.demo.dto.ERP.StockEntryDetailDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class StockService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public StockService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<StockEntryDTO> getStockEntry(String sid) throws Exception{
        String doctype = "Stock Entry";
        String fields = "[\"name\" , \"stock_entry_type\" , \"company\"]";
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
    
        List<StockEntryDTO> stockEntry = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            System.out.println(data);
            for (JsonNode stockentry : data) {
                StockEntryDTO dto = new StockEntryDTO();

                dto.setName(getTextValue(stockentry, "name"));
                dto.setCompany(getTextValue(stockentry, "company"));
                dto.setStock_entry_type(getTextValue(stockentry, "stock_entry_type"));

                stockEntry.add(dto);
            }
        }

        int i = 0;
        for (StockEntryDTO stockEntryDTO : stockEntry) {
            i++;
            System.out.println("Stock num :" + i);
            System.out.println("naming series :" + stockEntryDTO.getName());
            System.out.println("Company :" + stockEntryDTO.getCompany());
            System.out.println("-----------------------------");
        }
        return stockEntry;
    }

    public StockEntryDTO getStockEntryByName(String sid, String stockEntryName) throws Exception {
        String url = baseUrl + "/api/resource/Stock Entry/" + stockEntryName;

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

        if (response.getBody() == null || !response.getBody().has("data")) {
            return null;
        }

        JsonNode stockentry = response.getBody().get("data");
        StockEntryDTO dto = new StockEntryDTO();

        dto.setName(getTextValue(stockentry, "name"));
        dto.setCompany(getTextValue(stockentry, "company"));
        dto.setStock_entry_type(getTextValue(stockentry, "stock_entry_type"));

        if (stockentry.has("items")) {
            List<StockEntryDetailDTO> itemList = new ArrayList<>();
            for (JsonNode item : stockentry.get("items")) {
                StockEntryDetailDTO detail = new StockEntryDetailDTO();
                detail.setItem_code(getTextValue(item, "item_code"));
                detail.setQty(item.has("qty") ? item.get("qty").asDouble() : 0.0);
                itemList.add(detail);
            }
            dto.setItems(itemList);
        }

        System.out.println("naming series :" + dto.getName());
        System.out.println("Company :" + dto.getCompany());
        System.out.println("Items count: " + (dto.getItems() != null ? dto.getItems().size() : 0));
        return dto;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}
