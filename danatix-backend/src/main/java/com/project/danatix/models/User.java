package com.project.danatix.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {  //UserDetails interface important for UsernamePasswordAuthentication

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(unique = true)
    private String email;

    private String password;

    //Role is necessary for UserDetails implementation
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean emailVerified;

    private String emailVerificationToken;

    private Date emailTokenExpiration;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductOrder> ordersList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Purchase> purchaseList = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.emailVerified = false;
    }

    public User(UserDataReceivedDTO userData) {
        this.name = userData.getName();
        this.email = userData.getEmail();
        this.emailVerified = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Date getEmailTokenExpiration() {
        return emailTokenExpiration;
    }

    public void setEmailTokenExpiration(Date emailTokenExpiration) {
        this.emailTokenExpiration = emailTokenExpiration;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ProductOrder> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<ProductOrder> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
