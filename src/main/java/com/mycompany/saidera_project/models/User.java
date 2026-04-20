package com.mycompany.saidera_project.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String registrationDate;
    private String password;

    public User(String id, String name, String email, String role, String registrationDate, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.registrationDate = registrationDate;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getRegistrationDate() { return registrationDate; }
    public String getPassword() { return password; }
}
