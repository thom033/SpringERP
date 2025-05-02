package com.dashboard.Dashboard.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.dashboard.Dashboard.model.SupplierQuotation;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.http.HttpSession;

@Controller
public class SupplierQuotationController extends BaseController {

    @GetMapping("/supplier-quotation")
    public String supplierQuotation(Model model, HttpSession session) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Cookie", "sid=" + getSid());

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

            // Récupérer le fournisseur sélectionné depuis la session
            String selectedSupplier = (String) session.getAttribute("selectedSupplier");
            if (selectedSupplier == null || selectedSupplier.isEmpty()) {
                model.addAttribute("error", "Aucun fournisseur sélectionné. Veuillez en choisir un.");
                return "redirect:/supplier";
            }

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
            System.out.println("Response Body: " + responseBody);

            // Convertir le JSON en liste d'objets SupplierQuotation
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            List<SupplierQuotation> supplierQuotations = new ArrayList<>();
            if (keysNode!=null) {
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
            

            if (supplierQuotations.isEmpty()) {
                model.addAttribute("message", "Aucune quotation fournisseur trouvée.");
            }

            // Ajouter la liste des Supplier Quotation au modèle
            model.addAttribute("supplierQuotations", supplierQuotations);
            return "supplier_quotation"; // Retourner la vue "supplier_quotation"

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return "supplier_quotation";
        }
    }
}
