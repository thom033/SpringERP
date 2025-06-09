package com.example.demo.service.RH;

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

import com.example.demo.dto.RH.SalaryComponentDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalaryComponentService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public SalaryComponentService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<SalaryComponentDTO> getSalaryComponent(String sid){
        String doctype = "Salary Component";
        String fields = "[\"*\"]";
        String filter = "[]";

        String url = baseUrl + "/api/resource/" + doctype + "?fields=" + fields + "&filters=" + filter + "&limit=0";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<SalaryComponentDTO> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
    
        List<SalaryComponentDTO> components = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode component : data) {
                SalaryComponentDTO dto = new SalaryComponentDTO();
                dto.setSalary_component(getTextValue(component, "salary_component"));
                dto.setSalary_component_abbr(getTextValue(component, "salary_component_abbr"));
                dto.setType(getTextValue(component, "type"));
                dto.setFormula(getTextValue(component, "formula"));
                dto.setAmount_based_on_formula(getTextValue(component, "amount_based_on_formula"));
                dto.setDepends_on_payment_days(getTextValue(component, "depends_on_payment_days"));
                dto.setCompany(getTextValue(component, "company"));

                components.add(dto);
            }
        }

        // Afficher les composants pour vérification
        System.out.println("---------------------------------------");
        System.out.println("Salary Components recuperee:");
        for (SalaryComponentDTO component : components) {
            System.out.println("Component: " + component);
        }   
        System.out.println("---------------------------------------");

        return components;
    }

    public void createSalaryComponent(String sid, SalaryComponentDTO salaryComponentDTO) throws Exception {
        String url = baseUrl + "/api/resource/Salary Component";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir le DTO en JSON automatiquement
        HttpEntity<SalaryComponentDTO> entity = new HttpEntity<>(salaryComponentDTO, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        // System.out.println("Response: " + response.getBody().toPrettyString());
        System.out.println("Salary Component created : " + salaryComponentDTO.getSalary_component());
    }

    public SalaryComponentDTO getSalaryComponentByName(String sid, String name) throws Exception {
        String url = baseUrl + "/api/resource/Salary Component/" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            JsonNode.class
        );

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            SalaryComponentDTO dto = new SalaryComponentDTO();
            dto.salary_component = data.has("salary_component") ? data.get("salary_component").asText() : null;
            dto.salary_component_abbr = data.has("abbr") ? data.get("abbr").asText() : null;
            dto.type = data.has("type") ? data.get("type").asText() : null;
            dto.formula = data.has("formula") ? data.get("formula").asText() : null;
            // Ajoute d'autres champs si besoin
            return dto;
        } else {
            throw new Exception("Salary Component not found: " + name);
        }
    }

    public boolean SalaryComponentExist(String sid, String name){
        Boolean exists;
        try {
            getSalaryComponentByName(sid, name);
            exists = true;
        } catch (Exception ex) {
            exists = false;
        }

        return exists;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }

    public static void main(String[] args) {
        // Exemple d'utilisation
        RestTemplate restTemplate = new RestTemplate();
        SalaryComponentService service = new SalaryComponentService(restTemplate);
        String sid = "d68d341979580a61c20a24714271a3409a5284f5a66722f98b0a7850";
        String baseUrl = "http://erpnext.localhost:8000";
        service.baseUrl = baseUrl;
        
        try {
            List<SalaryComponentDTO> components = service.getSalaryComponent(sid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
