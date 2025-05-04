package com.dashboard.Dashboard.service;

import org.springframework.http.HttpHeaders; // Corrected import
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dashboard.Dashboard.model.SupplierQuotation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupplierQuotationService {

    public List<SupplierQuotation> getSupplierQuotationsBySupplier(HttpHeaders headers, String selectedSupplier) {
        List<SupplierQuotation> supplierQuotations = new ArrayList<>(); // Initialize the list
        try {
            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";
        
            // Préparer le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Supplier Quotation");
            requestBody.put("fields", List.of(
                "`tabSupplier Quotation`.`name`", "`tabSupplier Quotation`.`owner`", "`tabSupplier Quotation`.`creation`",
                "`tabSupplier Quotation`.`modified`", "`tabSupplier Quotation`.`modified_by`", "`tabSupplier Quotation`.`_user_tags`",
                "`tabSupplier Quotation`.`_comments`", "`tabSupplier Quotation`.`_assign`", "`tabSupplier Quotation`.`_liked_by`",
                "`tabSupplier Quotation`.`docstatus`", "`tabSupplier Quotation`.`idx`", "`tabSupplier Quotation`.`status`",
                "`tabSupplier Quotation`.`transaction_date`", "`tabSupplier Quotation`.`valid_till`", "`tabSupplier Quotation`.`total`",
                "`tabSupplier Quotation`.`net_total`", "`tabSupplier Quotation`.`taxes_and_charges_added`", "`tabSupplier Quotation`.`taxes_and_charges_deducted`",
                "`tabSupplier Quotation`.`total_taxes_and_charges`", "`tabSupplier Quotation`.`discount_amount`", "`tabSupplier Quotation`.`grand_total`",
                "`tabSupplier Quotation`.`rounding_adjustment`", "`tabSupplier Quotation`.`rounded_total`", "`tabSupplier Quotation`.`title`",
                "`tabSupplier Quotation`.`supplier`", "`tabSupplier Quotation`.`base_grand_total`", "`tabSupplier Quotation`.`company`",
                "`tabSupplier Quotation`.`currency`"
            ));

            // Ajouter le fournisseur sélectionné dans les filtres
            Map<String, Object> filters = new HashMap<>();
            filters.put("supplier", selectedSupplier);
            requestBody.put("filters", filters);

            requestBody.put("order_by", "`tabSupplier Quotation`.creation desc");
            requestBody.put("start", 0);
            requestBody.put("page_length", 20);
            requestBody.put("view", "List");
            requestBody.put("with_comment_count", 1);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Récupérer la liste des Supplier Quotation
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log the entire response body
            String responseBody = response.getBody();
            System.out.println("Response Body of Supplier Quotation: " + responseBody);

            // Convertir le JSON en liste d'objets SupplierQuotation
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            if (keysNode != null) {
                if (keysNode.isArray() && valuesNode.isArray()) {
                    List<String> keys = objectMapper.convertValue(keysNode, new TypeReference<List<String>>() {});
                    for (JsonNode valueArray : valuesNode) {
                        Map<String, Object> supplierQuotationMap = new HashMap<>();
                        for (int i = 0; i < keys.size(); i++) {
                            supplierQuotationMap.put(keys.get(i), valueArray.get(i).asText());
                        }
                        SupplierQuotation supplierQuotation = objectMapper.convertValue(supplierQuotationMap, SupplierQuotation.class);
                        supplierQuotations.add(supplierQuotation);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des Supplier Quotations : " + e.getMessage());
        }
        return supplierQuotations; // Added return statement
    }

    public List<SupplierQuotation> getSupplierQuotationsByName(HttpHeaders headers, String SupplierQuotationName) {
        List<SupplierQuotation> supplierQuotations = new ArrayList<>(); // Initialize the list
        try {
            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";
        
            // Préparer le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Supplier Quotation");
            requestBody.put("fields", List.of(
                "`tabSupplier Quotation`.`name`", "`tabSupplier Quotation`.`owner`", "`tabSupplier Quotation`.`creation`",
                "`tabSupplier Quotation`.`modified`", "`tabSupplier Quotation`.`modified_by`", "`tabSupplier Quotation`.`_user_tags`",
                "`tabSupplier Quotation`.`_comments`", "`tabSupplier Quotation`.`_assign`", "`tabSupplier Quotation`.`_liked_by`",
                "`tabSupplier Quotation`.`docstatus`", "`tabSupplier Quotation`.`idx`", "`tabSupplier Quotation`.`status`",
                "`tabSupplier Quotation`.`transaction_date`", "`tabSupplier Quotation`.`valid_till`", "`tabSupplier Quotation`.`total`",
                "`tabSupplier Quotation`.`net_total`", "`tabSupplier Quotation`.`taxes_and_charges_added`", "`tabSupplier Quotation`.`taxes_and_charges_deducted`",
                "`tabSupplier Quotation`.`total_taxes_and_charges`", "`tabSupplier Quotation`.`discount_amount`", "`tabSupplier Quotation`.`grand_total`",
                "`tabSupplier Quotation`.`rounding_adjustment`", "`tabSupplier Quotation`.`rounded_total`", "`tabSupplier Quotation`.`title`",
                "`tabSupplier Quotation`.`supplier`", "`tabSupplier Quotation`.`base_grand_total`", "`tabSupplier Quotation`.`company`",
                "`tabSupplier Quotation`.`currency`"
            ));

            // Ajouter le fournisseur sélectionné dans les filtres
            Map<String, Object> filters = new HashMap<>();
            filters.put("name", SupplierQuotationName);
            requestBody.put("filters", filters);

            requestBody.put("order_by", "`tabSupplier Quotation`.creation desc");
            requestBody.put("start", 0);
            requestBody.put("page_length", 20);
            requestBody.put("view", "List");
            requestBody.put("with_comment_count", 1);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Récupérer la liste des Supplier Quotation
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log the entire response body
            String responseBody = response.getBody();
            System.out.println("Response Body of Supplier Quotation By Name: " + responseBody);

            // Convertir le JSON en liste d'objets SupplierQuotation
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            if (keysNode != null) {
                if (keysNode.isArray() && valuesNode.isArray()) {
                    List<String> keys = objectMapper.convertValue(keysNode, new TypeReference<List<String>>() {});
                    for (JsonNode valueArray : valuesNode) {
                        Map<String, Object> supplierQuotationMap = new HashMap<>();
                        for (int i = 0; i < keys.size(); i++) {
                            supplierQuotationMap.put(keys.get(i), valueArray.get(i).asText());
                        }
                        SupplierQuotation supplierQuotation = objectMapper.convertValue(supplierQuotationMap, SupplierQuotation.class);
                        supplierQuotations.add(supplierQuotation);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des Supplier Quotations : " + e.getMessage());
        }
        return supplierQuotations; // Added return statement
    
    }
}
