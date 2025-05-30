package com.example.demo.controller.ERP;

import com.example.demo.config.CookieUtil;
import com.example.demo.dto.ERP.LoginRequest;
import com.example.demo.dto.ERP.LoginResponse;
import com.example.demo.service.ERP.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(LoginRequest loginRequest, Model model, HttpServletResponse response, HttpSession session) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            
            if (loginResponse.getSid() != null) {
                session.setAttribute("sid", loginResponse.getSid());

                response.addHeader(HttpHeaders.SET_COOKIE, 
                    CookieUtil.createCookie("sid", loginResponse.getSid()).toString());
            }
            if (loginResponse.getUserId() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, 
                    CookieUtil.createCookie("user_id", loginResponse.getUserId()).toString());
            }
            if (loginResponse.getUserLang() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, 
                    CookieUtil.createCookie("user_lang", loginResponse.getUserLang()).toString());
            }
            if (loginResponse.getSystemUser() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, 
                    CookieUtil.createCookie("system_user", loginResponse.getSystemUser()).toString());
            }

            model.addAttribute("loginResponse", loginResponse);
            model.addAttribute("username", loginRequest.getUsr());
            return "suppliers/list";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/check-auth")
    @ResponseBody
    public ResponseEntity<Boolean> checkAuth(@CookieValue(name = "sid", required = false) String sid) {
        return ResponseEntity.ok(sid != null);
    }
}