package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabGender")
public class Gender {

    @Id
    @Column(name = "gender")
    private String gender;

    public Gender() {}

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
