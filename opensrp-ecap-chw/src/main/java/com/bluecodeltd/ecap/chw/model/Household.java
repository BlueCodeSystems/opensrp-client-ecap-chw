package com.bluecodeltd.ecap.chw.model;

public class Household {


    private String unique_id;
    private String user_select_hiv;
    private String new_caregiver_death_date;
    private String caseworker_name;
    //household_case_status
    private String household_case_status;
    private String screening_location_home;
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
    private String subpop;
    private String caregiver_name;
    private String caregiver_sex;
    private String caregiver_birth_date;
    private String physical_address;
    private String caregiver_phone;
    private String caregiver_hiv_status;
    private String active_on_treatment;
    private String date_started_art;
    private String caregiver_art_number;
    private String relation;
    private String date_next_vl;
    private String viral_load_results;
    private String date_of_last_viral_load;
    private String base_entity_id;
    private String bid;
    private String household_id;
    private String village;
    private String user_gps;
    private String district;
    private String partner;
    private String landmark;
    private String screening_date;
    private String mother_screening_date;
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
    private String secure;
    private String approved_family;
    private String adolescent_village;
    private String approved_by;
    private String is_caregiver_virally_suppressed;
    private String carried_by;
    private String caregiver_education;
    private String marital_status;
    private  String vl_suppressed;
    private  String marriage_partner_name;
    private String highest_grade;
    private String spouse_name;
    private String homeaddress;
    private String at_risk_reasons;
    private String reason_for_hiv_risk;
    private String consent_check_box;
    private String info_editedby;
    private String phone;
    private String date_edited;
    private String ward;
    private String province;
    private String facility;
    private String status;
    private String case_status;
    private String de_registration_date;
    private String de_registration_reason;
    private String transfer_reason;
    private String other_de_registration_reason;
    private String level_mmd;
    private String caregiver_mmd;
    private String new_caregiver_name;
    private String new_caregiver_nrc;
    private String new_caregiver_birth_date;
    private String new_caregiver_sex;
    private String new_relation;
    private String new_caregiver_hiv_status;
    private String new_caregiver_phone;
    private String sub_population;
    private String signature;
    private  String relationship_other;
    private String household_location;

    private String change_caregiver_date;

    private String household_receiving_caseworker;

    private String district_moved_to;

    private String household_receiving_district;
    private String location_moved_to;

    private String household_receiving_facility;

    private String ovc_name;

    public String getHousehold_receiving_caseworker() {
        return household_receiving_caseworker;
    }

    public void setHousehold_receiving_caseworker(String household_receiving_caseworker) {
        this.household_receiving_caseworker = household_receiving_caseworker;
    }

    public String getDistrict_moved_to() {
        return district_moved_to;
    }

    public void setDistrict_moved_to(String district_moved_to) {
        this.district_moved_to = district_moved_to;
    }

    public String getHousehold_receiving_district() {
        return household_receiving_district;
    }

    public void setHousehold_receiving_district(String household_receiving_district) {
        this.household_receiving_district = household_receiving_district;
    }

    public String getNew_caregiver_death_date() {
        return new_caregiver_death_date;
    }

    public void setNew_caregiver_death_date(String new_caregiver_death_date) {
        this.new_caregiver_death_date = new_caregiver_death_date;
    }

    public String getRelationship_other() {
        return relationship_other;
    }

