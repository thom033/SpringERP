package com.example.demo.controller.ERP;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ERP.DashboardService;

import java.util.Map;

@Controller
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/login";
        }
        
        Map<String, Object> dashboardData = dashboardService.getDashboardData(sid);
        model.addAllAttributes(dashboardData);
        
        return "dashboard";
    }
}