package com.example.demo.service.RH;

import java.time.LocalDate;
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
import com.example.demo.entity.SalarySlip;
import com.example.demo.entity.SalaryStructureAssignment;
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
            String filter = "[[\"docstatus\",\"=\",1]]";

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
            
            // System.out.println("Liste des Slary Slip récupérés :");
            // for (SalarySlipDTO emp : salarys) {
            //     System.out.println("SalarySLIP Name: " +emp.getName());
            // }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(salarys.size() + " Salary Slip(s) found.");

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
                salary = getSalarySlipbyName(sid, salary.getName());
                completeSalarys.add(salary);
            }
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

            // System.out.println("-------------------------------------");
            // System.out.println("Slary Slip récupérés BY NAME :");
            // System.out.println(dto);
            // System.out.println("--------------------------------------");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
        
    public SalarySlipDTO generateSalarySlip(String sid, SalaryStructureAssignmentDTO assignment){
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        System.out.println("Method: Salary Slip Service : generateSalarySlip List ");
        SalarySlipDTO slip = new SalarySlipDTO();
        try {
            String employeeRef = assignment.getEmployee_ref();
            System.out.println("Employee Reference: " + employeeRef);
            List<EmployeeDTO> emps = employeeService.getEmployeeByRef(sid, employeeRef);
            EmployeeDTO emp = emps.get(0);
            
            String employee_name = emp.getName();
            String employee_full_name = emp.getFirst_name() + " " + emp.getLast_name();
            LocalDate posting_date = assignment.getFrom_date();
            String salary_structure = assignment.getSalary_structure();
            String company = assignment.getCompany();

            slip = new SalarySlipDTO(employee_name,posting_date, salary_structure,company);
            slip.setEmployee_name(employee_full_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(slips);
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        return slip;
    }

    public List<SalarySlipDTO> generateSalarySlip(String sid, List<SalaryStructureAssignmentDTO> assignments) {
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        System.out.println("Method: Salary Slip Service : generateSalarySlip List ");
        List<SalarySlipDTO> slips = new ArrayList<>();
        try {
            
            for (SalaryStructureAssignmentDTO assignment : assignments) {
                SalarySlipDTO slip = generateSalarySlip(sid, assignment);
                slips.add(slip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        return slips;
    }

    public void createSalarySlip(String sid, SalarySlipDTO salarySlipDTO) {
        try {
            String url = baseUrl + "/api/resource/Salary Slip";

            JSONObject json = new JSONObject();
            json.put("doctype", "Salary Slip");
            json.put("employee", salarySlipDTO.getEmployee());
            json.put("employee_name", salarySlipDTO.getEmployee_name());
            json.put("company", salarySlipDTO.getCompany());
            json.put("posting_date", salarySlipDTO.getPosting_date().toString());
            json.put("start_date", salarySlipDTO.getStart_date().toString());
            json.put("end_date", salarySlipDTO.getEnd_date().toString());
            json.put("currency", salarySlipDTO.getCurrency());
            json.put("exchange_rate", salarySlipDTO.getExchange_rate());
            json.put("salary_structure", salarySlipDTO.getSalary_structure());
            json.put("total_working_days", salarySlipDTO.getTotal_working_days());
            json.put("payment_days", salarySlipDTO.getPayment_days());
            json.put("docstatus", "1");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                url,
                entity,
                JsonNode.class
            );

            System.out.println("Slip created:" + salarySlipDTO.getEmployee() + " Pour date:" + salarySlipDTO.getPosting_date());
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du Salary Slip pour " + salarySlipDTO.getEmployee() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveSalarySlip(String sid , List<SalarySlipDTO> salarySlips) {
        try {
            for (SalarySlipDTO salarySlip : salarySlips) {
                createSalarySlip(sid, salarySlip);
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

    public void cancelSalarySlip(String sid, String salarySlipName) {
        String url = baseUrl + "/api/resource/Salary Slip/" + salarySlipName + "?run_method=cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url,
            entity,
            String.class
        );

        System.out.println("Cancel response: " + response.getBody());
    }

    public void deleteSalarySlip(String sid, String salarySlipName) {
        String url = baseUrl + "/api/resource/Salary Slip/" + salarySlipName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            entity,
            String.class
        );

        System.out.println("Delete response: " + response.getBody());
    }

    public List<SalarySlipDTO> getSalarySlipCondition(String sid, String componentNane, String condition, double componentAmount){
        List<SalarySlipDTO> all = getSalarySlip(sid);
        all = completeSalarySlip(sid, all);

        List<SalarySlipDTO> val = new ArrayList<>();

        for (SalarySlipDTO unit : all) {
            for (SalaryDetailDTO detail : unit.getEarnings()) {
                if (componentNane.equals(detail.getSalary_component())) {
                    if (condition.equals(">=")) {
                        if (detail.getAmount() >= componentAmount) {
                            val.add(unit);
                        }
                    }
                    else if(condition.equals("<=")){
                        if (detail.getAmount() <= componentAmount) {
                            val.add(unit);
                        }
                    }
                }
            }
        }

        return val;
    }

    public void cancelList(String sid, List<SalarySlipDTO> list){
        for (SalarySlipDTO slip : list) {
            cancelSalarySlip(sid, slip.getName());
        }
    }

    public void deleteList(String sid, List<SalarySlipDTO> list){
        for (SalarySlipDTO slip : list) {
            deleteSalarySlip(sid, slip.getName());
        }
    }

    public List<SalarySlipDTO> newSalaireBase(List<SalarySlipDTO> slips, double pourcent){
        List<SalarySlipDTO> val = new ArrayList<>(); 
        for (SalarySlipDTO slip : slips) {
            val.add(slip);
        }
        for (SalarySlipDTO slip : val) {
            if (pourcent > 0) {
                for (SalaryDetailDTO detailDTO : slip.getEarnings()) {
                    if (detailDTO.getSalary_component().equals("Salaire Base")) {
                        double baseTaloha = detailDTO.getAmount();
                        double pourcentage = (baseTaloha * pourcent) / 100;
                        double baseVaovao = baseTaloha + pourcentage;
                        detailDTO.setAmount(baseVaovao);
                    }
                }
            }
            else if (pourcent < 0) {
                for (SalaryDetailDTO detailDTO : slip.getEarnings()) {
                    if (detailDTO.getSalary_component().equals("Salaire Base")) {
                        double baseTaloha = detailDTO.getAmount();
                        pourcent = pourcent * -1;
                        double pourcentage = (baseTaloha * pourcent) / 100;
                        double baseVaovao = baseTaloha - pourcentage;
                        detailDTO.setAmount(baseVaovao);
                        pourcent = pourcent * -1;
                    }
                }
            }
        }
        return val;
    }
    
    public boolean duplicateSalarySlip(List<SalarySlipDTO> slips, SalarySlipDTO slip) {
        for (SalarySlipDTO existingSlip : slips) {
            if (existingSlip.getEmployee().equals(slip.getEmployee()) &&
                String.valueOf(existingSlip.getPosting_date().getMonthValue()).equals(String.valueOf(slip.getPosting_date().getMonthValue()))) {
                System.out.println("Duplicate found for employee: " + existingSlip.getEmployee() + " in month: " + existingSlip.getPosting_date().getMonthValue());
                return true; // Duplicate found
            }
        }
        return false; // No duplicate found
    }
    public static void main(String[] args) {
        // Exemple d'utilisation
        RestTemplate restTemplate = new RestTemplate();
        SalarySlipService service = new SalarySlipService(restTemplate);
        String sid = "d68d341979580a61c20a24714271a3409a5284f5a66722f98b0a7850";
        String baseUrl = "http://erpnext.localhost:8000";
        service.baseUrl = baseUrl;

        // service.printSalarySlip(sid);
    }

}
