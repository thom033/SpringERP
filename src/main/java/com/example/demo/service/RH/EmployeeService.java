package com.example.demo.service.RH;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class EmployeeService {
    @Value("${erpnext.base-url}")
    public String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<EmployeeDTO> getEmployee(String sid) throws Exception{
        String doctype = "Employee";
        String fields = "[\"*\"]";
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
    
        List<EmployeeDTO> employees = new ArrayList<>();

        if (response.getBody() != null && response.getBody().has("data")) {
            JsonNode data = response.getBody().get("data");
            for (JsonNode employee : data) {
                EmployeeDTO dto = new EmployeeDTO();
                dto.setName(getTextValue(employee, "name"));
                dto.setFirst_name(getTextValue(employee, "first_name"));
                dto.setLast_name(getTextValue(employee, "last_name"));
                dto.setRef(getTextValue(employee, "ref"));
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
            System.out.println("Empname: "+emp.getName()+" empRef: " + emp.getRef() + "first_name: " + emp.getFirst_name() + " last_name: " + emp.getLast_name());
        }
        
        return employees;
    }
    
    public EmployeeDTO getEmployeeByName(String sid, String employeeName) throws Exception {
        try {
            String url = baseUrl + "/api/resource/Employee/" + employeeName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new Exception("Erreur de réponse de l'API: " + response.getStatusCode());
            }

            JsonNode data = response.getBody().get("data");
            if (data == null || data.isNull()) {
                throw new Exception("Employee not found: " + employeeName);
            }

            // Création du DTO
            EmployeeDTO dto = new EmployeeDTO();
            dto.setName(getTextValue(data, "name"));
            dto.setFirst_name(getTextValue(data, "first_name"));
            dto.setLast_name(getTextValue(data, "last_name"));
            dto.setRef(getTextValue(data, "ref"));
            dto.setGender(getTextValue(data, "gender"));
            
            // Gestion des dates nullables
            String dob = getTextValue(data, "date_of_birth");
            if (dob != null) {
                dto.setDate_of_birth(LocalDate.parse(dob));
            }
            
            String doj = getTextValue(data, "date_of_joining");
            if (doj != null) {
                dto.setDate_of_joining(LocalDate.parse(doj));
            }
            
            dto.setStatus(getTextValue(data, "status"));
            dto.setCompany(getTextValue(data, "company"));

            // log.info("Employee retrieved - Name: {}, Ref: {}", dto.getName(), dto.getRef());
            return dto;

        } catch (RestClientException e) {
            throw new Exception("Erreur de communication avec l'API: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            throw new Exception("Format de date invalide dans la réponse", e);
        } catch (Exception e) {
            throw new Exception("Erreur inattendue: " + e.getMessage(), e);
        }
    }
    
    public String getEmployeeFullName(EmployeeDTO employee){
        return employee.getFirst_name() + " " + employee.getLast_name();
    } 

    public List<EmployeeDTO> getEmployeeByRef(String sid, String ref) throws Exception {
        List<EmployeeDTO> allEmployees = getEmployee(sid);
        List<EmployeeDTO> filteredEmployees = new ArrayList<>();
        for (EmployeeDTO employee : allEmployees) {
            if (employee.getRef() != null && employee.getRef().equalsIgnoreCase(ref)) {
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    public List<EmployeeDTO> filterEmployeeByName(String sid, String employeeName, List<EmployeeDTO> list){
        List<EmployeeDTO> val = new ArrayList<>();
        for (EmployeeDTO employee : list) {
            if (getEmployeeFullName(employee).contains(employeeName)) {
                val.add(employee);
            }
        }
        return val;
    }

    public List<EmployeeDTO> filterEmployeeByGender(String sid, String employeeGender, List<EmployeeDTO> list){
        List<EmployeeDTO> val = new ArrayList<>();
        for (EmployeeDTO employee : list) {
            if (employee.getGender().equalsIgnoreCase(employeeGender)) {
                val.add(employee);
            }
        }
        return val;
    }
    
     void createEmployee(String sid, EmployeeDTO employeeDTO) throws Exception {
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

        // System.out.println("Response: " + response.getBody().toPrettyString());
        System.out.println("Employee created w => ref:" + employeeDTO.getRef());
    }

    public void saveEmployee(String sid, List<EmployeeDTO> employees) throws Exception {
        for (EmployeeDTO employeeDTO : employees) {
            createEmployee(sid, employeeDTO);
        }
    }
    
    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }

    public List<Employee> findAll(){
        List<Employee> employees = new ArrayList<>();
        try {
            System.out.println("EmployeeService method : findAll");
            employees = employeeRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("vita tsy nisy exception");
        return employees;
    }

    public static void main(String[] args) {
        // Exemple d'utilisation
        RestTemplate restTemplate = new RestTemplate();
        EmployeeService service = new EmployeeService(restTemplate);
        String sid = "d647ebdb4c147aaac47f38725db32541f14afd57a25a6f370dffa9af";
        String baseUrl = "http://erpnext.localhost:8000";
        service.baseUrl = baseUrl;

        List<Employee> employees = service.findAll();
        for (Employee employee : employees) {
            System.out.println("Employee name :" + employee.getName());
        }
    }
}
