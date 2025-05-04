package com.dashboard.Dashboard.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.dashboard.Dashboard.session.SessionManager;


public abstract class BaseController {

    @Autowired
    protected SessionManager sessionManager; // Partagé entre tous les contrôleurs

    // Méthode utilitaire pour vérifier si une session est valide
    protected boolean hasValidSession() {
        return sessionManager != null && sessionManager.hasValidSession();
    }

    // Méthode utilitaire pour récupérer le SID
    protected String getSid() {
        return sessionManager.getSid();
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Cookie", "sid=" + getSid());
        return headers;
    }
}