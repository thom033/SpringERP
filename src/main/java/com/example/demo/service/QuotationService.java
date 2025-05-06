package com.example.demo.service;

import com.example.demo.dto.QuotationDTO;
import com.example.demo.dto.QuotationItemDTO;
import com.example.demo.dto.UpdatePriceRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuotationService {
    
    @Value("${erpnext.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;

    public QuotationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<QuotationDTO> getSupplierQuotations(String sid, String supplierId) {
        // Changement de l'URL pour pointer vers Supplier Quotation
        String url = baseUrl + "/api/resource/Supplier Quotation?fields=[\"*\"]" +
                    "&filters=[[\"supplier\",\"=\",\"" + supplierId + "\"]]";
        
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
    
        List<QuotationDTO> quotations = new ArrayList<>();
        
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode quotation : data) {
                QuotationDTO dto = new QuotationDTO();
                dto.setName(getTextValue(quotation, "name"));
                dto.setTransaction_date(getTextValue(quotation, "transaction_date"));
                dto.setSupplier(supplierId);
                dto.setSupplier_name(getTextValue(quotation, "supplier_name"));
                dto.setStatus(getTextValue(quotation, "status"));
                quotations.add(dto);
            }
        }
        
        return quotations;
    }
    
    public QuotationDTO getQuotationDetails(String sid, String quotationId) {
        // Changement de l'URL pour pointer vers Supplier Quotation
        String url = baseUrl + "/api/resource/Supplier Quotation/" + quotationId + "?fields=[\"*\",\"items.*\"]";
        
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
    
        QuotationDTO quotation = new QuotationDTO();
        
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            quotation.setName(getTextValue(data, "name"));
            quotation.setTransaction_date(getTextValue(data, "transaction_date"));
            quotation.setSupplier(getTextValue(data, "supplier"));
            quotation.setSupplier_name(getTextValue(data, "supplier_name"));
            quotation.setStatus(getTextValue(data, "status"));
            quotation.setTotal(getDoubleValue(data, "total"));
            quotation.setCurrency(getTextValue(data, "currency"));
            quotation.setValid_till(getTextValue(data, "valid_till"));
            
            List<QuotationItemDTO> items = new ArrayList<>();
            if (data.has("items")) {
                for (JsonNode item : data.get("items")) {
                    QuotationItemDTO itemDTO = new QuotationItemDTO();
                    itemDTO.setName(getTextValue(item, "name"));
                    itemDTO.setItem_code(getTextValue(item, "item_code"));
                    itemDTO.setItem_name(getTextValue(item, "item_name"));
                    itemDTO.setQty(getDoubleValue(item, "qty"));
                    itemDTO.setStock_uom(getTextValue(item, "stock_uom"));
                    itemDTO.setRate(getDoubleValue(item, "rate"));
                    itemDTO.setAmount(getDoubleValue(item, "amount"));
                    itemDTO.setDescription(getTextValue(item, "description"));
                    itemDTO.setConversion_factor(getDoubleValue(item, "conversion_factor"));
                    items.add(itemDTO);
                }
            }
            quotation.setItems(items);
        }
        
        return quotation;
    }

    public void updateItemPrice(String sid, String quotationId, UpdatePriceRequest request) {
        // D'abord, vérifier le statut du devis
        QuotationDTO quotation = getQuotationDetails(sid, quotationId);
        if (!"Draft".equalsIgnoreCase(quotation.getStatus())) {
            throw new RuntimeException("Le devis doit être en état 'Draft' pour pouvoir modifier les prix");
        }
    
        String url = baseUrl + "/api/resource/Supplier Quotation Item/" + request.getItemName();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> data = new HashMap<>();
        data.put("rate", request.getNewRate());
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
        
        restTemplate.exchange(
            url,
            HttpMethod.PUT,
            entity,
            JsonNode.class
        );
    }

    public void submitQuotation(String sid, String quotationId) {
        // Mettre à jour le statut de la quotation à "Submitted"
        String updateStatusUrl = baseUrl + "/api/resource/Supplier Quotation/" + quotationId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("status", "Submitted");
        
        HttpEntity<Map<String, Object>> statusEntity = new HttpEntity<>(statusData, headers);
        
        restTemplate.exchange(
            updateStatusUrl,
            HttpMethod.PUT,
            statusEntity,
            JsonNode.class
        );
    }

    public void cancelQuotation(String sid, String quotationId) {
        String url = baseUrl + "/api/method/frappe.client.cancel";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> data = new HashMap<>();
        data.put("doctype", "Supplier Quotation");
        data.put("name", quotationId);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
        
        restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            JsonNode.class
        );
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }

    private Double getDoubleValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asDouble() : null;
    }

    public List<QuotationDTO> getRecentQuotations(String sid, int limit) {
        String url = baseUrl + "/api/resource/Supplier Quotation?fields=[" +
                    "\"name\",\"transaction_date\",\"supplier\",\"supplier_name\"," +
                    "\"status\",\"total\"]" +
                    "&limit=" + limit +
                    "&order_by=transaction_date desc";
        
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
    
        List<QuotationDTO> quotations = new ArrayList<>();
        
        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode quotation : data) {
                QuotationDTO dto = new QuotationDTO();
                dto.setName(getTextValue(quotation, "name"));
                dto.setTransaction_date(getTextValue(quotation, "transaction_date"));
                dto.setSupplier(getTextValue(quotation, "supplier"));
                dto.setSupplier_name(getTextValue(quotation, "supplier_name"));
                dto.setStatus(getTextValue(quotation, "status"));
                dto.setTotal(getDoubleValue(quotation, "total"));
                quotations.add(dto);
            }
        }
        
        return quotations;
    }
}