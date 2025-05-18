package com.eventBooking.models.user;


public class User {
    private String email;
    private final String username;
    private String password;

    public User(String username, String password, String email) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toFileString() {
        return username + "," + password + "," + email;
    }

    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        return new User(parts[0], parts[1], parts[2]);
    }
}

