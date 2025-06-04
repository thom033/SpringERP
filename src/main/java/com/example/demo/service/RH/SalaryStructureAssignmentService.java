package com.example.demo.service.RH;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalaryStructureAssignmentDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalaryStructureAssignmentService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public EmployeeService employeeService;

    public SalaryStructureAssignmentService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void createSalaryStructureAssignement(String sid, SalaryStructureAssignmentDTO salaryStructureAssignment) throws Exception {
        String url = baseUrl + "/api/resource/Salary Structure Assignment";
        
        List<EmployeeDTO> employeeList = employeeService.getEmployeeByRef(sid, salaryStructureAssignment.getEmployee_ref());
        EmployeeDTO employee = employeeList.get(0);

        salaryStructureAssignment.setEmployee(employee.getName());
        salaryStructureAssignment.setCompany(employee.getCompany());

        JSONObject json = new JSONObject();
        json.put("doctype", "Salary Structure Assignment");
        json.put("employee_ref", salaryStructureAssignment.getEmployee_ref());
        json.put("salary_structure", salaryStructureAssignment.getSalary_structure());
        json.put("base", salaryStructureAssignment.getBase());
        json.put("currency", salaryStructureAssignment.getCurrency());
        json.put("company", salaryStructureAssignment.getCompany());
        json.put("employee", salaryStructureAssignment.getEmployee());
        json.put("docstatus", "1");

        // Convertir les dates en string
        if (salaryStructureAssignment.getFrom_date() != null) {
            json.put("from_date", salaryStructureAssignment.getFrom_date().toString()); // yyyy-MM-dd
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

    public void saveAssignments(String sid , List<SalaryStructureAssignmentDTO> assignments){
        for (SalaryStructureAssignmentDTO salaryStructureAssignmentDTO : assignments) {
            try {
                createSalaryStructureAssignement(sid, salaryStructureAssignmentDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
}
