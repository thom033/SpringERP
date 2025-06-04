package com.example.demo.service.RH;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

        System.out.println("Response: " + response.getBody().toPrettyString());
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
}
