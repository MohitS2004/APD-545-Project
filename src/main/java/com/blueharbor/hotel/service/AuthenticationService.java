package com.blueharbor.hotel.service;

import com.blueharbor.hotel.logging.ActivityLogger;
import com.blueharbor.hotel.model.Administrator;
import com.blueharbor.hotel.model.activity.ActivityAction;
import com.blueharbor.hotel.repository.AdministratorRepository;
import com.blueharbor.hotel.security.CurrentUserSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserSession currentUserSession;
    private final ActivityLogger activityLogger;

    public AuthenticationService(
        AdministratorRepository administratorRepository,
        PasswordEncoder passwordEncoder,
        CurrentUserSession currentUserSession,
        ActivityLogger activityLogger
    ) {
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserSession = currentUserSession;
        this.activityLogger = activityLogger;
    }

    public boolean authenticate(String email, String password) {
        Optional<Administrator> admin = administratorRepository.findByEmail(email);
        if (admin.isPresent()) {
            System.out.println("DEBUG: Found admin: " + email);
            System.out.println("DEBUG: Stored hash: " + admin.get().getPasswordHash());
            System.out.println("DEBUG: Password entered length: " + password.length());
            System.out.println("DEBUG: Match result: " + passwordEncoder.matches(password, admin.get().getPasswordHash()));
            
            if (passwordEncoder.matches(password, admin.get().getPasswordHash())) {
                currentUserSession.setPrincipal(admin.get());
                activityLogger.log(ActivityAction.LOGIN, email, "Administrator", email, "Successful login");
                return true;
            }
        } else {
            System.out.println("DEBUG: Admin not found: " + email);
        }
        activityLogger.log(ActivityAction.LOGIN, email, "Administrator", email, "Failed login");
        return false;
    }

    public void logout() {
        if (currentUserSession.isAuthenticated()) {
            activityLogger.log(ActivityAction.LOGOUT, currentUserSession.getPrincipal().getEmail(),
                "Administrator", currentUserSession.getPrincipal().getEmail(), "Logout");
        }
        currentUserSession.clear();
    }

    public Administrator currentAdmin() {
        return currentUserSession.getPrincipal();
    }
}