    public void setRelationship_other(String relationship_other) {
        this.relationship_other = relationship_other;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public String getDe_registration_date() {
        return de_registration_date;
    }

    public void setDe_registration_date(String de_registration_date) {
        this.de_registration_date = de_registration_date;
    }

    public String getDe_registration_reason() {
        return de_registration_reason;
    }

    public void setDe_registration_reason(String de_registration_reason) {
        this.de_registration_reason = de_registration_reason;
    }

    public String getTransfer_reason() {
        return transfer_reason;
    }

    public void setTransfer_reason(String transfer_reason) {
        this.transfer_reason = transfer_reason;
    }

    public String getOther_de_registration_reason() {
        return other_de_registration_reason;
    }

    public void setOther_de_registration_reason(String other_de_registration_reason) {
        this.other_de_registration_reason = other_de_registration_reason;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getCaregiver_education() {
        return caregiver_education;
    }

    public void setCaregiver_education(String caregiver_education) {
        this.caregiver_education = caregiver_education;
    }

    public String getCarried_by() {
        return carried_by;
    }

    public void setCarried_by(String carried_by) {
        this.carried_by = carried_by;
    }

    public String getDate_of_last_viral_load() {
        return date_of_last_viral_load;
    }

    public void setDate_of_last_viral_load(String date_of_last_viral_load) {
        this.date_of_last_viral_load = date_of_last_viral_load;
    }

    public String getViral_load_results() {
        return viral_load_results;
    }

    public void setViral_load_results(String viral_load_results) {
        this.viral_load_results = viral_load_results;
    }

    public String getIs_caregiver_virally_suppressed() {
        return is_caregiver_virally_suppressed;
    }

    public void setIs_caregiver_virally_suppressed(String is_caregiver_virally_suppressed) {
        this.is_caregiver_virally_suppressed = is_caregiver_virally_suppressed;
    }

    public String getInfo_editedby() {
        return info_editedby;
    }

    public void setInfo_editedby(String info_editedby) {
        this.info_editedby = info_editedby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
    }

    public String getAdolescent_village() {
        return adolescent_village;
    }

    public void setAdolescent_village(String adolescent_village) {
        this.adolescent_village = adolescent_village;
    }


    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getApproved_family() {
        return approved_family;
    }

    public void setApproved_family(String approved_family) {
        this.approved_family = approved_family;
    }

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

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
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

    public String getSubpop() {
        return subpop;
    }

    public void setSubpop(String subpop) {
        this.subpop = subpop;
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

    public String getScreening_location_home() {
        return screening_location_home;
    }

    public void setScreening_location_home(String screening_location_home) {
        this.screening_location_home = screening_location_home;
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

    public String getActive_on_treatment() {
        return active_on_treatment;
    }

    public void setActive_on_treatment(String active_on_treatment) {
        this.active_on_treatment = active_on_treatment;
    }

    public String getCaregiver_art_number() {
        return caregiver_art_number;
    }

    public void setCaregiver_art_number(String caregiver_art_number) {
        this.caregiver_art_number = caregiver_art_number;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getVl_suppressed() {
        return vl_suppressed;
    }

    public void setVl_suppressed(String vl_suppressed) {
        this.vl_suppressed = vl_suppressed;
    }

    public String getMarriage_partner_name() {
        return marriage_partner_name;
    }

    public void setMarriage_partner_name(String marriage_partner_name) {
        this.marriage_partner_name = marriage_partner_name;
    }

    public String getHighest_grade() {
        return highest_grade;
    }

    public void setHighest_grade(String highest_grade) {
        this.highest_grade = highest_grade;
    }


    public String getSpouse_name() {
        return spouse_name;
    }

    public void setSpouse_name(String spouse_name) {
        this.spouse_name = spouse_name;
    }

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;

    }

    public String getAt_risk_reasons() {
        return at_risk_reasons;
    }

    public void setAt_risk_reasons(String at_risk_reasons) {
        this.at_risk_reasons = at_risk_reasons;
    }

    public String getReason_for_hiv_risk() {
        return reason_for_hiv_risk;
    }

    public void setReason_for_hiv_risk(String reason_for_hiv_risk) {
        this.reason_for_hiv_risk = reason_for_hiv_risk;
    }
    public String getConsent_check_box() {
        return consent_check_box;
    }

    public void setConsent_check_box(String consent_check_box) {
        this.consent_check_box = consent_check_box;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate_edited() {
        return date_edited;
    }

    public void setDate_edited(String date_edited) {
        this.date_edited = date_edited;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getMother_screening_date() {
        return mother_screening_date;
    }

    public void setMother_screening_date(String mother_screening_date) {
        this.mother_screening_date = mother_screening_date;
    }

    public String getHousehold_case_status() {
        return household_case_status;
    }

    public void setHousehold_case_status(String household_case_status) {
        this.household_case_status = household_case_status;
    }

    public String getDate_started_art() {
        return date_started_art;
    }

    public void setDate_started_art(String date_started_art) {
        this.date_started_art = date_started_art;
    }

    public String getDate_next_vl() {
        return date_next_vl;
    }

    public void setDate_next_vl(String date_next_vl) {
        this.date_next_vl = date_next_vl;
    }

    public String getLevel_mmd() {
        return level_mmd;
    }

    public void setLevel_mmd(String level_mmd) {
        this.level_mmd = level_mmd;
    }

    public String getCaregiver_mmd() {
        return caregiver_mmd;
    }

    public void setCaregiver_mmd(String caregiver_mmd) {
        this.caregiver_mmd = caregiver_mmd;
    }

    public String getNew_caregiver_name() {
        return new_caregiver_name;
    }

    public void setNew_caregiver_name(String new_caregiver_name) {
        this.new_caregiver_name = new_caregiver_name;
    }

    public String getNew_caregiver_nrc() {
        return new_caregiver_nrc;
    }

    public void setNew_caregiver_nrc(String new_caregiver_nrc) {
        this.new_caregiver_nrc = new_caregiver_nrc;
    }

    public String getNew_caregiver_birth_date() {
        return new_caregiver_birth_date;
    }

    public void setNew_caregiver_birth_date(String new_caregiver_birth_date) {
        this.new_caregiver_birth_date = new_caregiver_birth_date;
    }

    public String getNew_caregiver_sex() {
        return new_caregiver_sex;
    }

    public void setNew_caregiver_sex(String new_caregiver_sex) {
        this.new_caregiver_sex = new_caregiver_sex;
    }

    public String getNew_relation() {
        return new_relation;
    }

    public void setNew_relation(String new_relation) {
        this.new_relation = new_relation;
    }

    public String getNew_caregiver_hiv_status() {
        return new_caregiver_hiv_status;
    }

    public void setNew_caregiver_hiv_status(String new_caregiver_hiv_status) {
        this.new_caregiver_hiv_status = new_caregiver_hiv_status;
    }

    public String getNew_caregiver_phone() {
        return new_caregiver_phone;
    }

    public void setNew_caregiver_phone(String new_caregiver_phone) {
        this.new_caregiver_phone = new_caregiver_phone;
    }

    public String getSub_population() {
        return sub_population;
    }

    public void setSub_population(String sub_population) {
        this.sub_population = sub_population;
    }

    public String getHousehold_location() {
        return household_location;
    }

    public void setHousehold_location(String household_location) {
        this.household_location = household_location;
    }

    public String getChange_caregiver_date() {
        return change_caregiver_date;
    }

    public void setChange_caregiver_date(String change_caregiver_date) {
        this.change_caregiver_date = change_caregiver_date;
    }

    public String getLocation_moved_to() {
        return location_moved_to;
    }

    public void setLocation_moved_to(String location_moved_to) {
        this.location_moved_to = location_moved_to;
    }

    public String getHousehold_receiving_facility() {
        return household_receiving_facility;
    }

    public void setHousehold_receiving_facility(String household_receiving_facility) {
        this.household_receiving_facility = household_receiving_facility;
    }

    public String getOvc_name() {
        return ovc_name;
    }

    public void setOvc_name(String ovc_name) {
        this.ovc_name = ovc_name;
    }

    public String getUser_select_hiv() {
        return user_select_hiv;
    }

    public void setUser_select_hiv(String user_select_hiv) {
        this.user_select_hiv = user_select_hiv;
    }
}
