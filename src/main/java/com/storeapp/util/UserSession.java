package com.storeapp.util;

import com.storeapp.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public boolean isEmployee() {
        return currentUser != null && ("cashier".equals(currentUser.getRole()) || "manager".equals(currentUser.getRole()));
    }

    public void clearSession() {
        this.currentUser = null;
    }
}
