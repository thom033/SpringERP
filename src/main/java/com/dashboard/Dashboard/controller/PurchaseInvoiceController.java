package com.dashboard.Dashboard.controller;

import com.dashboard.Dashboard.model.PurchaseInvoice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PurchaseInvoiceController extends BaseController {

    @GetMapping("/purchase-invoices")
    public String getPurchaseInvoices(Model model) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            // Ajouter l'indicateur de page active
            model.addAttribute("activePage", "purchase-invoices");

            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Cookie", "sid=" + getSid());

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Purchase Invoice");
            requestBody.put("fields", List.of(
                "`tabPurchase Invoice`.`name`", "`tabPurchase Invoice`.`owner`", "`tabPurchase Invoice`.`posting_date`",
                "`tabPurchase Invoice`.`total`", "`tabPurchase Invoice`.`status`"
            ));
            requestBody.put("order_by", "`tabPurchase Invoice`.creation desc");
            requestBody.put("start", 0);
            requestBody.put("page_length", 20);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Make the request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse the response
            String responseBody = response.getBody();
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode valuesNode = messageNode.get("values");

            List<PurchaseInvoice> invoices = new ArrayList<>();
            if (valuesNode.isArray()) {
                for (JsonNode valueArray : valuesNode) {
                    Map<String, Object> invoiceMap = new HashMap<>();
                    invoiceMap.put("name", valueArray.get(0).asText());
                    invoiceMap.put("owner", valueArray.get(1).asText());
                    invoiceMap.put("posting_date", valueArray.get(2).asText());
                    invoiceMap.put("total", valueArray.get(3).asDouble());
                    invoiceMap.put("status", valueArray.get(4).asText());
                    PurchaseInvoice invoice = objectMapper.convertValue(invoiceMap, PurchaseInvoice.class);
                    invoices.add(invoice);
                }
            }

            if (invoices.isEmpty()) {
                model.addAttribute("message", "No purchase invoices found.");
            }

            // Add the list of Purchase Invoices to the model
            model.addAttribute("invoices", invoices);
            return "purchase_invoices"; // Return the view "purchase_invoices"

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error retrieving data: " + e.getMessage());
            return "error"; // Return an error view
        }
    }
}
