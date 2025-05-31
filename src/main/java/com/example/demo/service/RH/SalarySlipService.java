package com.example.demo.service.RH;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.SalaryDetailDTO;
import com.example.demo.dto.RH.SalarySlipDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalarySlipService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public SalarySlipService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<SalarySlipDTO> getSalarySlip(String sid){
        List<SalarySlipDTO> salarys = new ArrayList<>();
        try {
            String doctype = "Salary Slip";
            String fields = "[\"*\"]"; // Un tableau vide signifie tous les champs dans Frappe/ERPNext
            String filter = "[]";

            String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter;

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

    public List<SalarySlipDTO> getSalarySlip(String sid,String Employee){
        List<SalarySlipDTO> salarys = new ArrayList<>();
        try {
            String doctype = "Salary Slip";
            String fields = "[\"*\"]"; // Un tableau vide signifie tous les champs dans Frappe/ERPNext
            String filter = "[[\"employee\" , \"=\" , \"" +Employee+"\"]]";

            String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter;

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
}
