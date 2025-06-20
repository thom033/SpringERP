package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByName(String name); // Corrigé ici
}

