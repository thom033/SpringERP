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

import com.example.demo.dto.RH.CompanyDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class CompanyService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public CompanyService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    
    public List<CompanyDTO> getCompany(String sid) throws Exception{
        String doctype = "Company";
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
    
        List<CompanyDTO> companies = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode company : data) {
                CompanyDTO dto = new CompanyDTO();
                dto.setName(getTextValue(company, "name"));
                dto.setCompany_name(getTextValue(company, "company_name"));
                dto.setAbbr(getTextValue(company, "abbr"));

                companies.add(dto);
            }
        }
        

        System.out.println("Liste des COMPANY récupérés :");
        for (CompanyDTO comp : companies) {
            System.out.println(comp);
        }
        
        return companies;
    }

    public CompanyDTO getCompanyByName(String sid, String companyName) throws Exception {
        List<CompanyDTO> companies = getCompany(sid);
        for (CompanyDTO company : companies) {
            if (company.getCompany_name().equalsIgnoreCase(companyName)) {
                return company;
            }
        }
        return null;
    }

    public void createCompany(String sid, CompanyDTO companyDTO) throws Exception {
        String url = baseUrl + "/api/resource/Company";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir le DTO en JSON automatiquement
        HttpEntity<CompanyDTO> entity = new HttpEntity<>(companyDTO, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        System.out.println("Response: " + response.getBody().toPrettyString());
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
}
