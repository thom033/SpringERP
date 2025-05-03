package com.dashboard.Dashboard.controller;

import com.dashboard.Dashboard.model.PurchaseOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PurchaseOrderController extends BaseController {

    @GetMapping("/purchase-orders")
    public String listPurchaseOrders(Model model, HttpSession session) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            // Ajouter l'indicateur de page active
            model.addAttribute("activePage", "purchase-orders");

            // Récupérer le fournisseur sélectionné depuis la session
            String selectedSupplier = (String) session.getAttribute("selectedSupplier");
            if (selectedSupplier == null || selectedSupplier.isEmpty()) {
                model.addAttribute("error", "Aucun fournisseur sélectionné. Veuillez en choisir un.");
                return "redirect:/supplier";
            }

            String url = "http://erpnext.localhost:8000/api/method/frappe.desk.reportview.get";

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Cookie", "sid=" + getSid());

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("doctype", "Purchase Order");
            requestBody.put("fields", List.of(
                "`tabPurchase Order`.`name`", "`tabPurchase Order`.`owner`", "`tabPurchase Order`.`creation`",
                "`tabPurchase Order`.`modified`", "`tabPurchase Order`.`modified_by`", "`tabPurchase Order`.`_user_tags`",
                "`tabPurchase Order`.`_comments`", "`tabPurchase Order`.`_assign`", "`tabPurchase Order`.`_liked_by`",
                "`tabPurchase Order`.`docstatus`", "`tabPurchase Order`.`idx`", "`tabPurchase Order`.`transaction_date`",
                "`tabPurchase Order`.`total`", "`tabPurchase Order`.`net_total`", "`tabPurchase Order`.`tax_withholding_net_total`",
                "`tabPurchase Order`.`taxes_and_charges_added`", "`tabPurchase Order`.`taxes_and_charges_deducted`",
                "`tabPurchase Order`.`total_taxes_and_charges`", "`tabPurchase Order`.`grand_total`",
                "`tabPurchase Order`.`rounding_adjustment`", "`tabPurchase Order`.`rounded_total`",
                "`tabPurchase Order`.`advance_paid`", "`tabPurchase Order`.`discount_amount`", "`tabPurchase Order`.`status`",
                "`tabPurchase Order`.`per_billed`", "`tabPurchase Order`.`per_received`", "`tabPurchase Order`.`supplier_name`",
                "`tabPurchase Order`.`base_grand_total`", "`tabPurchase Order`.`company`", "`tabPurchase Order`.`currency`",
                "`tabPurchase Order`.`supplier`", "`tabPurchase Order`.`advance_payment_status`",
                "`tabPurchase Order`.`party_account_currency`"
            ));
            requestBody.put("order_by", "`tabPurchase Order`.creation desc");
            requestBody.put("start", 0);
            requestBody.put("page_length", 20);
            requestBody.put("view", "List");
            requestBody.put("with_comment_count", 1);

            // Ajouter le fournisseur sélectionné dans les filtres
            Map<String, Object> filters = new HashMap<>();
            filters.put("supplier", selectedSupplier);
            requestBody.put("filters", filters);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Make the request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse the response
            String responseBody = response.getBody();
            JsonNode messageNode = objectMapper.readTree(responseBody).get("message");
            JsonNode keysNode = messageNode.get("keys");
            JsonNode valuesNode = messageNode.get("values");

            List<PurchaseOrder> purchaseOrders = new ArrayList<>();
            if (keysNode!=null) {
                if (keysNode.isArray() && valuesNode.isArray()) {
                    List<String> keys = objectMapper.convertValue(keysNode, new TypeReference<List<String>>() {});
                    for (JsonNode valueArray : valuesNode) {
                        Map<String, Object> purchaseOrderMap = new HashMap<>();
                        for (int i = 0; i < keys.size(); i++) {
                            purchaseOrderMap.put(keys.get(i), valueArray.get(i).asText());
                        }
                        PurchaseOrder purchaseOrder = objectMapper.convertValue(purchaseOrderMap, PurchaseOrder.class);
                        purchaseOrders.add(purchaseOrder);
                    }
                }
            }
            

            if (purchaseOrders.isEmpty()) {
                model.addAttribute("message", "Aucune commande d'achat trouvée.");
            }

            // Add the list of Purchase Orders to the model
            model.addAttribute("orders", purchaseOrders);
            return "purchase_orders"; // Return the view "purchase_orders"

        } catch (org.springframework.web.client.HttpClientErrorException.Forbidden e) {
            e.printStackTrace();
            model.addAttribute("error", "Access denied: You do not have permission to access this resource. Please check your session or contact the administrator.");
            return "error"; // Return an error view
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error retrieving data: " + e.getMessage());
            return "error"; // Return an error view
        }
    }
}
