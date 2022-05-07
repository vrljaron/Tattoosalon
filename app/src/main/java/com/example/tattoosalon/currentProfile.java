package com.example.tattoosalon;

public class currentProfile {
    private String name;
    private String email;
    private String phone;

    public currentProfile(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public currentProfile() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
