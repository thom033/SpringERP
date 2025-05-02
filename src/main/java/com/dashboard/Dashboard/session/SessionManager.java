package com.dashboard.Dashboard.session;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ScopedProxyMode;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS) // Une instance par session utilisateur
public class SessionManager {
    private String sid;

    public boolean hasValidSession() {
        return sid != null && !sid.isEmpty();
    }
}