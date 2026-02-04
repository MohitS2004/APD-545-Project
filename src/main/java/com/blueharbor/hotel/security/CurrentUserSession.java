package com.blueharbor.hotel.security;

import com.blueharbor.hotel.model.Administrator;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserSession {

    private Administrator principal;

    public void setPrincipal(Administrator principal) {
        this.principal = principal;
    }

    public Administrator getPrincipal() {
        return principal;
    }

    public boolean isAuthenticated() {
        return principal != null;
    }

    public void clear() {
        principal = null;
    }
}
