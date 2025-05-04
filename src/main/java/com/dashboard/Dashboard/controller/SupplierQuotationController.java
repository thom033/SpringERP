package com.dashboard.Dashboard.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.dashboard.Dashboard.model.SupplierQuotation;
import com.dashboard.Dashboard.service.SupplierQuotationService;
import com.dashboard.Dashboard.service.SupplierService;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class SupplierQuotationController extends BaseController {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    @Autowired 
    private SupplierService supplierService;

    @Autowired
    private SupplierQuotationService supplierQuotationService;

    @GetMapping("/supplier-quotation")
    public String supplierQuotation(Model model, HttpSession session) {
        try {
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            HttpHeaders headers = createHeaders();
            
            // Récupérer le fournisseur sélectionné depuis la session
            String selectedSupplier = (String) session.getAttribute("selectedSupplier");
            if (selectedSupplier == null || selectedSupplier.isEmpty()) {
                model.addAttribute("error", "Aucun fournisseur sélectionné. Veuillez en choisir un.");
                return "redirect:/supplier";
            }

            List<SupplierQuotation> supplierQuotations = supplierQuotationService.getSupplierQuotationsBySupplier(headers, selectedSupplier);
            
            if (supplierQuotations.isEmpty()) {
                model.addAttribute("error", "Aucune quotation fournisseur trouvée.");
            }

            model.addAttribute("activePage", "supplier-quotation");
            model.addAttribute("quotations", supplierQuotations);
            return "supplier_quotation"; // Retourner la vue "supplier_quotation"

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return "supplier_quotation";
        }
    }

    @GetMapping("/supplier-quotation/update-price")
    public String showUpdatePricePage(@RequestParam("quotationId") String quotationId, Model model) {
        try {
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            HttpHeaders headers = createHeaders();

            List<SupplierQuotation> supplierQuotations = supplierQuotationService.getSupplierQuotationsByName(headers,quotationId);
            SupplierQuotation supplierQuotation = supplierQuotations.get(0);

            model.addAttribute("activePage", "supplier-quotation");
            model.addAttribute("quotation", supplierQuotation);
            return "update_price"; // Retourner la vue "update_price"
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors du chargement de la page : " + e.getMessage());
            return "redirect:/supplier-quotation";
        }
    }

    @PostMapping("/supplier-quotation/update-price")
    public String updateQuotationPrice(@RequestParam("quotationId") String quotationId,
                                    @RequestParam("newPrice") double newPrice,
                                    Model model, HttpSession session) {
        
        HttpHeaders headers = createHeaders();
                                        
        // D'abord, vérifier le statut du devis
        List<SupplierQuotation> quotationList = supplierQuotationService.getSupplierQuotationsByName(headers, quotationId);
        if (quotationList.isEmpty()) {
            model.addAttribute("error", "Devis introuvable.");
            return "redirect:/supplier-quotation";
        }
        SupplierQuotation quotation = quotationList.get(0);

        if (!"Draft".equalsIgnoreCase(quotation.getStatus())) {
            throw new RuntimeException("Le devis doit être en état 'Draft' pour pouvoir modifier les prix");
        }
    
        String url = baseUrl + "/api/resource/Supplier Quotation Item/" + request.getItemName();
        
        
        
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

}
