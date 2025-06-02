package com.example.demo.dto.RH;
import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeeDTO {
    String ref;
    String name;
    String first_name;
    String last_name;
    String gender;
    LocalDate date_of_birth;
    LocalDate date_of_joining;
    String status; //Active, Inactive, Suspended, Left
    String company;
}
