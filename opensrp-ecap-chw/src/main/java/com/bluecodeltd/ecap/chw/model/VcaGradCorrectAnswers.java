package com.bluecodeltd.ecap.chw.model;

public class VcaGradCorrectAnswers {
    private String birthdate;
    private String infection_correct;
    private String protect_correct;
    private String prevention_correct;

    public VcaGradCorrectAnswers(String birthdate, String infection_correct, String protect_correct, String prevention_correct) {
        this.birthdate = birthdate;
        this.infection_correct = infection_correct;
        this.protect_correct = protect_correct;
        this.prevention_correct = prevention_correct;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getInfection_correct() {
        return infection_correct;
    }

    public void setInfection_correct(String infection_correct) {
        this.infection_correct = infection_correct;
    }

    public String getProtect_correct() {
        return protect_correct;
    }

    public void setProtect_correct(String protect_correct) {
        this.protect_correct = protect_correct;
    }

    public String getPrevention_correct() {
        return prevention_correct;
    }

    public void setPrevention_correct(String prevention_correct) {
        this.prevention_correct = prevention_correct;
    }
}
