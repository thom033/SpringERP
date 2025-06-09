package com.example.demo.controller.RH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.RH.EmployeeService;
import com.example.demo.service.RH.ImportService;
import com.example.demo.service.RH.SalaryComponentService;
import com.example.demo.service.RH.SalaryStructureAssignmentService;

@Controller
@RequestMapping("/csv")
public class ImportComtroller {
    @Autowired
    public ImportService importService; 

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public SalaryComponentService salaryComponentService;

    @Autowired SalaryStructureAssignmentService salaryStructureAssignmemtService;
    
    @GetMapping
    public String showImportForm(){
        return "salary/csv-import/import-form";
    }

    @PostMapping
    public String importData(
        @CookieValue(name = "sid", required = true) String sid,
        @RequestParam("employee") MultipartFile employeeCsv,
        @RequestParam("salary_structure") MultipartFile salaryStructureCsv,
        @RequestParam("salary_slip") MultipartFile salarySlipCsv,
        Model model
    ){
        List<String> errors = new ArrayList<>();
        try {
            // Sauvegarde temporaire du fichier employeeCsv
            File employeeTempFile = importService.saveTempFile(employeeCsv, "employee-");
            File salaryTempFile = importService.saveTempFile(salaryStructureCsv, "salary-");
            File assignmentTempFile = importService.saveTempFile(salarySlipCsv, "assignment-");

            // Validation
            importService.validateEmployeeCsv(sid , employeeTempFile.getAbsolutePath(), errors);
            importService.validateAssignment(assignmentTempFile.getAbsolutePath(), errors);

            if (errors.isEmpty()) {
                importService.importData(sid, employeeTempFile.getAbsolutePath(), salaryTempFile.getAbsolutePath(), assignmentTempFile.getAbsolutePath());
                model.addAttribute("message", "SUCCES");
            }

            employeeTempFile.delete();
            salaryTempFile.delete();
            assignmentTempFile.delete();

            model.addAttribute("error", errors);
        } catch (Exception e) {
            errors = List.of("Erreur lors du traitement du fichier : " + e.getMessage());
        }

        model.addAttribute("employeeErrors", errors);
        
        System.out.println("------------------------------------");
        System.out.println("Errors:" + errors);
        System.out.println("------------------------------------");

        System.out.println("------------------------------------");
        System.out.println("Employee:" + employeeCsv.getOriginalFilename());
        System.out.println("Salary Structure : " + salaryStructureCsv.getOriginalFilename());
        System.out.println("Salary Slip:" + salarySlipCsv.getOriginalFilename());
        System.out.println("------------------------------------");
        return "salary/csv-import/import-form";
    }

    @GetMapping("/reset-database")
    public String resetDatabase(
        @CookieValue(name = "sid", required = true) String sid, 
        Model model
        ) {
        try {
            importService.resetDatabase(sid);
            model.addAttribute("message", "Database reset successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to reset database: " + e.getMessage());
        }
        return "salary/csv-import/import-form"; // Redirect to the import form after reset
    }
}
