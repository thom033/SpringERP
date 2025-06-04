package com.example.demo.service.RH;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.SalaryComponentDTO;
import com.example.demo.dto.RH.SalaryStructureDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalaryStructureService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired 
    SalaryComponentService salaryComponentService;

    public SalaryStructureService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void createSalaryStructure(String sid, SalaryStructureDTO salaryStructureDTO) throws Exception {
        String url = baseUrl + "/api/resource/Salary Structure";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convertir le DTO en JSON automatiquement
        HttpEntity<SalaryStructureDTO> entity = new HttpEntity<>(salaryStructureDTO, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
            url,
            entity,
            JsonNode.class
        );

        System.out.println("---------------------------------------");
        System.out.println("Response Salary Strycture: " + response.getBody().toPrettyString());
        System.out.println("---------------------------------------");
    }

    public void saveSalaryStructure(String sid, List<SalaryStructureDTO> structure){
        for (SalaryStructureDTO sal : structure) {
            List<SalaryComponentDTO> earnings = sal.getEarnings();
            List<SalaryComponentDTO> deductions = sal.getDeductions();

            try {
                for (SalaryComponentDTO comp : earnings) {
                    if (!salaryComponentService.SalaryComponentExist(sid, comp.getSalary_component())) salaryComponentService.createSalaryComponent(sid, comp);
                }
                for (SalaryComponentDTO comp : deductions) {
                    if (!salaryComponentService.SalaryComponentExist(sid, comp.getSalary_component())) salaryComponentService.createSalaryComponent(sid, comp);
                }
                createSalaryStructure(sid, sal);
            } catch (Exception e) {
                e.printStackTrace();
            }        
        }
    }
}
