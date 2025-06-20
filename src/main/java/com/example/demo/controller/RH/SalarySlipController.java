package com.example.demo.controller.RH;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalaryComponentDTO;
import com.example.demo.dto.RH.SalarySlipDTO;
import com.example.demo.dto.RH.SalaryStructureAssignmentDTO;
import com.example.demo.dto.RH.SalaryStructureDTO;
import com.example.demo.service.RH.EmployeeService;
import com.example.demo.service.RH.SalaryComponentService;
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

    @Autowired 
    private SalaryComponentService salaryComponentService;


    @GetMapping("/{EmployeeId}")
    public String showSalarySlipByEmployee(
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

    @GetMapping("/all")
    public String showSalarySlip(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid);
        salarys = salarySlipService.completeSalarySlip(sid, salarys);
        SalarySlipDTO sumTotal = salarySlipService.sumSalarySlip(salarys);
        List<SalaryComponentDTO> components = salaryComponentService.getSalaryComponent(sid);

        model.addAttribute("salarys", salarys);
        model.addAttribute("components", components);
        model.addAttribute("sumTotal", sumTotal);
        return "salary/salary-all";
    }

    @PostMapping("/all")
    public String showSalarySlipMonth(
        @CookieValue(name = "sid", required = true) String sid,
        @RequestParam(required = false) Integer mois,
        @RequestParam(required = false) Integer annee,
        Model model
    ){
        // Valeurs par défaut si non renseignées
        int moisValue = (mois != null) ? mois : 1;
        int anneeValue = (annee != null) ? annee : Year.now().getValue();

        List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid);
        salarys = salarySlipService.completeSalarySlip(sid, salarys);
        List<SalaryComponentDTO> components = salaryComponentService.getSalaryComponent(sid);
        salarys = salarySlipService.getSalarySlipByMonth(sid, salarys, moisValue, anneeValue);
        SalarySlipDTO sumTotal = salarySlipService.sumSalarySlip(salarys);

        model.addAttribute("salarys", salarys);
        model.addAttribute("components", components);
        model.addAttribute("sumTotal", sumTotal);
        return "salary/salary-all";
    }

    @GetMapping("/statistic")
    public String showStatistic(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid);
        System.out.println("Ok get all aloha");
        salarys = salarySlipService.completeSalarySlip(sid, salarys);
        salarys = salarySlipService.statistic(sid, salarys, 2025);
        SalarySlipDTO sumTotal = salarySlipService.sumSalarySlip(salarys);
        List<SalaryComponentDTO> components = salaryComponentService.getSalaryComponent(sid);

        model.addAttribute("salarys", salarys);
        model.addAttribute("components", components);
        model.addAttribute("sumTotal", sumTotal);
        model.addAttribute("annee", 2025);
        return "salary/statistic";
    }

    @PostMapping("/statistic")
    public String showStatistic(
        @CookieValue(name = "sid", required = true) String sid,
        @RequestParam(required = false) Integer annee,
        Model model
    ){
        int anneeValue = (annee != null) ? annee : Year.now().getValue();

        List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid);
        salarys = salarySlipService.completeSalarySlip(sid, salarys);
        salarys = salarySlipService.statistic(sid, salarys, anneeValue);
        SalarySlipDTO sumTotal = salarySlipService.sumSalarySlip(salarys);
        List<SalaryComponentDTO> components = salaryComponentService.getSalaryComponent(sid);

        model.addAttribute("salarys", salarys);
        model.addAttribute("components", components);
        model.addAttribute("sumTotal", sumTotal);
        model.addAttribute("annee", anneeValue);
        return "salary/statistic";
    }

    @GetMapping("/graph")
    public String showGraph(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        List<SalarySlipDTO> salarys = salarySlipService.getSalarySlip(sid);
        salarys = salarySlipService.completeSalarySlip(sid, salarys);
        salarys = salarySlipService.statistic(sid, salarys, 2025);
        SalarySlipDTO sumTotal = salarySlipService.sumSalarySlip(salarys);
        List<SalaryComponentDTO> components = salaryComponentService.getSalaryComponent(sid);

        List<Double> net_pay = new ArrayList<>();
        for (SalarySlipDTO slip : salarys) {
            net_pay.add(slip.getNet_pay());
        }

        List<Double> total_earnings = new ArrayList<>();
        for (SalarySlipDTO slip : salarys) {
            total_earnings.add(slip.getTotal_earnings());
        }

        List<Double> total_deductions = new ArrayList<>();
        for (SalarySlipDTO slip : salarys) {
            total_deductions.add(slip.getTotal_deduction());
        }

        model.addAttribute("net_pay", net_pay);
        model.addAttribute("total_earnings", total_earnings);
        model.addAttribute("total_deductions", total_deductions);

        model.addAttribute("salarys", salarys);
        model.addAttribute("components", components);
        model.addAttribute("sumTotal", sumTotal);
        model.addAttribute("annee", 2025);
        return "salary/salary-graph";
    }

    @GetMapping("/generate")
    public String showSalaryForm(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){

        List<EmployeeDTO> employees = new ArrayList<>();
        try {
            employees = employeeService.getEmployee(sid);
        } catch (Exception e) {
            e.printStackTrace();
            employees = List.of();
        }
        

        model.addAttribute("employees", employees);
        return "salary/salary-generate";
    }

    @PostMapping("/generate")
    public String submitSalaryForm(
        @CookieValue(name = "sid", required = true) String sid,
        @RequestParam LocalDate debut, 
        @RequestParam LocalDate fin,
        @RequestParam String name,
        @RequestParam Integer base,
        Model model
    ){

        System.out.println(debut);
        System.out.println(fin);
        System.out.println("emp ======= "+name);
        System.out.println("base ======= "+base);

        int moisDebut = debut.getMonthValue();
        int moisFin = fin.getMonthValue();

        int diff = moisFin - moisDebut;
        System.out.println("MOis deb :" + moisDebut);
        System.out.println("Mois fin : " + moisFin);
        System.out.println(diff);

        try{
            EmployeeDTO emp = employeeService.getEmployeeByName(sid, name);

            // List<SalaryComponentDTO> ear = new ArrayList<>();
            // SalaryComponentDTO salaire_base = salaryComponentService.getSalaryComponentByName(sid, "Salaire Base");
            // ear.add(ear);

            // SalaryStructureDTO struct = new SalaryStructureDTO();
            // struct.setName("Generate");
            // struct.setCompany(emp.getCompany());
            // struct.setIs_active("Yes");
            // struct.setIs_default("Yes");
            // struct.setCurrency("ALL");
            // struct.setDocstatus("1");


            SalaryStructureAssignmentDTO assignemt = new SalaryStructureAssignmentDTO();
            assignemt.setBase(base.toString());
            assignemt.setEmployee(name);
            assignemt.setFrom_date(debut);
            assignemt.setEmployee(emp.getName());
            assignemt.setEmployee_ref(emp.getRef());
            assignemt.setCurrency("ALL");
            assignemt.setCompany(emp.getCompany());

            LocalDate addDate = debut;
            for (int i = 0; i <= diff ; i++) {
                System.out.println("Mandalo " + i);
                
                SalarySlipDTO slip = new SalarySlipDTO(emp.getName(),addDate,"g1",emp.getCompany());
                salarySlipService.createSalarySlip(sid, slip);
                addDate = addDate.plusMonths(1);
                System.out.println("xxxxxxxxx");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        List<EmployeeDTO> employees = new ArrayList<>();
        try {
            employees = employeeService.getEmployee(sid);
        } catch (Exception e) {
            e.printStackTrace();
            employees = List.of();
        }
        

        model.addAttribute("employees", employees);
        return "salary/salary-generate";
    }

    @GetMapping("/update-base")
    public String updateBaseForm(
        @CookieValue(name = "sid", required = true) String sid,
        Model model
    ){
        List<SalaryComponentDTO> components = new ArrayList<>();
        try {
            components = salaryComponentService.getSalaryComponent(sid);
        } catch (Exception e) {
            // TODO: handle exception
        }

        model.addAttribute("components", components);
        return "salary/update-base";
    }
}
