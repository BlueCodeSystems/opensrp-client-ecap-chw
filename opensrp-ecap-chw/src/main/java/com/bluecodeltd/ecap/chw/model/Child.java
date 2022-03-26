package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Child implements Serializable {

    public static final String ENTITY_ID = "entity_id";

    public Child() {

    }

    public String getVl_check_box() {
        return vl_check_box;
    }

    public void setVl_check_box(String vl_check_box) {
        this.vl_check_box = vl_check_box;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getClient_screened() {
        return client_screened;
    }

    public void setClient_screened(String client_screened) {
        this.client_screened = client_screened;
    }

    public String getClient_result() {
        return client_result;
    }

    public void setClient_result(String client_result) {
        this.client_result = client_result;
    }

    public String getTpt_client_eligibility() {
        return tpt_client_eligibility;
    }

    public void setTpt_client_eligibility(String tpt_client_eligibility) {
        this.tpt_client_eligibility = tpt_client_eligibility;
    }

    public String getTpt_client_initiated() {
        return tpt_client_initiated;
    }

    public void setTpt_client_initiated(String tpt_client_initiated) {
        this.tpt_client_initiated = tpt_client_initiated;
    }

    public void setOther_reason(String other_reason) {
        this.other_reason = other_reason;
    }

    public String getIs_biological_child_of_mother_living_with_hiv() {
        return is_biological_child_of_mother_living_with_hiv;
    }

    public void setIs_biological_child_of_mother_living_with_hiv(String is_biological_child_of_mother_living_with_hiv) {
        this.is_biological_child_of_mother_living_with_hiv = is_biological_child_of_mother_living_with_hiv;
    }

    public void setAdolescent_gender(String adolescent_gender) {
        this.adolescent_gender = adolescent_gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Child(String phone, String caseworker_name, String dated_edited, String vl_check_box, String landmark, String client_screened, String client_result, String tpt_client_eligibility, String tpt_client_initiated, String case_status, String reason, String other_reason, String base_entity_id, String household_id, String unique_id, String first_name, String last_name, String adolescent_gender, String adolescent_birthdate, String subpop1, String subpop2, String subpop3, String subpop4, String subpop5, String subpop6, String is_biological_child_of_mother_living_with_hiv, String date_referred, String date_enrolled, String art_check_box, String art_number, String date_started_art, String date_last_vl, String date_next_vl, String vl_last_result, String vl_suppressed, String child_mmd, String level_mmd, String caregiver_name, String caregiver_birth_date, String caregiver_sex, String caregiver_hiv_status, String relation, String caregiver_phone, String facility, String gender, String relational_id, String index_check_box, String date_removed, String acceptance, String date_screened, String date_hiv_known, String is_hiv_positive, String is_on_hiv_treatment, String adolescent_first_name, String adolescent_last_name, String province, String district, String ward, String adolescent_village, String partner, String is_viral_load_test_results_on_file, String is_tb_screening_results_on_file, String screened_for_malnutrition, String gets_tb_preventive_therapy, String takes_drugs_to_prevent_other_diseases, String less_3, String positive_mother, String is_mother_currently_on_treatment, String mother_art_number, String is_mother_adhering_to_treatment, String is_mother_virally_suppressed, String is_child_hiv_positive, String child_receiving_breastfeeding, String child_tested_for_hiv_inline_with_guidelines, String receives_drugs_to_prevent_hiv_and_other_illnesses, String child_been_screened_for_malnutrition, String child_gets_drugs_to_prevent_tb_hei, String child_enrolled_in_early_childhood_development_program, String school, String other_school, String caregiver_nrc, String vl_next_result, String physical_address, String date_offered_enrollment) {
        this.caseworker_name = caseworker_name;
        this.dated_edited = dated_edited;
        this.vl_check_box = vl_check_box;
        this.landmark = landmark;
        this.client_screened = client_screened;
        this.client_result = client_result;
        this.tpt_client_eligibility = tpt_client_eligibility;
        this.tpt_client_initiated = tpt_client_initiated;
        this.reason = reason;
        this.other_reason = other_reason;
        this.base_entity_id = base_entity_id;
        this.household_id = household_id;
        this.unique_id = unique_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.adolescent_birthdate = adolescent_birthdate;
        this.subpop1 = subpop1;
        this.subpop2 = subpop2;
        this.subpop3 = subpop3;
        this.subpop4 = subpop4;
        this.subpop5 = subpop5;
        this.subpop6 = subpop6;
        this.is_biological_child_of_mother_living_with_hiv = is_biological_child_of_mother_living_with_hiv;
        this.date_referred = date_referred;
        this.date_enrolled = date_enrolled;
        this.art_check_box = art_check_box;
        this.art_number = art_number;
        this.date_started_art = date_started_art;
        this.date_last_vl = date_last_vl;
        this.date_next_vl = date_next_vl;
        this.vl_last_result = vl_last_result;
        this.vl_suppressed = vl_suppressed;
        this.child_mmd = child_mmd;
        this.level_mmd = level_mmd;
        this.caregiver_name = caregiver_name;
        this.caregiver_birth_date = caregiver_birth_date;
        this.caregiver_sex = caregiver_sex;
        this.caregiver_hiv_status = caregiver_hiv_status;
        this.relation = relation;
        this.caregiver_phone = caregiver_phone;
        this.facility = facility;
        this.gender = gender;
        this.adolescent_gender = adolescent_gender;
        this.relational_id = relational_id;
        this.case_status = case_status;
        this.index_check_box = index_check_box;
        this.date_removed = date_removed;
        this.acceptance = acceptance;
        this.date_screened = date_screened;
        this.date_hiv_known = date_hiv_known;
        this.is_hiv_positive = is_hiv_positive;
        this.is_on_hiv_treatment = is_on_hiv_treatment;
        this.adolescent_first_name = adolescent_first_name;
        this.adolescent_last_name = adolescent_last_name;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.adolescent_village = adolescent_village;
        this.partner = partner;
        this.is_viral_load_test_results_on_file = is_viral_load_test_results_on_file;
        this.is_tb_screening_results_on_file = is_tb_screening_results_on_file;
        this.screened_for_malnutrition = screened_for_malnutrition;
        this.gets_tb_preventive_therapy = gets_tb_preventive_therapy;
        this.takes_drugs_to_prevent_other_diseases = takes_drugs_to_prevent_other_diseases;
        this.less_3 = less_3;
        this.positive_mother = positive_mother;
        this.is_mother_currently_on_treatment = is_mother_currently_on_treatment;
        this.mother_art_number = mother_art_number;
        this.is_mother_adhering_to_treatment = is_mother_adhering_to_treatment;
        this.is_mother_virally_suppressed = is_mother_virally_suppressed;
        this.is_child_hiv_positive = is_child_hiv_positive;
        this.child_receiving_breastfeeding = child_receiving_breastfeeding;
        this.child_tested_for_hiv_inline_with_guidelines = child_tested_for_hiv_inline_with_guidelines;
        this.receives_drugs_to_prevent_hiv_and_other_illnesses = receives_drugs_to_prevent_hiv_and_other_illnesses;
        this.child_been_screened_for_malnutrition = child_been_screened_for_malnutrition;
        this.child_gets_drugs_to_prevent_tb_hei = child_gets_drugs_to_prevent_tb_hei;
        this.child_enrolled_in_early_childhood_development_program = child_enrolled_in_early_childhood_development_program;
        this.school =school;
        this.other_school = other_school;
        this.caregiver_nrc =caregiver_nrc;
        this.vl_next_result = vl_next_result;
        this.physical_address = physical_address;
        this.date_offered_enrollment =date_offered_enrollment;
        this.phone = phone;
    }
    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("caseworker_name")
    @Expose
    private String caseworker_name;

    @SerializedName("dated_edited")
    @Expose
    private String dated_edited;

    @SerializedName("vl_check_box")
    @Expose
    private String vl_check_box;

    @SerializedName("landmark")
    @Expose
    private String landmark;

    @SerializedName("client_screened")
    @Expose
    private String client_screened;

    @SerializedName("client_result")
    @Expose
    private String client_result;

    @SerializedName("tpt_client_eligibility")
    @Expose
    private String tpt_client_eligibility;

    @SerializedName("tpt_client_initiated")
    @Expose
    private String tpt_client_initiated;

    @SerializedName("case_status")
    @Expose
    private String case_status;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("other_reason")
    @Expose
    private String other_reason;

    @SerializedName("base_entity_id")
    @Expose
    private String base_entity_id;

    @SerializedName("household_id")
    @Expose
    private String household_id;

    @SerializedName("unique_id")
    @Expose
    private String unique_id;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("adolescent_birthdate")
    @Expose
    private String adolescent_birthdate;

    @SerializedName("subpop1")
    @Expose
    private String subpop1;

    @SerializedName("subpop2")
    @Expose
    private String subpop2;

    @SerializedName("subpop3")
    @Expose
    private String subpop3;

    @SerializedName("subpop4")
    @Expose
    private String subpop4;

    @SerializedName("subpop5")
    @Expose
    private String subpop5;

    @SerializedName("subpop")
    @Expose
    private String subpop6;

    @SerializedName("is_biological_child_of_mother_living_with_hiv")
    @Expose
    private String is_biological_child_of_mother_living_with_hiv;

    @SerializedName("date_referred")
    @Expose
    private String date_referred;

    @SerializedName("date_enrolled")
    @Expose
    private String date_enrolled;

    @SerializedName("art_check_box")
    @Expose
    private String art_check_box;

    @SerializedName("art_number")
    @Expose
    private String art_number;

    @SerializedName("date_started_art")
    @Expose
    private String date_started_art;

    @SerializedName("date_last_vl")
    @Expose
    private String date_last_vl;

    @SerializedName("date_next_vl")
    @Expose
    private String date_next_vl;

    @SerializedName("vl_last_result")
    @Expose
    private String vl_last_result;

    @SerializedName("vl_suppressed")
    @Expose
    private String vl_suppressed;

    @SerializedName("child_mmd")
    @Expose
    private String child_mmd;

    @SerializedName("level_mmd")
    @Expose
    private String level_mmd;

    @SerializedName("caregiver_name")
    @Expose
    private String caregiver_name;

    @SerializedName("caregiver_birth_date")
    @Expose
    private String caregiver_birth_date;

    @SerializedName("caregiver_sex")
    @Expose
    private String caregiver_sex;

    @SerializedName("caregiver_hiv_status")
    @Expose
    private String caregiver_hiv_status;

    @SerializedName("relation")
    @Expose
    private String relation;

    @SerializedName("caregiver_phone")
    @Expose
    private String caregiver_phone;

    @SerializedName("facility")
    @Expose
    private String facility;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("adolescent_gender")
    @Expose
    private String adolescent_gender;

    @SerializedName("relational_id")
    @Expose
    private String relational_id;

    @SerializedName("index_check_box")
    @Expose
    private String index_check_box;

    @SerializedName("date_removed")
    @Expose
    private String date_removed;

    @SerializedName("acceptance")
    @Expose
    private String acceptance;

    @SerializedName("date_screened")
    @Expose
    private String date_screened;

    @SerializedName("date_hiv_known")
    @Expose
    private String date_hiv_known;

    @SerializedName("is_hiv_positive")
    @Expose
    private String is_hiv_positive;

    @SerializedName("is_on_hiv_treatment")
    @Expose
    private String is_on_hiv_treatment;

    @SerializedName("adolescent_first_name")
    @Expose
    private String adolescent_first_name;

    @SerializedName("adolescent_last_name")
    @Expose
    private String adolescent_last_name;

    @SerializedName("province")
    @Expose
    private String province;

    @SerializedName("district")
    @Expose
    private String district;

    @SerializedName("ward")
    @Expose
    private String ward;

    @SerializedName("adolescent_village")
    @Expose
    private String adolescent_village;


    @SerializedName("partner")
    @Expose
    private String partner;


    @SerializedName("is_viral_load_test_results_on_file")
    @Expose
    private String is_viral_load_test_results_on_file;

    @SerializedName("is_tb_screening_results_on_file")
    @Expose
    private String is_tb_screening_results_on_file;

    @SerializedName("screened_for_malnutrition")
    @Expose
    private String screened_for_malnutrition;

    @SerializedName("gets_tb_preventive_therapy")
    @Expose
    private String gets_tb_preventive_therapy;

    @SerializedName("takes_drugs_to_prevent_other_diseases")
    @Expose
    private String takes_drugs_to_prevent_other_diseases;

    @SerializedName("less_3")
    @Expose
    private String less_3;

    @SerializedName("positive_mother")
    @Expose
    private String positive_mother;


    @SerializedName("is_mother_currently_on_treatment")
    @Expose
    private String is_mother_currently_on_treatment;

    @SerializedName("mother_art_number")
    @Expose
    private String mother_art_number;

    @SerializedName("is_mother_adhering_to_treatment")
    @Expose
    private String is_mother_adhering_to_treatment;

    @SerializedName("is_mother_virally_suppressed")
    @Expose
    private String is_mother_virally_suppressed;

    @SerializedName("is_child_hiv_positive")
    @Expose
    private String is_child_hiv_positive;

    @SerializedName("child_receiving_breastfeeding")
    @Expose
    private String child_receiving_breastfeeding;

    @SerializedName("child_tested_for_hiv_inline_with_guidelines")
    @Expose
    private String child_tested_for_hiv_inline_with_guidelines;


    @SerializedName("receives_drugs_to_prevent_hiv_and_other_illnesses")
    @Expose
    private String receives_drugs_to_prevent_hiv_and_other_illnesses;

    @SerializedName("child_been_screened_for_malnutrition")
    @Expose
    private String child_been_screened_for_malnutrition;

    @SerializedName("child_gets_drugs_to_prevent_tb")
    @Expose
    private String child_gets_drugs_to_prevent_tb_hei;

    @SerializedName("caregiver_nrc")
    @Expose
    private String caregiver_nrc;

    @SerializedName("date_offered_enrollment")
    @Expose
    private String date_offered_enrollment;

    @SerializedName("child_enrolled_in_early_childhood_development_program")
    @Expose
    private String child_enrolled_in_early_childhood_development_program;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @SerializedName("school")
    @Expose
    private String school;

    @SerializedName("other_school")
    @Expose
    private String other_school;

    public String getVl_next_result() {
        return vl_next_result;
    }

    public void setVl_next_result(String vl_next_result) {
        this.vl_next_result = vl_next_result;
    }

    @SerializedName("vl_next_result")
    @Expose
    private String vl_next_result;

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    @SerializedName("physical_address")
    @Expose
    private String physical_address;


    public static String getEntityId() {
        return ENTITY_ID;
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

    public String getOther_school() {
        return other_school;
    }

    public void setOther_school(String other_school) {
        this.other_school = other_school;
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

    public String getDate_referred() {
        return date_referred;
    }

    public void setDate_referred(String date_referred) {
        this.date_referred = date_referred;
    }

    public String getDate_enrolled() {
        return date_enrolled;
    }

    public void setDate_enrolled(String date_enrolled) {
        this.date_enrolled = date_enrolled;
    }

    public String getArt_check_box() {
        return art_check_box;
    }

    public void setArt_check_box(String art_check_box) {
        this.art_check_box = art_check_box;
    }

    public String getArt_number() {
        return art_number;
    }

    public void setArt_number(String art_number) {
        this.art_number = art_number;
    }

    public String getDate_started_art() {
        return date_started_art;
    }

    public void setDate_started_art(String date_started_art) {
        this.date_started_art = date_started_art;
    }

    public String getDate_last_vl() {
        return date_last_vl;
    }

    public void setDate_last_vl(String date_last_vl) {
        this.date_last_vl = date_last_vl;
    }

    public String getDate_next_vl() {
        return date_next_vl;
    }

    public void setDate_next_vl(String date_next_vl) {
        this.date_next_vl = date_next_vl;
    }

    public String getVl_last_result() {
        return vl_last_result;
    }

    public void setVl_last_result(String vl_last_result) {
        this.vl_last_result = vl_last_result;
    }

    public String getVl_suppressed() {
        return vl_suppressed;
    }

    public void setVl_suppressed(String vl_suppressed) {
        this.vl_suppressed = vl_suppressed;
    }

    public String getChild_mmd() {
        return child_mmd;
    }

    public void setChild_mmd(String child_mmd) {
        this.child_mmd = child_mmd;
    }

    public String getLevel_mmd() {
        return level_mmd;
    }

    public void setLevel_mmd(String level_mmd) {
        this.level_mmd = level_mmd;
    }

    public String getCaregiver_name() {
        return caregiver_name;
    }

    public void setCaregiver_name(String caregiver_name) {
        this.caregiver_name = caregiver_name;
    }

    public String getCaregiver_birth_date() {
        return caregiver_birth_date;
    }

    public void setCaregiver_birth_date(String caregiver_birth_date) {
        this.caregiver_birth_date = caregiver_birth_date;
    }

    public String getCaregiver_sex() {
        return caregiver_sex;
    }

    public void setCaregiver_sex(String caregiver_sex) {
        this.caregiver_sex = caregiver_sex;
    }

    public String getCaregiver_hiv_status() {
        return caregiver_hiv_status;
    }

    public void setCaregiver_hiv_status(String caregiver_hiv_status) {
        this.caregiver_hiv_status = caregiver_hiv_status;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getCaregiver_phone() {
        return caregiver_phone;
    }

    public void setCaregiver_phone(String caregiver_phone) {
        this.caregiver_phone = caregiver_phone;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }


    public String getRelational_id() {
        return relational_id;
    }


    public void setRelational_id(String relational_id) {
        this.relational_id = relational_id;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public String getIndex_check_box() {
        return index_check_box;
    }

    public void setIndex_check_box(String index_check_box) {
        this.index_check_box = index_check_box;
    }

    public String getDate_offered_enrollment() {
        return date_offered_enrollment;
    }

    public void setDate_offered_enrollment(String date_offered_enrollment) {
        this.date_offered_enrollment = date_offered_enrollment;
    }

    public String getDate_removed() {
        return date_removed;
    }

    public void setDate_removed(String date_removed) {
        this.date_removed = date_removed;
    }

    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }

    public String getDate_screened() {
        return date_screened;
    }

    public void setDate_screened(String date_screened) {
        this.date_screened = date_screened;
    }

    public String getDate_hiv_known() {
        return date_hiv_known;
    }

    public void setDate_hiv_known(String date_hiv_known) {
        this.date_hiv_known = date_hiv_known;
    }

    public String getIs_hiv_positive() {
        return is_hiv_positive;
    }

    public void setIs_hiv_positive(String is_hiv_positive) {
        this.is_hiv_positive = is_hiv_positive;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAdolescent_village() {
        return adolescent_village;
    }

    public void setAdolescent_village(String adolescent_village) {
        this.adolescent_village = adolescent_village;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getIs_viral_load_test_results_on_file() {
        return is_viral_load_test_results_on_file;
    }

    public void setIs_viral_load_test_results_on_file(String is_viral_load_test_results_on_file) {
        this.is_viral_load_test_results_on_file = is_viral_load_test_results_on_file;
    }

    public String getIs_tb_screening_results_on_file() {
        return is_tb_screening_results_on_file;
    }

    public void setIs_tb_screening_results_on_file(String is_tb_screening_results_on_file) {
        this.is_tb_screening_results_on_file = is_tb_screening_results_on_file;
    }

    public String getScreened_for_malnutrition() {
        return screened_for_malnutrition;
    }

    public void setScreened_for_malnutrition(String screened_for_malnutrition) {
        this.screened_for_malnutrition = screened_for_malnutrition;
    }

    public String getGets_tb_preventive_therapy() {
        return gets_tb_preventive_therapy;
    }

    public void setGets_tb_preventive_therapy(String gets_tb_preventive_therapy) {
        this.gets_tb_preventive_therapy = gets_tb_preventive_therapy;
    }

    public String getTakes_drugs_to_prevent_other_diseases() {
        return takes_drugs_to_prevent_other_diseases;
    }

    public void setTakes_drugs_to_prevent_other_diseases(String takes_drugs_to_prevent_other_diseases) {
        this.takes_drugs_to_prevent_other_diseases = takes_drugs_to_prevent_other_diseases;
    }

    public String getLess_3() {
        return less_3;
    }

    public void setLess_3(String less_3) {
        this.less_3 = less_3;
    }

    public String getPositive_mother() {
        return positive_mother;
    }

    public void setPositive_mother(String positive_mother) {
        this.positive_mother = positive_mother;
    }

    public String getIs_mother_currently_on_treatment() {
        return is_mother_currently_on_treatment;
    }

    public void setIs_mother_currently_on_treatment(String is_mother_currently_on_treatment) {
        this.is_mother_currently_on_treatment = is_mother_currently_on_treatment;
    }

    public String getMother_art_number() {
        return mother_art_number;
    }

    public void setMother_art_number(String mother_art_number) {
        this.mother_art_number = mother_art_number;
    }

    public String getIs_mother_adhering_to_treatment() {
        return is_mother_adhering_to_treatment;
    }

    public void setIs_mother_adhering_to_treatment(String is_mother_adhering_to_treatment) {
        this.is_mother_adhering_to_treatment = is_mother_adhering_to_treatment;
    }

    public String getIs_mother_virally_suppressed() {
        return is_mother_virally_suppressed;
    }

    public void setIs_mother_virally_suppressed(String is_mother_virally_suppressed) {
        this.is_mother_virally_suppressed = is_mother_virally_suppressed;
    }

    public String getIs_child_hiv_positive() {
        return is_child_hiv_positive;
    }

    public void setIs_child_hiv_positive(String is_child_hiv_positive) {
        this.is_child_hiv_positive = is_child_hiv_positive;
    }

    public String getChild_receiving_breastfeeding() {
        return child_receiving_breastfeeding;
    }

    public void setChild_receiving_breastfeeding(String child_receiving_breastfeeding) {
        this.child_receiving_breastfeeding = child_receiving_breastfeeding;
    }

    public String getChild_tested_for_hiv_inline_with_guidelines() {
        return child_tested_for_hiv_inline_with_guidelines;
    }

    public void setChild_tested_for_hiv_inline_with_guidelines(String child_tested_for_hiv_inline_with_guidelines) {
        this.child_tested_for_hiv_inline_with_guidelines = child_tested_for_hiv_inline_with_guidelines;
    }

    public String getReceives_drugs_to_prevent_hiv_and_other_illnesses() {
        return receives_drugs_to_prevent_hiv_and_other_illnesses;
    }

    public void setReceives_drugs_to_prevent_hiv_and_other_illnesses(String receives_drugs_to_prevent_hiv_and_other_illnesses) {
        this.receives_drugs_to_prevent_hiv_and_other_illnesses = receives_drugs_to_prevent_hiv_and_other_illnesses;
    }

    public String getChild_been_screened_for_malnutrition() {
        return child_been_screened_for_malnutrition;
    }

    public void setChild_been_screened_for_malnutrition(String child_been_screened_for_malnutrition) {
        this.child_been_screened_for_malnutrition = child_been_screened_for_malnutrition;
    }

    public String getChild_gets_drugs_to_prevent_tb_hei() {
        return child_gets_drugs_to_prevent_tb_hei;
    }

    public void setChild_gets_drugs_to_prevent_tb_hei(String child_gets_drugs_to_prevent_tb_hei) {
        this.child_gets_drugs_to_prevent_tb_hei = child_gets_drugs_to_prevent_tb_hei;
    }

    public String getCaregiver_nrc() {
        return caregiver_nrc;
    }

    public void setCaregiver_nrc(String caregiver_nrc) {
        this.caregiver_nrc = caregiver_nrc;
    }

    public String getChild_enrolled_in_early_childhood_development_program() {
        return child_enrolled_in_early_childhood_development_program;
    }

    public void setChild_enrolled_in_early_childhood_development_program(String child_enrolled_in_early_childhood_development_program) {
        this.child_enrolled_in_early_childhood_development_program = child_enrolled_in_early_childhood_development_program;
    }

    public String getIs_on_hiv_treatment() {
        return is_on_hiv_treatment;
    }

    public void setIs_on_hiv_treatment(String is_on_hiv_treatment) {
        this.is_on_hiv_treatment = is_on_hiv_treatment;
    }

    public String getAdolescent_first_name() {
        return adolescent_first_name;
    }

    public void setAdolescent_first_name(String adolescent_first_name) {
        this.adolescent_first_name = adolescent_first_name;
    }

    public String getAdolescent_last_name() {
        return adolescent_last_name;
    }

    public void setAdolescent_last_name(String adolescent_last_name) {
        this.adolescent_last_name = adolescent_last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBaseEntity_id() {
        return base_entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.base_entity_id = entity_id;
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

    public String getAdolescent_birthdate() {
        return adolescent_birthdate;
    }

    public void setAdolescent_birthdate(String adolescent_birthdate) {
        this.adolescent_birthdate = adolescent_birthdate;
    }

    public String getAdolescent_gender() {
        return adolescent_gender;
    }

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOther_reason() {
        return other_reason;
    }

    public String getCaseworker_name() {
        return caseworker_name;
    }

    public void setCaseworker_name(String caseworker_name) {
        this.caseworker_name = caseworker_name;
    }

    public String getDated_edited() {
        return dated_edited;
    }

    public void setDated_edited(String dated_edited) {
        this.dated_edited = dated_edited;
    }
}
