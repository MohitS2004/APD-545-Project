package com.blueharbor.hotel.app;

public enum ViewKey {
    WELCOME("/view/welcome-view.fxml", "Blue Harbor Hotel"),
    LOGIN("/view/login-view.fxml", "Blue Harbor Hotel Suite"),
    ADMIN("/view/admin/admin-dashboard.fxml", "Blue Harbor Operations"),
    KIOSK("/view/kiosk/kiosk-view.fxml", "Blue Harbor Self-Service"),
    FEEDBACK("/view/feedback-view.fxml", "Blue Harbor Feedback");

    private final String fxml;
    private final String title;

    ViewKey(String fxml, String title) {
        this.fxml = fxml;
        this.title = title;
    }

    public String fxml() {
        return fxml;
    }

    public String title() {
        return title;
    }
}
