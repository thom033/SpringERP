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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ui.Model;
import com.dashboard.Dashboard.model.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpSession;

@Controller
public class SupplierController extends BaseController {

    @GetMapping("/supplier")
    public String supplier(Model model) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            // Ajouter l'indicateur de page active
            model.addAttribute("activePage", "supplier");

            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Cookie", "sid=" + getSid());

            // Préparer le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Supplier"); // Spécifier le DocType
            requestBody.put("fields", List.of("name", "supplier_name", "country", "supplier_type", "language")); // Champs à récupérer
            requestBody.put("filters", new HashMap<>()); // Ajouter des filtres si nécessaire

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

            List<Supplier> suppliers = new ArrayList<>();
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

            // Ajouter la liste des fournisseurs au modèle
            model.addAttribute("suppliers", suppliers);
            return "supplier"; // Retourner la vue "supplier"

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return "supplier";
        }
    }

    @PostMapping("/select-supplier")
    public String selectSupplier(@RequestParam("supplierName") String supplierName, HttpSession session, Model model) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            // Enregistrer le fournisseur sélectionné dans la session
            session.setAttribute("selectedSupplier", supplierName);

            // Ajouter un message de confirmation au modèle
            model.addAttribute("success", "Fournisseur sélectionné : " + supplierName);

            return "redirect:/supplier"; // Rediriger vers la page des fournisseurs
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la sélection du fournisseur : " + e.getMessage());
            return "supplier";
        }
    }
}
