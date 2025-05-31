package com.example.demo.controller.RH;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalarySlipDTO;
import com.example.demo.service.RH.EmployeeService;
import com.example.demo.service.RH.SalarySlipService;

@Controller
@RequestMapping("/employee")
public class EmployeeComtroller {
    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public SalarySlipService salarySlipService;
    
    @GetMapping
    public String showEmployeeList(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        try {
            List<EmployeeDTO> employees = employeeService.getEmployee(sid);
            model.addAttribute("employees", employees);
            return "employee/employee-list";
        } catch (Exception e) {
            List<EmployeeDTO> employees = List.of();
            model.addAttribute("employees", employees);
            return "employee/employee-list";
        }
    }
}
