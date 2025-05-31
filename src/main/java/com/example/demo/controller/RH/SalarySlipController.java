package com.example.demo.controller.RH;

import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalarySlipDTO;
import com.example.demo.service.RH.EmployeeService;
import com.example.demo.service.RH.SalarySlipService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/salary")
public class SalarySlipController {
    @Autowired
    public SalarySlipService salarySlipService;

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    private SpringTemplateEngine templateEngine;


    @GetMapping("/{EmployeeId}")
    public String showSalarySlip(
        @CookieValue(name = "sid", required = true) String sid,
        @PathVariable String EmployeeId,
        Model model
    ){
        try {
            List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid,EmployeeId);
            EmployeeDTO employee = employeeService.getEmployeeByName(sid, EmployeeId);

            model.addAttribute("salarys", salarys);
            model.addAttribute("employee", employee);
            return "salary/salary-list";
        } catch (Exception e) {
            List<SalarySlipDTO> salarys = List.of();
            model.addAttribute("salarys", salarys);
            return "salary/salary-list";
        }
    }

    @GetMapping("/detail/{doctype}/{employee}/{number}")
    public String showDetails(
        @CookieValue(name = "sid", required = true) String sid,
        @PathVariable String doctype,
        @PathVariable String employee,
        @PathVariable String number,
        Model model
    ){
        String SalaryId = doctype + "/" + employee + "/" + number;
        try {
            SalarySlipDTO salary = salarySlipService.getSalarySlipbyName(sid, SalaryId);
            model.addAttribute("salary", salary);
            return "salary/salary-detail";
        } catch (Exception e) {
            SalarySlipDTO salary = new SalarySlipDTO();
            model.addAttribute("salary", salary);
            return "salary/salary-detail";
        }
    }

    @GetMapping("/export/pdf/{doctype}/{employee}/{number}")
    public void exportPdf(
        @PathVariable String doctype,
        @PathVariable String employee,
        @PathVariable String number,
        @CookieValue(name = "sid") String sid,
        HttpServletResponse response
    ) throws Exception {
        String salaryId = doctype + "/" + employee + "/" + number;

        // Récupère les données comme dans showDetails()
        SalarySlipDTO salary = salarySlipService.getSalarySlipbyName(sid, salaryId);

        // Préparer le HTML avec Thymeleaf
        Context context = new Context();
        context.setVariable("salary", salary);
        String html = templateEngine.process("salary/salary-pdf", context);

        // Générer le PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=salary_" + salary.getName() + ".pdf");

        OutputStream outputStream = response.getOutputStream();
        renderer.createPDF(outputStream);
        outputStream.close();
    }

}
