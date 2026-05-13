package com.bluecodeltd.ecap.chw.model;

public class EcClientIndexSummary {
    private String baseEntityId;
    private String householdId;
    private String firstName;
    private String lastName;
    private String gender;
    private String adolescentBirthdate;

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAdolescentBirthdate() {
        return adolescentBirthdate;
    }

    public void setAdolescentBirthdate(String adolescentBirthdate) {
        this.adolescentBirthdate = adolescentBirthdate;
    }
}

