package com.example.demo.controller.ERP;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.ERP.WarehouseDTO;
import com.example.demo.service.ERP.WarehouseService;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    private WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService){
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public String formWarehouse(){
        return "warehouse/warehouse-form.html";
    }

    @PostMapping
    public String createWarehouse(
        @CookieValue(name = "sid", required = true) String sid,
        @RequestParam String warehouseName,
        RedirectAttributes redirectAttributes
    ){
        try {
            WarehouseDTO warehouse = new WarehouseDTO();
            warehouse.setWarehouse_name(warehouseName);
            warehouse.setCompany("Bureau International de Consultance en Informatique");

            warehouseService.createWarehouse(sid, warehouse);
            redirectAttributes.addFlashAttribute("success", "Warehouse inserted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'insertion de warehouse': " + e.getMessage());
        }

        return "redirect:/warehouse";
    }
    
}
