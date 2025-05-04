package com.dashboard.Dashboard.controller;

import org.springframework.http.HttpHeaders;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;
import com.dashboard.Dashboard.model.Supplier;
import com.dashboard.Dashboard.service.SupplierService;
import jakarta.servlet.http.HttpSession;

@Controller
public class SupplierController extends BaseController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/supplier")
    public String supplier(Model model) {
        try {
            // Vérifier si le SID est valide
            if (!hasValidSession()) {
                return "redirect:/login?error=session_expired";
            }

            HttpHeaders headers = createHeaders();

            List<Supplier> suppliers = supplierService.getAllSuppliers(headers);
            

            model.addAttribute("activePage", "supplier");
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
