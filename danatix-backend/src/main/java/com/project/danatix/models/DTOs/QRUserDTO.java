package com.project.danatix.models.DTOs;

public class QRUserDTO {

    private String name;
    private String email;

    public QRUserDTO() {
    }

    public QRUserDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
