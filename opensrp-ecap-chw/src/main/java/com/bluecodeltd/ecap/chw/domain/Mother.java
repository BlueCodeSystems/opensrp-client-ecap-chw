package com.bluecodeltd.ecap.chw.domain;

import org.smartregister.chw.core.domain.Person;

import java.util.Date;

public class Mother {
    public  String baseEntityID;
    public String first_name;
    public String last_name;
    public String middleName;
    public Date dob;

    public Mother(String baseEntityID, String first_name, String last_name, String middleName, Date dob) {
        this.baseEntityID = baseEntityID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.middleName = middleName;
        this.dob = dob;
    }
}
