package com.example.demo.controller.ERP;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ERP.StockService;

@Controller
@RequestMapping("/stock")
public class StockController {
    
    private StockService stockService;

    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @GetMapping
    public String ListStockEntry(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        model.addAttribute("pageTitle", "Formulaire de Devis");
        model.addAttribute("content", "quotation-form");
        
        try {
            model.addAttribute("stockEntry", stockService.getStockEntry(sid));
            System.out.println(stockService.getStockEntryByName(sid, "MAT-STE-2025-00009"));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("stockEntry", List.of());
        }
        return "stock/stock-entry-list";
    }

    @GetMapping("/{stockEntryId}")
    public String ListStockEntryByName(
        @CookieValue(name = "sid", required = true) String sid,
        @PathVariable String stockEntryId,
        Model model
    ){
        try {
            model.addAttribute("stockEntry", stockService.getStockEntryByName(sid, stockEntryId));
            return "stock/stock-entry-detail";
        } catch (Exception e) {
            model.addAttribute("stockEntry", List.of());
            return "suppliers/stock-entry-detail";
        }
    }
}
