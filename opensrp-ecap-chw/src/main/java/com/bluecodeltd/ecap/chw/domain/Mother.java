package com.bluecodeltd.ecap.chw.domain;

import org.smartregister.chw.core.domain.Person;

import java.util.Date;

public class Mother {
    private  String baseEntityID;
    private String deleted;
    private String caregiver_name;

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }



    public String getCaregiver_name() {
        return caregiver_name;
    }

    public void setCaregiver_name(String caregiver_name) {
        this.caregiver_name = caregiver_name;
    }





    public Mother(String baseEntityID, String is_closed, String caregiver_name) {
        this.baseEntityID = baseEntityID;

        this.caregiver_name = caregiver_name;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
