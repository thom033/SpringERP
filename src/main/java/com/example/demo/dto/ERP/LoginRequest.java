package com.example.demo.dto.ERP;

import lombok.Data;

@Data
public class LoginRequest {
    private String usr;
    private String pwd;
    private String cmd = "login";
}