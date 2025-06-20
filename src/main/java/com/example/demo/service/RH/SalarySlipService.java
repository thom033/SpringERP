package com.example.demo.service.RH;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalaryDetailDTO;
import com.example.demo.dto.RH.SalarySlipDTO;
import com.example.demo.dto.RH.SalaryStructureAssignmentDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalarySlipService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalaryStructureAssignmentService salaryStructureAssignmentService;

    private final RestTemplate restTemplate;

    public SalarySlipService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    
    public List<SalarySlipDTO> getSalarySlip(String sid){
        System.out.println("MAMPIASA GET SALARY SLIP");
        List<SalarySlipDTO> salarys = new ArrayList<>();
        try {
            String doctype = "Salary Slip";
            String fields = "[\"*\"]"; // Un tableau vide signifie tous les champs dans Frappe/ERPNext
            String filter = "[]";

            String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter + "&limit=0";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );

            if (response.getBody() != null && response.getBody().has("data")) {
                JsonNode data = response.getBody().get("data");
                for (JsonNode salary : data) {
                    SalarySlipDTO dto = new SalarySlipDTO();
                    dto.setName(getTextValue(salary, "name"));
                    dto.setEmployee(getTextValue(salary, "employee"));
                    dto.setEmployee_name(getTextValue(salary, "employee_name"));
                    dto.setCompany(getTextValue(salary, "company"));
                    dto.setPosting_date(LocalDate.parse(getTextValue(salary, "posting_date")));
                    dto.setCurrency(getTextValue(salary, "currency"));
                    dto.setExchange_rate(Double.parseDouble(getTextValue(salary, "exchange_rate")));
                    dto.setSalary_structure(getTextValue(salary, "salary_structure"));
                    dto.setTotal_working_days(Double.parseDouble(getTextValue(salary, "total_working_days")));
                    dto.setPayment_days(Double.parseDouble(getTextValue(salary, "payment_days")));
                    dto.setNet_pay(Double.parseDouble(getTextValue(salary, "net_pay")));
                    dto.setTotal_earnings(Double.parseDouble(getTextValue(salary, "total_earnings")));
                    dto.setTotal_deduction(Double.parseDouble(getTextValue(salary, "total_deduction")));
                    dto.setGross_pay(Double.parseDouble(getTextValue(salary, "gross_pay")));

                    salarys.add(dto);
                }
            }

            System.out.println("Liste des Slary Slip récupérés :");
            for (SalarySlipDTO emp : salarys) {
                System.out.println("SalarySLIP Name: " +emp.getName());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return salarys;
    }
    
    public List<SalarySlipDTO> getSalarySlip(String sid,String Employee){
        List<SalarySlipDTO> salarys = new ArrayList<>();
        try {
            String doctype = "Salary Slip";
            String fields = "[\"*\"]"; // Un tableau vide signifie tous les champs dans Frappe/ERPNext
            String filter = "[[\"employee\" , \"=\" , \"" +Employee+"\"]]";

            String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter + "&limit=0";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );

            if (response.getBody() != null && response.getBody().has("data")) {
                JsonNode data = response.getBody().get("data");
                for (JsonNode salary : data) {
                    SalarySlipDTO dto = new SalarySlipDTO();
                    dto.setName(getTextValue(salary, "name"));
                    dto.setEmployee(getTextValue(salary, "employee"));
                    dto.setEmployee_name(getTextValue(salary, "employee_name"));
                    dto.setCompany(getTextValue(salary, "company"));
                    dto.setPosting_date(LocalDate.parse(getTextValue(salary, "posting_date")));
                    dto.setCurrency(getTextValue(salary, "currency"));
                    dto.setExchange_rate(Double.parseDouble(getTextValue(salary, "exchange_rate")));
                    dto.setSalary_structure(getTextValue(salary, "salary_structure"));
                    dto.setTotal_working_days(Double.parseDouble(getTextValue(salary, "total_working_days")));
                    dto.setPayment_days(Double.parseDouble(getTextValue(salary, "payment_days")));
                    dto.setNet_pay(Double.parseDouble(getTextValue(salary, "net_pay")));
                    dto.setTotal_earnings(Double.parseDouble(getTextValue(salary, "total_earnings")));
                    dto.setTotal_deduction(Double.parseDouble(getTextValue(salary, "total_deduction")));
                    dto.setGross_pay(Double.parseDouble(getTextValue(salary, "gross_pay")));

                    salarys.add(dto);
                }
            }

            System.out.println("Liste des Slary Slip récupérés :");
            for (SalarySlipDTO emp : salarys) {
                System.out.println(emp);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return salarys;
    }

    public List<SalarySlipDTO> completeSalarySlip(String sid, List<SalarySlipDTO> salarys) {
        List<SalarySlipDTO> completeSalarys = new ArrayList<>();
        try {
            for (SalarySlipDTO salary : salarys) {
                SalarySlipDTO dto = getSalarySlipbyName(sid, salary.getName());
                completeSalarys.add(dto);
            }
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("Liste des Slary Slip complétés :");
            for (SalarySlipDTO emp : completeSalarys) {
                System.out.println(emp);
            }
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return completeSalarys;
    }

    public List<SalarySlipDTO> getSalarySlipByMonth(String sid,List<SalarySlipDTO> list, int month, int annee){
        List<SalarySlipDTO> val = new ArrayList<>();
        for (SalarySlipDTO salarySlipDTO : list) {
            int salaryMonth = salarySlipDTO.getPosting_date().getMonthValue();
            int salaryYear = salarySlipDTO.getPosting_date().getYear();
            if (salaryMonth == month && salaryYear == annee) {
                val.add(salarySlipDTO);
            }
        }
        return val;
    }

    public SalarySlipDTO sumSalarySlip(List<SalarySlipDTO> list){
        SalarySlipDTO total = new SalarySlipDTO();
        total.setNet_pay(0);
        total.setTotal_earnings(0);
        total.setTotal_deduction(0);
        total.setGross_pay(0);

        List<SalaryDetailDTO> earnings = new ArrayList<>();
        List<SalaryDetailDTO> deductions = new ArrayList<>();

        for (SalarySlipDTO salary : list) {
            total.setNet_pay(total.getNet_pay() + salary.getNet_pay());
            total.setTotal_earnings(total.getTotal_earnings() + salary.getTotal_earnings());
            total.setTotal_deduction(total.getTotal_deduction() + salary.getTotal_deduction());
            total.setGross_pay(total.getGross_pay() + salary.getGross_pay());

            // Cumuler earnings
            if (salary.getEarnings() != null) {
                for (SalaryDetailDTO list_earning : salary.getEarnings()) {
                    boolean found = false;
                    for (SalaryDetailDTO earnings_earning : earnings) {
                        if (earnings_earning.getSalary_component().equals(list_earning.getSalary_component())) {
                            earnings_earning.setAmount(earnings_earning.getAmount() + list_earning.getAmount());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        SalaryDetailDTO newEarning = new SalaryDetailDTO();
                        newEarning.setSalary_component(list_earning.getSalary_component());
                        newEarning.setAmount(list_earning.getAmount());
                        earnings.add(newEarning);
                    }
                }
            }

            // Cumuler deductions
            if (salary.getDeductions() != null) {
                for (SalaryDetailDTO list_deduction : salary.getDeductions()) {
                    boolean found = false;
                    for (SalaryDetailDTO deduction : deductions) {
                        if (deduction.getSalary_component().equals(list_deduction.getSalary_component())) {
                            deduction.setAmount(deduction.getAmount() + list_deduction.getAmount());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        SalaryDetailDTO newDeduction = new SalaryDetailDTO();
                        newDeduction.setSalary_component(list_deduction.getSalary_component());
                        newDeduction.setAmount(list_deduction.getAmount());
                        deductions.add(newDeduction);
                    }
                }
            }
        }
        total.setCurrency("ALL");
        total.setEarnings(earnings);
        total.setDeductions(deductions);
        return total;
    }
    
    public SalarySlipDTO getSalarySlipbyName(String sid, String salarySlipName){
        SalarySlipDTO dto = new SalarySlipDTO();
        try {
            String url = baseUrl + "/api/resource/Salary Slip/" + salarySlipName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );

            if (response.getBody() != null && response.getBody().has("data")) {
                JsonNode data = response.getBody().get("data");
                
                dto.setName(getTextValue(data, "name"));
                dto.setEmployee(getTextValue(data, "employee"));
                dto.setEmployee_name(getTextValue(data, "employee_name"));
                dto.setCompany(getTextValue(data, "company"));
                dto.setPosting_date(LocalDate.parse(getTextValue(data, "posting_date")));
                dto.setCurrency(getTextValue(data, "currency"));
                dto.setExchange_rate(Double.parseDouble(getTextValue(data, "exchange_rate")));
                dto.setSalary_structure(getTextValue(data, "salary_structure"));
                dto.setTotal_working_days(Double.parseDouble(getTextValue(data, "total_working_days")));
                dto.setPayment_days(Double.parseDouble(getTextValue(data, "payment_days")));
                dto.setNet_pay(Double.parseDouble(getTextValue(data, "net_pay")));
                dto.setTotal_earnings(Double.parseDouble(getTextValue(data, "total_earnings")));

                double amountEarning = 0;
                if (data.has("earnings")) {
                    List<SalaryDetailDTO> earnings = new ArrayList<>();
                    for (JsonNode earning : data.get("earnings")) {
                        SalaryDetailDTO detail = new SalaryDetailDTO();
                        detail.setAmount(Double.parseDouble(getTextValue(earning, "amount")));
                        amountEarning += Double.parseDouble(getTextValue(earning, "amount"));
                        detail.setSalary_component(getTextValue(earning,"salary_component"));
                        earnings.add(detail);
                    }
                    dto.setEarnings(earnings);
                }

                double amountDeduction = 0;
                if (data.has("deductions")) {
                    List<SalaryDetailDTO> deductions = new ArrayList<>();
                    for (JsonNode deduc : data.get("deductions")) {
                        SalaryDetailDTO detail = new SalaryDetailDTO();
                        detail.setAmount(Double.parseDouble(getTextValue(deduc, "amount")));
                        amountDeduction += Double.parseDouble(getTextValue(deduc, "amount"));
                        detail.setSalary_component(getTextValue(deduc,"salary_component"));
                        deductions.add(detail);
                    }
                    dto.setDeductions(deductions);

                }

                dto.setTotal_deduction(amountDeduction);
                dto.setTotal_earnings(amountEarning);
            }

            System.out.println("-------------------------------------");
            System.out.println("Slary Slip récupérés BY NAME :");
            System.out.println(dto);
            System.out.println("--------------------------------------");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
        
    public List<SalarySlipDTO> generateSalarySlip(String sid, List<SalaryStructureAssignmentDTO> assignments) {
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        System.out.println("MAMPIASA GENERATE SLIP");
        List<SalarySlipDTO> slips = new ArrayList<>();
        try {
            
            for (SalaryStructureAssignmentDTO assignment : assignments) {
                String employeeRef = assignment.getEmployee_ref();
                System.out.println("Employee Reference: " + employeeRef);
                List<EmployeeDTO> emps = employeeService.getEmployeeByRef(sid, employeeRef);
                EmployeeDTO emp = emps.get(0);
                
                String employee_name = emp.getName();
                String employee_full_name = emp.getFirst_name() + " " + emp.getLast_name();
                LocalDate posting_date = assignment.getFrom_date();
                String salary_structure = assignment.getSalary_structure();
                String company = assignment.getCompany();

                SalarySlipDTO slip = new SalarySlipDTO(employee_name,posting_date, salary_structure,company);
                slip.setEmployee_name(employee_full_name);

                slips.add(slip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(slips);
        return slips;
    }

    public void createSalarySlip(String sid, SalarySlipDTO salarySlipDTO) {
        String url = baseUrl + "/api/resource/Salary Slip";

        JSONObject json = new JSONObject();
        json.put("doctype", "Salary Slip");
        json.put("employee", salarySlipDTO.getEmployee());
        json.put("employee_name", salarySlipDTO.getEmployee_name());
        json.put("company", salarySlipDTO.getCompany());
        json.put("posting_date", salarySlipDTO.getPosting_date().toString()); // yyyy-MM-dd
        json.put("start_date", salarySlipDTO.getStart_date().toString());     // <-- AJOUTE CETTE LIGNE
        json.put("end_date", salarySlipDTO.getEnd_date().toString());         // <-- ET CELLE-CI
        json.put("currency", salarySlipDTO.getCurrency());
        json.put("exchange_rate", salarySlipDTO.getExchange_rate());
        json.put("salary_structure", salarySlipDTO.getSalary_structure());
        json.put("total_working_days", salarySlipDTO.getTotal_working_days());
        json.put("payment_days", salarySlipDTO.getPayment_days());

        json.put("docstatus", "1");

        // System.out.println("JSON envoyé à l'API : " + json.toString(4)); // Affichage formaté

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        // System.out.println("Response: " + response.getBody().toPrettyString());
        System.out.println("Slip created:" + salarySlipDTO.getEmployee() + " Pour date:"+ salarySlipDTO.getPosting_date());
    }

    public void saveSalarySlip(String sid , List<SalarySlipDTO> salarySlips) {
        try {
            for (SalarySlipDTO salarySlip : salarySlips) {
                createSalarySlip(sid, salarySlip);
                // EmployeeDTO employee = employeeService.getEmployeeByName(sid, salarySlip.getEmployee());
                // String salaryStructure = salarySlip.getSalary_structure();
                // LocalDate postingDate = salarySlip.getPosting_date();
                // if(salaryStructureAssignmentService.isAssignmentValid(sid, employee, salaryStructure,postingDate)){
                //     System.out.println("VALIDDDD Salary Structure Assignment is valid for employee: " + employee.getName());
                //     System.out.println("CREATING Salary Slip creation for employee: " + employee.getName());
                //     createSalarySlip(sid, salarySlip);
                // } else {
                //     System.out.println("Salary Structure Assignment is not valid for employee: " + employee.getName());
                //     System.out.println("Skipping Salary Slip creation for employee: " + employee.getName());
                //     continue;
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SalarySlipDTO> statistic(String sid, List<SalarySlipDTO> list, int annee){
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
        System.out.println("MAPIASA SATISTIC");
        List<SalarySlipDTO> val = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            SalarySlipDTO total = sumSalarySlip(getSalarySlipByMonth(sid, list, i, annee));
            val.add(total);
        }
        System.out.println("taille val: "+val.size());
        System.out.println("VITA NY STATISTIC");
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
        return val;
        
    }

    public void printSalarySlip(String sid){
        String url = baseUrl + "/api/method/hrms.payroll.doctype.salary_slip.salary_slip.get_emp_and_working_day_details";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject json = new JSONObject();
        json.put("salary_slip", "EMP-0001");
        // json.put("posting_date", "2024-05-31");
        // json.put("salary_structure", "SAL-STR-0001");

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            JsonNode.class
        );

        System.out.println(response.getBody());

    }
    public static void main(String[] args) {
        // Exemple d'utilisation
        RestTemplate restTemplate = new RestTemplate();
        SalarySlipService service = new SalarySlipService(restTemplate);
        String sid = "d68d341979580a61c20a24714271a3409a5284f5a66722f98b0a7850";
        String baseUrl = "http://erpnext.localhost:8000";
        service.baseUrl = baseUrl;

        service.printSalarySlip(sid);
    }

}
