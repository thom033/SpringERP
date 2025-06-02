package com.example.demo.controller.RH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.service.RH.EmployeeService;
import com.example.demo.service.RH.ImportService;
import com.example.demo.service.RH.SalaryComponentService;

@Controller
@RequestMapping("/csv")
public class ImportComtroller {
    @Autowired
    public ImportService importService;

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public SalaryComponentService salaryComponentService;
    
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
            File employeeTempFile = File.createTempFile("employee-", ".csv");
            try (InputStream in = employeeCsv.getInputStream();
                 FileOutputStream out = new FileOutputStream(employeeTempFile)) {
                in.transferTo(out);
            }

            File salaryTempFile = File.createTempFile("salary-", ".csv");
            try (InputStream in = salaryStructureCsv.getInputStream();
                 FileOutputStream out = new FileOutputStream(salaryTempFile)) {
                in.transferTo(out);
            }
            // Validation
            errors = importService.validateEmployeeCsv(sid , employeeTempFile.getAbsolutePath());
            

            if (errors.isEmpty()) {
                List<EmployeeDTO> employees = importService.extractEmployee(employeeTempFile.getAbsolutePath());
                importService.importSalaryStructures(sid, salaryTempFile.getAbsolutePath());
                employeeService.saveEmployee(sid, employees);
            }

            importService.importSalaryStructures(sid, salaryTempFile.getAbsolutePath());

            salaryTempFile.delete();
            employeeTempFile.delete();
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

}
