package com.dashboard.Dashboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dashboard.Dashboard.model.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class SupplierService {

    public List<Supplier> getAllSuppliers(HttpHeaders headers) {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Supplier");
            requestBody.put("fields", List.of("name", "supplier_name", "country", "supplier_type", "language"));
            requestBody.put("filters", new HashMap<>()); 

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Récupérer la liste des fournisseurs
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log the entire response body
            String responseBody = response.getBody();
            System.out.println("Response Body: " + responseBody);

            // Convertir le JSON en liste d'objets Supplier
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            if (keysNode.isArray() && valuesNode.isArray()) {
                List<String> keys = objectMapper.convertValue(keysNode, new TypeReference<List<String>>() {});
                for (JsonNode valueArray : valuesNode) {
                    Map<String, Object> supplierMap = new HashMap<>();
                    for (int i = 0; i < keys.size(); i++) {
                        supplierMap.put(keys.get(i), valueArray.get(i).asText());
                    }
                    Supplier supplier = objectMapper.convertValue(supplierMap, Supplier.class);
                    suppliers.add(supplier);
                }
            }

            if (suppliers.isEmpty()) {
                System.out.println("Aucun fournisseur trouvé.");
            } else {
                System.out.println("Liste des fournisseurs récupérée avec succès.");
            }
            return suppliers;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des fournisseurs : " + e.getMessage());
        }

        return suppliers;
    }
    
    public List<Supplier> getSuppliersByName(HttpHeaders headers, String supplierName) {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Supplier");
            requestBody.put("fields", List.of("name", "supplier_name", "country", "supplier_type", "language"));
            
            // Ajouter le fournisseur sélectionné dans les filtres
            Map<String, Object> filters = new HashMap<>();
            filters.put("supplier_name", supplierName);
            requestBody.put("filters", filters);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Récupérer la liste des fournisseurs
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log the entire response body
            String responseBody = response.getBody();
            System.out.println("Response Body: " + responseBody);

            // Convertir le JSON en liste d'objets Supplier
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            if (keysNode.isArray() && valuesNode.isArray()) {
                List<String> keys = objectMapper.convertValue(keysNode, new TypeReference<List<String>>() {});
                for (JsonNode valueArray : valuesNode) {
                    Map<String, Object> supplierMap = new HashMap<>();
                    for (int i = 0; i < keys.size(); i++) {
                        supplierMap.put(keys.get(i), valueArray.get(i).asText());
                    }
                    Supplier supplier = objectMapper.convertValue(supplierMap, Supplier.class);
                    suppliers.add(supplier);
                }
            }

            if (suppliers.isEmpty()) {
                System.out.println("Aucun fournisseur trouvé.");
            } else {
                System.out.println("Liste des fournisseurs récupérée avec succès.");
            }
            return suppliers;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des fournisseurs : " + e.getMessage());
        }

        return suppliers;
    }
    
}
