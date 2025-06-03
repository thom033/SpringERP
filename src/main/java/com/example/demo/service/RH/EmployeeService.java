package com.example.demo.service.RH;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public void createEmployee(String sid, EmployeeDTO employeeDTO) throws Exception {
        String url = baseUrl + "/api/resource/Employee";
        JSONObject json = new JSONObject();
        json.put("doctype", "Employee");
        json.put("first_name", employeeDTO.getFirst_name());
        json.put("last_name", employeeDTO.getLast_name());
        json.put("gender", employeeDTO.getGender());
        json.put("company", employeeDTO.getCompany());
        json.put("ref", employeeDTO.getRef());

        // Convertir les dates en string
        if (employeeDTO.getDate_of_birth() != null) {
            json.put("date_of_birth", employeeDTO.getDate_of_birth().toString()); // yyyy-MM-dd
        }
        if (employeeDTO.getDate_of_joining() != null) {
            json.put("date_of_joining", employeeDTO.getDate_of_joining().toString());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        System.out.println("Response: " + response.getBody().toPrettyString());
    }

    public void saveEmployee(String sid, List<EmployeeDTO> employees) throws Exception {
        for (EmployeeDTO employeeDTO : employees) {
            createEmployee(sid, employeeDTO);
        }
    }

    public List<EmployeeDTO> getEmployeeByRef(String sid, String ref) throws Exception{
        String doctype = "Employee";
        String fields = "[\"*\"]";
        String filter = "[[\"ref\" , \"=\" , \"" + ref + "\"]]";

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
        

        System.out.println("Liste des employés récupérés By REF:");
        for (EmployeeDTO emp : employees) {
            System.out.println(emp);
        }
        
        return employees;
    }
    
    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}
