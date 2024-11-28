package org.smartregister.chw.core.model;

import android.util.Pair;

import org.smartregister.chw.core.contract.FamilyCallDialogContract;

public class FamilyCallDialogModel implements FamilyCallDialogContract.Model {

    private String Name;
    private String Role;
    private String PhoneNumber;
    private Pair<String,String> otherContact;
    private boolean isIndependent = false;

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setName(String name) {
        Name = name;
    }

    @Override
    public String getRole() {
        return Role;
    }

    @Override
    public void setRole(String role) {
        Role = role;
    }

    @Override
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    @Override
    public boolean isIndependent() {
        return isIndependent;
    }

    @Override
    public void setIndependent(boolean independent) {
        isIndependent = independent;
    }

    @Override
    public Pair<String, String> getOtherContact() {
        return otherContact;
    }

    @Override
    public void setOtherContact(Pair<String, String> otherContact) {
        this.otherContact = otherContact;
    }
}