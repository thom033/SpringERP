package com.dashboard.Dashboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.dashboard.Dashboard.session.SessionManager;

@Controller
public class LoginController {
    @Autowired
    private SessionManager sessionManager;
    
    @GetMapping("/")
    public String index(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login"; 
    }

    @PostMapping("/login")
    public String login(@RequestParam("usr") String usr, @RequestParam("pwd") String pwd) {
        try {
            String url = "http://erpnext.localhost:8000/api/method/login";

            RestTemplate restTemplate = new RestTemplate();

            // Préparer le corps JSON
            Map<String, String> body = new HashMap<>();
            body.put("usr", usr);
            body.put("pwd", pwd);

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // Faire la requête POST
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCodeValue() == 200) {
                String sid = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
                if (sid != null && sid.contains("sid=")) {
                    sid = sid.split("sid=")[1].split(";")[0]; // Extraire la valeur du sid
                    sessionManager.setSid(sid); // Stocker le sid dans SessionManager
                }
                return "redirect:/hello"; // Rediriger vers une page "hello world"
            } else {
                System.err.println("Reponse serveur: " + response);

                String errorMessage = response.getBody();
                return "redirect:/login?error="+errorMessage;
            }

        } catch (Exception e) {           
            e.printStackTrace();
            return "redirect:/login?error="+e.getMessage();
        }
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";  
    }

    @GetMapping("/logout")
    public String logout() {
        // Effacer le sid dans le SessionManager
        sessionManager.setSid(null);
    
        // Rediriger vers la page de connexion
        return "redirect:/login";
    }
}
