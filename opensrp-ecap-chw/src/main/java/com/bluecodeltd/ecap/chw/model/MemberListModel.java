package com.bluecodeltd.ecap.chw.model;

import java.util.List;
import java.util.Map;

public class MemberListModel {
    String username;
    boolean enabled;
    boolean emailVerified;
    String firstName;
    String lastName;
    String email;
    Map<String, String> attributes;
    List<Credentials> credentials;

    public MemberListModel(String username, boolean enabled, boolean emailVerified, String firstName, String lastName, String email, Map<String, String> attributes, List<Credentials> credentials) {
        this.username = username;
        this.enabled = enabled;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.attributes = attributes;
        this.credentials = credentials;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Credentials> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<Credentials> credentials) {
        this.credentials = credentials;
    }
}
