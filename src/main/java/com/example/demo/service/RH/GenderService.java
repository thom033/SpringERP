package com.example.demo.service.RH;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.GenderDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GenderService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired 
    public CompanyService companyService;

    public GenderService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<GenderDTO> getGender(String sid) throws Exception{
        String doctype = "Gender";
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
    
        List<GenderDTO> genders = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode gender : data) {
                GenderDTO dto = new GenderDTO();
                dto.setGender(getTextValue(gender, "gender"));

                genders.add(dto);
            }
        }
        

        System.out.println("Liste des genres récupérés :");
        for (GenderDTO gend : genders) {
            System.out.println(gend);
        }
        
        return genders;
    }

    public void createGender(String sid, GenderDTO genderDTO) throws Exception {
        String url = baseUrl + "/api/resource/Gender";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir le DTO en JSON automatiquement
        HttpEntity<GenderDTO> entity = new HttpEntity<>(genderDTO, headers);

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
