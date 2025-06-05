package com.example.demo.service.RH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.RH.CompanyDTO;
import com.example.demo.dto.RH.EmployeeDTO;
import com.example.demo.dto.RH.GenderDTO;
import com.example.demo.dto.RH.SalaryComponentAccountDTO;
import com.example.demo.dto.RH.SalaryComponentDTO;
import com.example.demo.dto.RH.SalaryStructureAssignmentDTO;
import com.example.demo.dto.RH.SalaryStructureDTO;
import com.opencsv.CSVReader;

@Service
public class ImportService {
    @Value("${erpnext.base-url}")
    private String baseUrl;

    @Autowired 
    public CompanyService companyService;

    @Autowired
    public GenderService genderService;

    @Autowired
    public SalaryComponentService salaryComponentService;

    @Autowired
    public SalaryStructureService salaryStructureService;

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public SalaryStructureAssignmentService salaryStructureAssignmentService;

    public void validateEmployeeCsv(String sid, String csvFilePath, List<String> errors) {
        String[] requiredHeaders = {"Ref", "Nom", "Prenom", "genre", "Date embauche", "date naissance", "company"};
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                errors.add("Fichier : " +csvFilePath+ "->Le fichier est vide.");
            }
            // Vérifie les en-têtes
            for (int i = 0; i < requiredHeaders.length; i++) {
                if (headers.length <= i || !headers[i].trim().equalsIgnoreCase(requiredHeaders[i])) {
                    errors.add("Fichier : " +csvFilePath+ "->En-tête manquant ou incorrect: " + requiredHeaders[i]);
                }
            }

