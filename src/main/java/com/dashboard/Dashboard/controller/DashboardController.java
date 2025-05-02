package com.dashboard.Dashboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.dashboard.Dashboard.model.Dashboard;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DashboardController extends BaseController{

    @GetMapping("/dashboard")
    public String dashboardTotal(Map<String, Object> model) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.doctype.number_card.number_card.get_result";

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Cookie", "sid="+getSid()); // Remplacez par un cookie de session valide

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Récupérer le premier total
            Map<String, Object> doc1 = new HashMap<>();
            doc1.put("function", "Count");
            doc1.put("document_type", "Article");
            doc1.put("aggregate_function_based_on", "");

            Map<String, Object> requestBody1 = new HashMap<>();
            requestBody1.put("doc", doc1);
            requestBody1.put("filters", List.of(
                List.of("Article", "docstatus", "=", "0")
            ));

            HttpEntity<Map<String, Object>> entity1 = new HttpEntity<>(requestBody1, headers);
            ResponseEntity<String> response1 = restTemplate.postForEntity(url, entity1, String.class);
            String totalPurchaseInvoices = objectMapper.readTree(response1.getBody()).get("message").asText();
            

            // Récupérer le deuxième total
            Map<String, Object> doc2 = new HashMap<>();
            doc2.put("function", "Sum");
            doc2.put("document_type", "Sales Invoice");
            doc2.put("aggregate_function_based_on", "grand_total");

            Map<String, Object> requestBody2 = new HashMap<>();
            requestBody2.put("doc", doc2);
            requestBody2.put("filters", List.of(
                List.of("Sales Invoice", "docstatus", "=", "1")
            ));

            HttpEntity<Map<String, Object>> entity2 = new HttpEntity<>(requestBody2, headers);
            ResponseEntity<String> response2 = restTemplate.postForEntity(url, entity2, String.class);
            String totalSalesInvoices = objectMapper.readTree(response2.getBody()).get("message").asText();

            // Récupérer le troisième total
            Map<String, Object> doc3 = new HashMap<>();
            doc3.put("function", "Count");
            doc3.put("document_type", "Customer");
            doc3.put("aggregate_function_based_on", "");

            Map<String, Object> requestBody3 = new HashMap<>();
            requestBody3.put("doc", doc3);
            requestBody3.put("filters", List.of());

            HttpEntity<Map<String, Object>> entity3 = new HttpEntity<>(requestBody3, headers);
            ResponseEntity<String> response3 = restTemplate.postForEntity(url, entity3, String.class);
            String totalCustomers = objectMapper.readTree(response3.getBody()).get("message").asText();

            Dashboard dashboard = new Dashboard();
            dashboard.setTotalCustomers(Double.parseDouble(totalCustomers));
            dashboard.setTotalPurchaseInvoices(Double.parseDouble(totalPurchaseInvoices));
            dashboard.setTotalSalesInvoices(Double.parseDouble(totalSalesInvoices));
            // Ajouter les résultats au modèle
            model.put("dashboard", dashboard);


            // Retourner la vue "dashboard"
            return "dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.put("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return "dashboard";
        }
    }
}
