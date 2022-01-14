package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Household {


    private String unique_id;
    private String caseworker_name;
    private String screening_location;
    private String biological_children;
    private String first_name;
    private String last_name;
    private String gender;
    private String adolescent_birthdate;
    private String subpop1;
    private String subpop2;
    private String subpop3;
    private String subpop4;
    private String subpop5;
    private String subpop6;
    private String caregiver_name;
    private String caregiver_sex;
    private String caregiver_birth_date;
    private String physical_address;
    private String caregiver_phone;
    private String caregiver_hiv_status;
    private String base_entity_id;
    private String household_id;
    private String village;
    private String user_gps;
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
    private String emergency_name;
    private String e_relationship;
    private String contact_address;
    private String contact_number;



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

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAdolescent_birthdate() {
        return adolescent_birthdate;
    }

    public void setAdolescent_birthdate(String adolescent_birthdate) {
        this.adolescent_birthdate = adolescent_birthdate;
    }

    public String getSubpop1() {
        return subpop1;
    }

    public void setSubpop1(String subpop1) {
        this.subpop1 = subpop1;
    }

    public String getSubpop2() {
        return subpop2;
    }

    public void setSubpop2(String subpop2) {
        this.subpop2 = subpop2;
    }

    public String getSubpop3() {
        return subpop3;
    }

    public void setSubpop3(String subpop3) {
        this.subpop3 = subpop3;
    }

    public String getSubpop4() {
        return subpop4;
    }

    public void setSubpop4(String subpop4) {
        this.subpop4 = subpop4;
    }

    public String getSubpop5() {
        return subpop5;
    }

    public void setSubpop5(String subpop5) {
        this.subpop5 = subpop5;
    }

    public String getSubpop6() {
        return subpop6;
    }

    public void setSubpop6(String subpop6) {
        this.subpop6 = subpop6;
    }

    public String getCaregiver_name() {
        return caregiver_name;
    }

    public void setCaregiver_name(String caregiver_name) {
        this.caregiver_name = caregiver_name;
    }

    public String getCaregiver_sex() {
        return caregiver_sex;
    }

    public void setCaregiver_sex(String caregiver_sex) {
        this.caregiver_sex = caregiver_sex;
    }

    public String getCaregiver_birth_date() {
        return caregiver_birth_date;
    }

    public void setCaregiver_birth_date(String caregiver_birth_date) {
        this.caregiver_birth_date = caregiver_birth_date;
    }

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    public String getCaregiver_phone() {
        return caregiver_phone;
    }

    public void setCaregiver_phone(String caregiver_phone) {
        this.caregiver_phone = caregiver_phone;
    }

    public String getCaregiver_hiv_status() {
        return caregiver_hiv_status;
    }

    public void setCaregiver_hiv_status(String caregiver_hiv_status) {
        this.caregiver_hiv_status = caregiver_hiv_status;
    }

    public String getScreening_location() {
        return screening_location;
    }

    public void setScreening_location(String screening_location) {
        this.screening_location = screening_location;
    }

    public String getBiological_children() {
        return biological_children;
    }

    public void setBiological_children(String biological_children) {
        this.biological_children = biological_children;
    }

    public String getCaseworker_name() {
        return caseworker_name;
    }

    public void setCaseworker_name(String caseworker_name) {
        this.caseworker_name = caseworker_name;
    }

    public String getUser_gps() {
        return user_gps;
    }

    public void setUser_gps(String user_gps) {
        this.user_gps = user_gps;
    }



    public String getEmergency_name() {
        return emergency_name;
    }

    public void setEmergency_name(String emergency_name) {
        this.emergency_name = emergency_name;
    }

    public String getE_relationship() {
        return e_relationship;
    }

    public void setE_relationship(String e_relationship) {
        this.e_relationship = e_relationship;
    }

    public String getContact_address() {
        return contact_address;
    }

    public void setContact_address(String contact_address) {
        this.contact_address = contact_address;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }
}
