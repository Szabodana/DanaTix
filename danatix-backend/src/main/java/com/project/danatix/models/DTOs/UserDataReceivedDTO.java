package com.project.danatix.models.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UserDataReceivedDTO {

    @NotNull(message = "Name is required.")
    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Email is required.")
    @NotBlank(message = "Email is required.")
    @Email
    private String email;

    @NotNull(message = "Password is required.")
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;

    public UserDataReceivedDTO() {
    }

    public UserDataReceivedDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
