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

import com.example.demo.dto.ERP.WarehouseDTO;
import com.example.demo.dto.RH.EmployeeDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class EmployeeService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public EmployeeService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<EmployeeDTO> getEmployee(String sid) throws Exception{
        String doctype = "Employee";
        String fields = "[\"*\"]";
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
    
        List<EmployeeDTO> employees = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode employee : data) {
                EmployeeDTO dto = new EmployeeDTO();
                dto.setName(getTextValue(employee, "name"));
                dto.setFirst_name(getTextValue(employee, "first_name"));
                dto.setGender(getTextValue(employee, "gender"));
                dto.setDate_of_birth(LocalDate.parse(getTextValue(employee, "date_of_birth")));
                dto.setDate_of_joining(LocalDate.parse(getTextValue(employee, "date_of_joining")));
                dto.setStatus(getTextValue(employee, "status"));
                dto.setCompany(getTextValue(employee, "company"));
                employees.add(dto);
            }
        }
        

        System.out.println("Liste des employés récupérés :");
        for (EmployeeDTO emp : employees) {
            System.out.println(emp);
        }
        
        return employees;
    }

    public EmployeeDTO getEmployeeByName(String sid, String employeeName){
        EmployeeDTO employee = new EmployeeDTO();
        try {
            String url = baseUrl + "/api/resource/Employee/" + employeeName;

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
    
                employee.setName(getTextValue(data, "name"));
                employee.setFirst_name(getTextValue(data, "first_name"));
                employee.setGender(getTextValue(data, "gender"));
                employee.setDate_of_birth(LocalDate.parse(getTextValue(data, "date_of_birth")));
                employee.setDate_of_joining(LocalDate.parse(getTextValue(data, "date_of_joining")));
                employee.setStatus(getTextValue(data, "status"));
                employee.setCompany(getTextValue(data, "company"));
            }
            

            System.out.println("Employé récupéré by name :");
            System.out.println(employee);

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        
        return employee;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}