            String[] fields;
            int row = 1;
            while ((fields = reader.readNext()) != null) {
                row++;
                if (fields.length < requiredHeaders.length) {
                    errors.add("Fichier : " +csvFilePath+ "->Ligne " + row + " incomplète.");
                    continue;
                }
                // Vérifie les dates
                try {
                    dateFormatter.parse(fields[4].trim()); // Date embauche
                } catch (DateTimeParseException e) {
                    errors.add("Fichier : " +csvFilePath+ "->Format de date d'embauche invalide à la ligne " + row + " : " + fields[4]);
                }
                try {
                    dateFormatter.parse(fields[5].trim()); // date naissance
                } catch (DateTimeParseException e) {
                    errors.add("Fichier : " +csvFilePath+ "->Format de date de naissance invalide à la ligne " + row + " : " + fields[5]);
                }
                // Vérifie le genre
                String genre = fields[3].trim();

                // boolean exist_gender = false;
                // List<GenderDTO> genders = genderService.getGender(sid);

                // for (GenderDTO gend : genders) {
                //     if(gend.getGender().equalsIgnoreCase(genre)) exist_gender = true;
                // }

                // if (exist_gender == false) {
                //     GenderDTO newGender = new GenderDTO();
                //     newGender.setGender(genre);

                //     genderService.createGender(sid, newGender);
                // }

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

                    errors.add("Fichier : " +csvFilePath+ "->Company inexistate a la ligne " + row + ": " + fields[6]);
                }
            }
        } catch (Exception e) {
            errors.add("Fichier : " +csvFilePath+ "->Erreur de validation CSV: " + e.getMessage());
        }
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

                String genre = fields[3].trim();
                if (genre.equalsIgnoreCase("Masculin")) {
                    genre = "Male";
                }
                if (genre.equalsIgnoreCase("Feminin")) {
                    genre = "Female";
                }

                employee.setGender(genre);
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

    // public void validateSalaryStructure(String csvFilePath, List<String> errors)

    public List<SalaryStructureDTO> extractSalaryStructure(String sid, String csvFilePath){
        Map<String, List<SalaryComponentDTO>> groupedByStructure = new HashMap<>();
        List<SalaryStructureDTO> val = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext(); // read header
            
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
                String formula = fields[idxValeur].trim();
                String company = fields[idxCompany].trim();

                if (type.equals("earning")) {
                    type = "Earning";
                }
                if (type.equals("deduction")) {
                    type = "Deduction";
                }

                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                System.out.println("Company: " + company);
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                CompanyDTO companyDTO = companyService.getCompanyByName(sid, company);

                List<SalaryComponentAccountDTO> accounts = new ArrayList<>();
                SalaryComponentAccountDTO account = new SalaryComponentAccountDTO();
                account.setCompany(company);
                account.setAccount("Payroll Payable - " + companyDTO.getAbbr());

                accounts.add(account);

                SalaryComponentDTO component = new SalaryComponentDTO(
                    salaryComponent, abbr, type, formula, "1", "0", company, accounts
                );

                addComponent(groupedByStructure, structureName, component);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, List<SalaryComponentDTO>> entry : groupedByStructure.entrySet()) {
            String structureName = entry.getKey();
            List<SalaryComponentDTO> components = entry.getValue();

            SalaryStructureDTO salaryStructure = new SalaryStructureDTO();
            salaryStructure.setName(structureName);
            salaryStructure.setIs_active("Yes");
            salaryStructure.setCompany(csvFilePath);
            salaryStructure.setDocstatus("1");

            List<SalaryComponentDTO> earnings = new ArrayList<>();
            List<SalaryComponentDTO> deductions = new ArrayList<>();

            for (SalaryComponentDTO comp : components) {
                salaryStructure.setCompany(comp.company);

                if (comp.type.equalsIgnoreCase("earning")) {
                    earnings.add(comp);
                } else {
                    deductions.add(comp);
                }
            }

            salaryStructure.setEarnings(earnings);
            salaryStructure.setDeductions(deductions);

            // Attendre que tous les Salary Components existent avant de créer la Salary Structure
            // Petite pause pour laisser ERPNext indexer les nouveaux composants (optionnel, mais utile en cas de latence)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }

            val.add(salaryStructure);

            System.out.println("Structure créée : " + salaryStructure);

            
        }
        return val;
    }

    public void validateAssignment(String csvFilePath, List<String> errors){
        String[] requiredHeaders = {"Mois", "Ref Employe", "Salaire Base", "Salaire"};
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext();
            
            // Vérifie les en-têtes
            for (int i = 0; i < requiredHeaders.length; i++) {
                if (headers.length <= i || !headers[i].trim().equalsIgnoreCase(requiredHeaders[i])) {
                    errors.add("Fichier : " +csvFilePath+ "->En-tête manquant ou incorrect: " + requiredHeaders[i]);
                }
            }

            String[] fields;
            int row = 1;
            while ((fields = reader.readNext()) != null) {
                row++;
                if (fields.length < requiredHeaders.length) {
                    errors.add("Fichier : " +csvFilePath+ "->Ligne " + row + " incomplète.");
                    continue;
                }
                // Vérifie les dates
                try {
                    dateFormatter.parse(fields[0].trim()); // Date embauche
                } catch (DateTimeParseException e) {
                    errors.add("Fichier : " +csvFilePath+ "->Format de \" Posting date \" invalide à la ligne " + row + " : " + fields[0]);
                }
            }
        } catch (Exception e) {
            errors.add("Fichier : " +csvFilePath+ "->Erreur de validation CSV: " + e.getMessage());
        }
    }

    public List<SalaryStructureAssignmentDTO> extractAssignment(String sid, String csvFilePath){
        List<SalaryStructureAssignmentDTO> val = new ArrayList<>();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            reader.readNext(); // skip header
            String[] fields;
            int row = 1;
            while ((fields = reader.readNext()) != null) {
                row++;
                if (fields.length < 4) continue; // skip incomplete lines
                SalaryStructureAssignmentDTO salaryStructureAssignment = new SalaryStructureAssignmentDTO();

                LocalDate from_date;
                try {
                    String from_date_str = LocalDate.parse(fields[0].trim(), inputFormatter).format(outputFormatter);
                    from_date = LocalDate.parse(from_date_str, outputFormatter);
                } catch (Exception e) {
                    System.err.println("Erreur parsing \"from date\" à la ligne " + row + " : " + fields[4] + " (" + e.getMessage() + ")");
                    continue;
                }

                salaryStructureAssignment.setFrom_date(from_date);
                salaryStructureAssignment.setEmployee_ref(fields[1].trim());
                salaryStructureAssignment.setBase(fields[2].trim());
                salaryStructureAssignment.setSalary_structure(fields[3].trim());

                salaryStructureAssignment.setCurrency("ALL");

                val.add(salaryStructureAssignment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (SalaryStructureAssignmentDTO salary_structure_assignment : val) {
            System.out.println("-------------------------------");
            System.out.println("Extract ASSIGNMENT" + salary_structure_assignment);
            System.out.println("-------------------------------");

        }
        
        return val;
    }
    
    public void importData(String sid, String EmployeeCsv, String SalaryCsv, String AssignmentCSV){
        List<EmployeeDTO> employees = extractEmployee(EmployeeCsv);
        List<SalaryStructureDTO> structures = extractSalaryStructure(sid,SalaryCsv);
        List<SalaryStructureAssignmentDTO> assignments = extractAssignment(sid, AssignmentCSV);

        try {
            employeeService.saveEmployee(sid, employees);
            salaryStructureService.saveSalaryStructure(sid, structures);
            salaryStructureAssignmentService.saveAssignments(sid,assignments);
        } catch (Exception e) {
            e.printStackTrace();
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

    public File saveTempFile(MultipartFile multipartFile, String prefix) throws IOException {
        File tempFile = File.createTempFile(prefix, ".csv");
        try (InputStream in = multipartFile.getInputStream();
            FileOutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }
        return tempFile;
    }

    private static void addComponent(Map<String, List<SalaryComponentDTO>> map, String structureName, SalaryComponentDTO component) {
        map.computeIfAbsent(structureName, k -> new ArrayList<>()).add(component);
    }

}
