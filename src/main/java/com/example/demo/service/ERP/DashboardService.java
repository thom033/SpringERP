package com.example.demo.service.ERP;

import com.example.demo.dto.ERP.InvoiceDTO;
import com.example.demo.dto.ERP.QuotationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class DashboardService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final QuotationService quotationService;
    private final PurchaseOrderService orderService;
    private final AccountingService accountingService;
    private final SupplierService supplierService;

    public DashboardService(RestTemplate restTemplate, 
                          QuotationService quotationService,
                          PurchaseOrderService orderService,
                          AccountingService accountingService,
                          SupplierService supplierService) {
        this.restTemplate = restTemplate;
        this.quotationService = quotationService;
        this.orderService = orderService;
        this.accountingService = accountingService;
        this.supplierService = supplierService;
    }

    public Map<String, Object> getDashboardData(String sid) {
        Map<String, Object> dashboardData = new HashMap<>();
    
        // Récupérer le nombre de devis en attente
        int pendingQuotationsCount = getPendingQuotationsCount(sid);
        System.out.println("Pending Quotations Count: " + pendingQuotationsCount);
        dashboardData.put("pendingQuotationsCount", pendingQuotationsCount);
    
        // Récupérer le nombre de commandes actives
        int activeOrdersCount = getActiveOrdersCount(sid);
        System.out.println("Active Orders Count: " + activeOrdersCount);
        dashboardData.put("activeOrdersCount", activeOrdersCount);
    
        // Récupérer le nombre de factures impayées
        int unpaidInvoicesCount = getUnpaidInvoicesCount(sid);
        System.out.println("Unpaid Invoices Count: " + unpaidInvoicesCount);
        dashboardData.put("unpaidInvoicesCount", unpaidInvoicesCount);
    
        // Récupérer le nombre total de fournisseurs
        int suppliersCount = getSuppliersCount(sid);
        System.out.println("Suppliers Count: " + suppliersCount);
        dashboardData.put("suppliersCount", suppliersCount);
    
        // Récupérer les derniers devis
        // List<QuotationDTO> recentQuotations = quotationService.getRecentQuotations(sid, 5);
        // System.out.println("Recent Quotations: " + recentQuotations.size());
        // dashboardData.put("recentQuotations", recentQuotations);
    
        // Récupérer les dernières factures
        List<InvoiceDTO> recentInvoices = accountingService.getRecentInvoices(sid, 5);
        System.out.println("Recent Invoices: " + recentInvoices.size());
        dashboardData.put("recentInvoices", recentInvoices);
    
        return dashboardData;
    }

    private int getPendingQuotationsCount(String sid) {
        String url = baseUrl + "/api/resource/Supplier Quotation?fields=[\"name\"]" +
                    "&filters=[[\"docstatus\",\"=\",1],[\"status\",\"=\",\"Pending\"]]" +
                    "&limit_page_length=1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            JsonNode.class
        );
    
        if (response.getBody() != null) {
            return response.getBody().path("total_count").asInt(0);  // Utiliser total_count
        }
        return 0;
    }    

    private int getActiveOrdersCount(String sid) {
        String url = baseUrl + "/api/resource/Purchase Order?fields=[\"name\"]" +
                    "&filters=[[\"docstatus\",\"=\",1],[\"status\",\"not in\",[\"Completed\",\"Cancelled\"]]]" +
                    "&limit_page_length=1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            JsonNode.class
        );
    
        if (response.getBody() != null) {
            return response.getBody().path("total_count").asInt(0);  // Utiliser total_count
        }
        return 0;
    }

    private int getUnpaidInvoicesCount(String sid) {
        String url = baseUrl + "/api/resource/Purchase Invoice?fields=[\"name\"]" +
                    "&filters=[[\"docstatus\",\"=\",1],[\"status\",\"=\",\"Unpaid\"]]" +
                    "&limit_page_length=1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            JsonNode.class
        );
    
        if (response.getBody() != null) {
            return response.getBody().path("total_count").asInt(0);  // Utiliser total_count
        }
        return 0;
    }

    private int getSuppliersCount(String sid) {
        String url = baseUrl + "/api/resource/Supplier?fields=[\"name\"]" +
                    "&limit_page_length=1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            JsonNode.class
        );
    
        if (response.getBody() != null) {
            return response.getBody().path("total_count").asInt(0);  // Utiliser total_count
        }
        return 0;
    }
}