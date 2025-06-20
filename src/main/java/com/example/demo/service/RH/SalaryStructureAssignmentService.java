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

import com.example.demo.dto.RH.CompanyDTO;
import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.SalaryStructureAssignmentDTO;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SalaryStructureAssignmentService {
    @Value("${erpnext.base-url}")
    public String baseUrl;

    public final RestTemplate restTemplate;

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public CompanyService companyService;

    public SalaryStructureAssignmentService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<SalaryStructureAssignmentDTO> getAssignmentsbyEmployee(String sid, String EmpName){
        List<SalaryStructureAssignmentDTO> all = getAssignments(sid);

        List<SalaryStructureAssignmentDTO> val = new ArrayList<>();

        for (SalaryStructureAssignmentDTO assignement : all) {
            if (assignement.getEmployee().equals(EmpName)) {
                val.add(assignement);
            }
        }

        return val;
    }

    public List<SalaryStructureAssignmentDTO> getAssignments(String sid){
        System.out.println("MAMPIASA GET SALARY STRUCTURE ASSIGNEMENT");
        List<SalaryStructureAssignmentDTO> assignments = new ArrayList<>();
        try {
            String doctype = "Salary Structure Assignment";
            String fields = "[\"*\"]"; // Un tableau vide signifie tous les champs dans Frappe/ERPNext
            String filter = "[]";

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
                    SalaryStructureAssignmentDTO dto = new SalaryStructureAssignmentDTO();
                    dto.setName(getTextValue(salary, "name"));
                    dto.setEmployee(getTextValue(salary, "employee"));
                    dto.setSalary_structure(getTextValue(salary, "salary_structure"));
                    dto.setFrom_date(LocalDate.parse(getTextValue(salary, "from_date")));
                    dto.setBase(getTextValue(salary, "base"));
                    dto.setCurrency(getTextValue(salary,"currency"));
                    dto.setCompany(getTextValue(salary,"company"));
                    

                    assignments.add(dto);
                }
            }

            System.out.println("Liste des Slary Structure récupérés :");
            for (SalaryStructureAssignmentDTO emp : assignments) {
                System.out.println("SSA Name: " +emp.getName());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Salary Slip : " + e.getMessage());
            e.printStackTrace();
        }
        return assignments;
    }

    public void createSalaryStructureAssignement(String sid, SalaryStructureAssignmentDTO salaryStructureAssignment) throws Exception {
        String url = baseUrl + "/api/resource/Salary Structure Assignment";
        
        List<EmployeeDTO> emps = employeeService.getEmployeeByRef(sid, salaryStructureAssignment.getEmployee_ref());
        EmployeeDTO employee = emps.get(0);

        salaryStructureAssignment.setEmployee(employee.getName());
        salaryStructureAssignment.setCompany(employee.getCompany());

        CompanyDTO company = companyService.getCompanyByName(sid, salaryStructureAssignment.getCompany());

        JSONObject json = new JSONObject();
        json.put("doctype", "Salary Structure Assignment");
        json.put("employee_ref", salaryStructureAssignment.getEmployee_ref());
        json.put("salary_structure", salaryStructureAssignment.getSalary_structure());
        json.put("base", salaryStructureAssignment.getBase());
        json.put("payroll_payable_account", "Payroll Payable - " + company.getAbbr());
        json.put("currency", salaryStructureAssignment.getCurrency());
        json.put("company", company.getCompany_name());
        json.put("employee", salaryStructureAssignment.getEmployee());
        json.put("employee_name", employee.getFirst_name() + " " + employee.getLast_name());
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

        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
        System.out.println("RESPOSNSE CREATE ASSIGNEMENT" + salaryStructureAssignment.getEmployee_ref() + " Salary Structure : "+ salaryStructureAssignment.getSalary_structure());
        // System.out.println("Response: " + response.getBody().toPrettyString());
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");

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

    public boolean isAssignmentValid(String sid, EmployeeDTO employee, String salaryStructure, LocalDate postingDate) {
        try {
            String filters = String.format(
                "[[\"employee\",\"=\",\"%s\"]," +
                "[\"salary_structure\",\"=\",\"%s\"]," +
                "[\"from_date\",\"<=\",\"%s\"]," +
                "[\"docstatus\",\"=\",1]," +
                "[\"company\",\"=\",\"%s\"]]",
                employee.getName(),
                salaryStructure,
                postingDate.toString(),
                employee.getCompany()
            );

            String url = baseUrl + "/api/resource/Salary Structure Assignment?filters=" + filters;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            return response.getBody().get("data").size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // public SalaryStructureAssignmentDTO getLastAssignement(String sid, EmployeeDTO employee, LocalDate postingDate, List<SalaryStructureAssignmentDTO> assignments){
    //     List<SalaryStructureAssignmentDTO> all = getAssignmentsbyEmployee(sid,employee.getName());
    //     List<SalaryStructureAssignmentDTO> before = new ArrayList<>();
    //     for (SalaryStructureAssignmentDTO assignment : all) {
    //         if(assignment.getFrom_date().isBefore(postingDate)){
    //             before.add(assignment);
    //         }
    //     }
    //     return before.getLast();
    // }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
    
    

    public static void main(String[] args) {
        // Exemple d'utilisation
        RestTemplate restTemplate = new RestTemplate();
        SalaryStructureAssignmentService service = new SalaryStructureAssignmentService(restTemplate);
        String sid = "d647ebdb4c147aaac47f38725db32541f14afd57a25a6f370dffa9af";
        String baseUrl = "http://erpnext.localhost:8000";
        service.baseUrl = baseUrl;

        EmployeeService employeeService = new EmployeeService(restTemplate);
        employeeService.baseUrl = baseUrl;

        SalaryStructureService salaryStructureService = new SalaryStructureService(restTemplate);

        // Create a sample EmployeeDTO
        // EmployeeDTO employee = employeeService.getEmployeeByName(sid, "HR-EMP-00119");
        // SalaryStructureDTO salaryStructureDTO = salaryStructureService.getSalaryStructureByName(sid, "SAL-STRUCT-001");
        
        String salaryStructure = "gasy1";
        LocalDate postingDate = LocalDate.now();

        // boolean valid = service.isAssignmentValid(sid, employee, salaryStructure, postingDate);
        // System.out.println("Is assignment valid? " + valid);
    }
}
