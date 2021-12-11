package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Household {

    private String base_entity_id;
    private String village;
    private String district;
    private String screening_date;
    private String violence_six_months;
    private String children_violence_six_months;
    private String enrolled_pmtct;
    private String screened;
    private String enrollment_date;
    private String entry_type;
    private String other_entry_type;
    private String monthly_expenses;
    private String males_less_5;
    private String females_less_5;
    private String males_10_17;
    private String females_10_17;
    private String income;
    private String fam_source_income;
    private String pregnant_women;
    private String beds;
    private String malaria_itns;
    private String household_member_had_malaria;


    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getScreening_date() {
        return screening_date;
    }

    public void setScreening_date(String screening_date) {
        this.screening_date = screening_date;
    }

    public String getViolence_six_months() {
        return violence_six_months;
    }

    public void setViolence_six_months(String violence_six_months) {
        this.violence_six_months = violence_six_months;
    }

    public String getChildren_violence_six_months() {
        return children_violence_six_months;
    }

    public void setChildren_violence_six_months(String children_violence_six_months) {
        this.children_violence_six_months = children_violence_six_months;
    }

    public String getEnrolled_pmtct() {
        return enrolled_pmtct;
    }

    public void setEnrolled_pmtct(String enrolled_pmtct) {
        this.enrolled_pmtct = enrolled_pmtct;
    }

    public String getScreened() {
        return screened;
    }

    public void setScreened(String screened) {
        this.screened = screened;
    }

    public String getHousehold_member_had_malaria() {
        return household_member_had_malaria;
    }

    public void setHousehold_member_had_malaria(String household_member_had_malaria) {
        this.household_member_had_malaria = household_member_had_malaria;
    }

    public String getMalaria_itns() {
        return malaria_itns;
    }

    public void setMalaria_itns(String malaria_itns) {
        this.malaria_itns = malaria_itns;
    }

    public String getPregnant_women() {
        return pregnant_women;
    }

    public void setPregnant_women(String pregnant_women) {
        this.pregnant_women = pregnant_women;
    }

    public String getFam_source_income() {
        return fam_source_income;
    }

    public void setFam_source_income(String fam_source_income) {
        this.fam_source_income = fam_source_income;
    }

    public String getFemales_10_17() {
        return females_10_17;
    }

    public void setFemales_10_17(String females_10_17) {
        this.females_10_17 = females_10_17;
    }

    public String getMales_10_17() {
        return males_10_17;
    }

    public void setMales_10_17(String males_10_17) {
        this.males_10_17 = males_10_17;
    }

    public String getFemales_less_5() {
        return females_less_5;
    }

    public void setFemales_less_5(String females_less_5) {
        this.females_less_5 = females_less_5;
    }

    public String getMales_less_5() {
        return males_less_5;
    }

    public void setMales_less_5(String males_less_5) {
        this.males_less_5 = males_less_5;
    }

    public String getMonthly_expenses() {
        return monthly_expenses;
    }

    public void setMonthly_expenses(String monthly_expenses) {
        this.monthly_expenses = monthly_expenses;
    }

    public String getOther_entry_type() {
        return other_entry_type;
    }

    public void setOther_entry_type(String other_entry_type) {
        this.other_entry_type = other_entry_type;
    }

    public String getEntry_type() {
        return entry_type;
    }

    public void setEntry_type(String entry_type) {
        this.entry_type = entry_type;
    }

    public String getEnrollment_date() {
        return enrollment_date;
    }

    public void setEnrollment_date(String enrollment_date) {
        this.enrollment_date = enrollment_date;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }
}
