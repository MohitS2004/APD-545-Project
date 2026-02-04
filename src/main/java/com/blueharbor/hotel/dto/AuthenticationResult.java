package com.blueharbor.hotel.dto;

import com.blueharbor.hotel.model.AdminRole;

public record AuthenticationResult(
    boolean success,
    AdminRole role,
    String email
) {
    public static AuthenticationResult failure() {
        return new AuthenticationResult(false, null, null);
    }
}
