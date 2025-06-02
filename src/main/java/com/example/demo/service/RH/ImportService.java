package com.example.demo.service.RH;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.RH.CompanyDTO;
import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.GenderDTO;
import com.example.demo.dto.RH.SalaryComponentDTO;
import com.opencsv.CSVReader;

@Service
public class ImportService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Autowired 
    public CompanyService companyService;

    @Autowired
    public GenderService genderService;

    @Autowired
    public SalaryComponentService salaryComponentService;

    public ImportService (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    /**
     * Valide un fichier CSV d'employés et retourne la liste des erreurs.
     * @param csvFilePath chemin du fichier CSV
     * @return liste des erreurs (vide si tout est valide)
     */
    public List<String> validateEmployeeCsv(String sid, String csvFilePath) {
        List<String> errors = new ArrayList<>();
        String[] requiredHeaders = {"Ref", "Nom", "Prenom", "genre", "Date embauche", "date naissance", "company"};
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                errors.add("Le fichier est vide.");
                return errors;
            }
            // Vérifie les en-têtes
            for (int i = 0; i < requiredHeaders.length; i++) {
                if (headers.length <= i || !headers[i].trim().equalsIgnoreCase(requiredHeaders[i])) {
                    errors.add("En-tête manquant ou incorrect: " + requiredHeaders[i]);
                }
            }

            String[] fields;
            int row = 1;
            while ((fields = reader.readNext()) != null) {
                row++;
                if (fields.length < requiredHeaders.length) {
                    errors.add("Ligne " + row + " incomplète.");
                    continue;
                }
                // Vérifie les dates
                try {
                    dateFormatter.parse(fields[4].trim()); // Date embauche
                } catch (DateTimeParseException e) {
                    errors.add("Format de date d'embauche invalide à la ligne " + row + " : " + fields[4]);
                }
                try {
                    dateFormatter.parse(fields[5].trim()); // date naissance
                } catch (DateTimeParseException e) {
                    errors.add("Format de date de naissance invalide à la ligne " + row + " : " + fields[5]);
                }
                // Vérifie le genre
                String genre = fields[3].trim();

                boolean exist_gender = false;
                List<GenderDTO> genders = genderService.getGender(sid);

                for (GenderDTO gend : genders) {
                    if(gend.getGender().equalsIgnoreCase(genre)) exist_gender = true;
                }

                if (exist_gender == false) {
                    GenderDTO newGender = new GenderDTO();
                    newGender.setGender(genre);

                    genderService.createGender(sid, newGender);
                }

                String company = fields[6].trim();
                
                boolean exist_company = false;
                List<CompanyDTO> companies = companyService.getCompany(sid);

                for (CompanyDTO comp : companies) {
                    if(comp.getCompany_name().equalsIgnoreCase(company)) exist_company = true;
                }

                if (exist_company == false) {
                    CompanyDTO newCompany = new CompanyDTO();
                    newCompany.setAbbr(abbreviate(fields[6]));
                    newCompany.setCompany_name(fields[6]);
                    newCompany.setCountry("Madagascar");
                    newCompany.setDefault_currency("ALL");

                    companyService.createCompany(sid, newCompany);

                    errors.add("Company inexistate a la ligne " + row + ": " + fields[6]);
                }
            }
        } catch (Exception e) {
            errors.add("Erreur de validation CSV: " + e.getMessage());
        }
        return errors;
    }

    public List<EmployeeDTO> extractEmployee(String csvFilePath) {
        List<EmployeeDTO> val = new ArrayList<>();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            reader.readNext(); // skip header
            String[] fields;
            int row = 1;
            while ((fields = reader.readNext()) != null) {
                row++;
                if (fields.length < 7) continue; // skip incomplete lines
                EmployeeDTO employee = new EmployeeDTO();
                employee.setRef(fields[0].trim());
                employee.setLast_name(fields[1].trim());
                employee.setFirst_name(fields[2].trim());
                employee.setGender(fields[3].trim());
                // Parse and format dates to yyyy-MM-dd, then set as LocalDate
                LocalDate joining;
                LocalDate birth;
                try {
                    String joiningStr = LocalDate.parse(fields[4].trim(), inputFormatter).format(outputFormatter);
                    joining = LocalDate.parse(joiningStr, outputFormatter);
                } catch (Exception e) {
                    System.err.println("Erreur parsing date d'embauche à la ligne " + row + " : " + fields[4] + " (" + e.getMessage() + ")");
                    continue;
                }
                try {
                    String birthStr = LocalDate.parse(fields[5].trim(), inputFormatter).format(outputFormatter);
                    birth = LocalDate.parse(birthStr, outputFormatter);
                } catch (Exception e) {
                    System.err.println("Erreur parsing date de naissance à la ligne " + row + " : " + fields[5] + " (" + e.getMessage() + ")");
                    continue;
                }
                employee.setDate_of_joining(joining);
                employee.setDate_of_birth(birth);
                employee.setCompany(fields[6].trim());
                if (fields.length > 7) {
                    employee.setStatus(fields[7].trim());
                }
                val.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (EmployeeDTO employeeDTO : val) {
            System.out.println("-------------------------------");
            System.out.println("Extract" + employeeDTO);
            System.out.println("-------------------------------");

        }
        
        return val;
    }

    public void importSalaryStructures(String sid, String csvFilePath) throws Exception {
        Map<String, List<SalaryComponentDTO>> groupedByStructure = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext(); // read header
            if (headers == null) {
                System.err.println("CSV vide ou sans en-tête.");
                return;
            }
            int idxStructure = -1, idxName = -1, idxAbbr = -1, idxType = -1, idxValeur = -1, idxCompany = -1;
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                if (h.equals("salary structure")) idxStructure = i;
                else if (h.equals("name")) idxName = i;
                else if (h.equals("abbr")) idxAbbr = i;
                else if (h.equals("type")) idxType = i;
                else if (h.equals("valeur")) idxValeur = i;
                else if (h.equals("company")) idxCompany = i;
            }
            String[] fields;
            while ((fields = reader.readNext()) != null) {
                String structureName = fields[idxStructure].trim();
                String salaryComponent = fields[idxName].trim();
                String abbr = fields[idxAbbr].trim();
                String type = fields[idxType].trim();
                String valeur = fields[idxValeur].trim();
                String company = fields[idxCompany].trim();

                if (type.equals("earning")) {
                    type = "Earning";
                }
                if (type.equals("deduction")) {
                    type = "Deduction";
                }

                // Correction : la formule doit être calculée pour tous les cas, pas seulement pourcentage+remarque
                String formula = valeur;

                SalaryComponentDTO component = new SalaryComponentDTO(
                    salaryComponent, abbr, type, valeur, formula, "1", "0", company
                );

                groupedByStructure.computeIfAbsent(structureName, k -> new ArrayList<>()).add(component);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, List<SalaryComponentDTO>> entry : groupedByStructure.entrySet()) {
            String structureName = entry.getKey();
            List<SalaryComponentDTO> components = entry.getValue();

            JSONObject structureJson = new JSONObject();
            structureJson.put("doctype", "Salary Structure");
            structureJson.put("name", structureName);
            structureJson.put("is_active", "Yes");
            structureJson.put("company", "My Company");

            JSONArray earnings = new JSONArray();
            JSONArray deductions = new JSONArray();

            for (SalaryComponentDTO comp : components) {
                // Vérifier si le Salary Component existe déjà avant de créer
                boolean exists = false;
                try {
                    salaryComponentService.getSalaryComponentByName(sid, comp.salary_component);
                    exists = true;
                } catch (Exception ex) {
                    exists = false;
                }

                // Créer le Salary Component s'il n'existe pas
                if (!exists) {
                    salaryComponentService.createSalaryComponent(sid, comp);
                }

                JSONObject compJson = new JSONObject();
                compJson.put("salary_component", comp.salary_component);
                compJson.put("abbr", comp.salary_component_abbr);
                compJson.put("amount_based_on_formula", 1);
                compJson.put("formula", comp.formula != null ? comp.formula : "");

                if (comp.type.equalsIgnoreCase("earning")) {
                    earnings.put(compJson);
                } else {
                    deductions.put(compJson);
                }
            }

            structureJson.put("earnings", earnings);
            structureJson.put("deductions", deductions);

            // Attendre que tous les Salary Components existent avant de créer la Salary Structure
            // Petite pause pour laisser ERPNext indexer les nouveaux composants (optionnel, mais utile en cas de latence)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(structureJson.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/resource/Salary Structure",
                entity,
                String.class
            );

            System.out.println("Structure créée : " + response.getBody());
        }
    }

    public static String abbreviate(String phrase) {
        if (phrase == null || phrase.isEmpty()) return "";
        String[] words = phrase.trim().split("\\s+");
        StringBuilder abbreviation = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                abbreviation.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return abbreviation.toString();
    }

}
