package com.bluecodeltd.ecap.chw.model;

import android.os.Environment;

import com.bluecodeltd.ecap.chw.dao.CaregiverHivAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentUnder15Dao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.ReferralDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GenerateCSVsModel {
    public interface CSVCallback {
        void onSuccess(String filePath);
        void onError(String error);
    }


    public void createCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "households.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("base_entity_id, unique_id, household_id, new_caregiver_death_date, signature, status, date_edited, phone, facility, province, district, ward, household_location, partner, consent_check_box, village, mother_screening_date, screening_date, screening_location_home, caregiver_name, caregiver_nrc, relation, caregiver_sex, caregiver_birth_date, homeaddress, landmark, caregiver_phone, violence_six_months, children_violence_six_months, caregiver_hiv_status, active_on_treatment, date_started_art, caregiver_art_number, is_caregiver_virally_suppressed, date_next_vl, viral_load_results, vl_suppressed, date_of_last_viral_load, caregiver_mmd, level_mmd, biological_children, enrolled_pmtct, at_risk_reasons, reason_for_hiv_risk, is_biological_child_of_mother_living_with_hiv, is_mother_currently_on_treatment_wlhiv, mother_art_number_wlhiv, is_mother_adhering_to_treatment_wlhiv, is_mother_virally_suppressed_wlhiv, secure, carried_by, approved_family, adolescent_village, approved_by, date_approved, highest_grade, marriage_partner_name, spouse_name, relationship_partner_name, screened, enrollment_date, entry_type, other_entry_type, monthly_expenses, males_less_5, females_less_5, males_10_17, females_10_17, fam_source_income, pregnant_women, beds, malaria_itns, household_member_had_malaria, caregiver_education, marital_status, emergency_name, e_relationship, relationship_other, contact_number, case_status, household_case_status, de_registration_date, de_registration_reason, transfer_reason, other_de_registration_reason, reason_for_updating_caregiver, new_caregiver_name, new_caregiver_nrc, new_caregiver_birth_date, new_caregiver_sex, new_relation, new_caregiver_hiv_status, new_caregiver_phone, sub_population, household_receiving_caseworker, district_moved_to\n");
            List<HouseholdCSVModel> householdList = HouseholdDao.getAllHouseholdInDebug();

            if (householdList != null && !householdList.isEmpty()) {

                for (HouseholdCSVModel household : householdList) {
                    fileWriter.append(escapeCsvValue(household.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(household.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_death_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getSignature())).append(",");
                    fileWriter.append(escapeCsvValue(household.getStatus())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDate_edited())).append(",");
                    fileWriter.append(escapeCsvValue(household.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(household.getFacility())).append(",");
                    fileWriter.append(escapeCsvValue(household.getProvince())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDistrict())).append(",");
                    fileWriter.append(escapeCsvValue(household.getWard())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHousehold_location())).append(",");
                    fileWriter.append(escapeCsvValue(household.getPartner())).append(",");
                    fileWriter.append(escapeCsvValue(household.getConsent_check_box())).append(",");
                    fileWriter.append(escapeCsvValue(household.getVillage())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMother_screening_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getScreening_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getScreening_location_home())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_nrc())).append(",");
                    fileWriter.append(escapeCsvValue(household.getRelation())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_sex())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_birth_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHomeaddress())).append(",");
                    fileWriter.append(escapeCsvValue(household.getLandmark())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_phone())).append(",");
                    fileWriter.append(escapeCsvValue(household.getViolence_six_months())).append(",");
                    fileWriter.append(escapeCsvValue(household.getChildren_violence_six_months())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_hiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(household.getActive_on_treatment())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDate_started_art())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_art_number())).append(",");
                    fileWriter.append(escapeCsvValue(household.getIs_caregiver_virally_suppressed())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDate_next_vl())).append(",");
                    fileWriter.append(escapeCsvValue(household.getViral_load_results())).append(",");
                    fileWriter.append(escapeCsvValue(household.getVl_suppressed())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDate_of_last_viral_load())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(household.getLevel_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(household.getBiological_children())).append(",");
                    fileWriter.append(escapeCsvValue(household.getEnrolled_pmtct())).append(",");
                    fileWriter.append(escapeCsvValue(household.getAt_risk_reasons())).append(",");
                    fileWriter.append(escapeCsvValue(household.getReason_for_hiv_risk())).append(",");
                    fileWriter.append(escapeCsvValue(household.getIs_biological_child_of_mother_living_with_hiv())).append(",");
                    fileWriter.append(escapeCsvValue(household.getIs_mother_currently_on_treatment_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMother_art_number_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(household.getIs_mother_adhering_to_treatment_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(household.getIs_mother_virally_suppressed_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(household.getSecure())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCarried_by())).append(",");
                    fileWriter.append(escapeCsvValue(household.getApproved_family())).append(",");
                    fileWriter.append(escapeCsvValue(household.getAdolescent_village())).append(",");
                    fileWriter.append(escapeCsvValue(household.getApproved_by())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDate_approved())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHighest_grade())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMarriage_partner_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getSpouse_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getRelationship_partner_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getScreened())).append(",");
                    fileWriter.append(escapeCsvValue(household.getEnrollment_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getEntry_type())).append(",");
                    fileWriter.append(escapeCsvValue(household.getOther_entry_type())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMonthly_expenses())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMales_less_5())).append(",");
                    fileWriter.append(escapeCsvValue(household.getFemales_less_5())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMales_10_17())).append(",");
                    fileWriter.append(escapeCsvValue(household.getFemales_10_17())).append(",");
                    fileWriter.append(escapeCsvValue(household.getFam_source_income())).append(",");
                    fileWriter.append(escapeCsvValue(household.getPregnant_women())).append(",");
                    fileWriter.append(escapeCsvValue(household.getBeds())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMalaria_itns())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHousehold_member_had_malaria())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCaregiver_education())).append(",");
                    fileWriter.append(escapeCsvValue(household.getMarital_status())).append(",");
                    fileWriter.append(escapeCsvValue(household.getEmergency_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getE_relationship())).append(",");
                    fileWriter.append(escapeCsvValue(household.getRelationship_other())).append(",");
                    fileWriter.append(escapeCsvValue(household.getContact_number())).append(",");
                    fileWriter.append(escapeCsvValue(household.getCase_status())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHousehold_case_status())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDe_registration_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDe_registration_reason())).append(",");
                    fileWriter.append(escapeCsvValue(household.getTransfer_reason())).append(",");
                    fileWriter.append(escapeCsvValue(household.getOther_de_registration_reason())).append(",");
                    fileWriter.append(escapeCsvValue(household.getReason_for_updating_caregiver())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_name())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_nrc())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_birth_date())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_sex())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_relation())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_hiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(household.getNew_caregiver_phone())).append(",");
                    fileWriter.append(escapeCsvValue(household.getSub_population())).append(",");
                    fileWriter.append(escapeCsvValue(household.getHousehold_receiving_caseworker())).append(",");
                    fileWriter.append(escapeCsvValue(household.getDistrict_moved_to())).append("\n");
                }

            }

            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createVcaCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "vcas.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("deleted, household_id, unique_id, signature, date_edited, phone, caseworker_name, province, district, ward, facility, partner, adolescent_first_name, adolescent_last_name, adolescent_birthdate, homeaddress, landmark, adolescent_gender, school, schoolName, other_school, is_hiv_positive, is_on_hiv_treatment, art_number, viral_load_results_on_file, is_tb_screening_results_on_file, client_screened, client_result, tpt_client_eligibility, tpt_client_initiated, screened_for_malnutrition, takes_drugs_to_prevent_other_diseases, less_3, positive_mother, active_on_treatment, caregiver_art_number, adhering_to_treatment, is_mother_virally_suppressed, child_been_tested_for_hiv, child_receiving_breastfeeding, child_tested_for_hiv_inline_with_guidelines, receives_drugs_to_prevent_hiv_and_other_illnesses_hei, child_been_screened_for_malnutrition_hei, child_gets_drugs_to_prevent_tb_hei, child_enrolled_in_early_childhood_development_program, is_biological_child_of_mother_living_with_hiv, child_tested_for_hiv_with_mother_as_index_client, is_mother_currently_on_treatment_wlhiv, mother_art_number_wlhiv, is_mother_adhering_to_treatment_wlhiv, is_mother_virally_suppressed_wlhiv, child_receiving_any_hiv_and_violence_prevention_intervention, agyw_sexually_active, hiv_or_pregnancy_prevention_method_used, hiv_or_pregnancy_prevention_method_used_other, agyw_having_sex_with_older_men, agyw_transactional_sex, agyw_engaged_in_transactional_sex, agwy_engaged_in_sex_work, agyw_food_or_economically_insecure, agyw_marry_early, agyw_give_birth_before_the_age_of_18, agyw_have_a_partner_who_is_violent_or_has_experienced_violence, agyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness, agyw_in_school, agyw_receiving_an_economic_strengthening_intervention, child_ever_experienced_sexual_violence, child_still_living_in_the_same_household_as_the_perpetrator, child_supported_to_seek_justice, did_the_child_access_clinical_care, child_clinical_care_services_received, child_clinical_care_services_received_other, other_child_clinical_care_services_received, is_the_child_caregiver_an_fsw, fsw_child_tested, fsw_child_positive, fsw_prevention_intervention, fsw_economic_strengthening_intervention, date_screened, approved_by, consent_check_box, subpop1, subpop2, subpop3, subpop4, subpop5, screening_location, subpop, first_name, last_name, gender, birthdate, index_check_box, case_status, date_referred, date_offered_enrollment, acceptance, date_enrolled, date_hiv_known, art_check_box, date_started_art, vl_check_box, date_last_vl, vl_last_result, date_next_vl, child_mmd, level_mmd, caregiver_name, caregiver_nrc, caregiver_sex, caregiver_birth_date, caregiver_hiv_status, relation, caregiver_phone, de_registration_date, reason, transfer_reason, other_reason, exited_graduation_reason, abym_years, abym_sexually_active, abym_preventions, abym_preventions_other, abym_sex_older_women, abym_transactional_sex, abym_sex_work, abym_economically_insecure, abym_violent_partner, abym_diagnosed, abym_hiv_tested, abym_test_positive, abym_undergone_vmmc, abym_in_school, abym_economic_strengthening, vca_receiving_caseworker, district_moved_to\n");
            List<VcaCSVModel> vcaList = VCAScreeningDao.getVcaCSV();

            if (vcaList != null && !vcaList.isEmpty()) {

                for (VcaCSVModel vcaCSVModel : vcaList) {
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDeleted())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSignature())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_edited())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaseworker_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getProvince())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDistrict())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getWard())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFacility())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getPartner())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAdolescent_first_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAdolescent_last_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAdolescent_birthdate())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getHomeaddress())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getLandmark())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAdolescent_gender())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSchool())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSchoolName())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getOther_school())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_hiv_positive())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_on_hiv_treatment())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getArt_number())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getViral_load_results_on_file())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_tb_screening_results_on_file())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getClient_screened())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getClient_result())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getTpt_client_eligibility())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getTpt_client_initiated())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getScreened_for_malnutrition())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getTakes_drugs_to_prevent_other_diseases())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getLess_3())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getPositive_mother())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getActive_on_treatment())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_art_number())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAdhering_to_treatment())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_mother_virally_suppressed())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_been_tested_for_hiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_receiving_breastfeeding())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_tested_for_hiv_inline_with_guidelines())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getReceives_drugs_to_prevent_hiv_and_other_illnesses_hei())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_been_screened_for_malnutrition_hei())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_gets_drugs_to_prevent_tb_hei())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_enrolled_in_early_childhood_development_program())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_biological_child_of_mother_living_with_hiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_tested_for_hiv_with_mother_as_index_client())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_mother_currently_on_treatment_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getMother_art_number_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_mother_adhering_to_treatment_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_mother_virally_suppressed_wlhiv())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_receiving_any_hiv_and_violence_prevention_intervention())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_sexually_active())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getHiv_or_pregnancy_prevention_method_used())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getHiv_or_pregnancy_prevention_method_used_other())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_having_sex_with_older_men())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_transactional_sex())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_engaged_in_transactional_sex())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgwy_engaged_in_sex_work())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_food_or_economically_insecure())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_marry_early())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_give_birth_before_the_age_of_18())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_have_a_partner_who_is_violent_or_has_experienced_violence())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_in_school())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAgyw_receiving_an_economic_strengthening_intervention())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_ever_experienced_sexual_violence())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_still_living_in_the_same_household_as_the_perpetrator())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_supported_to_seek_justice())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDid_the_child_access_clinical_care())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_clinical_care_services_received())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_clinical_care_services_received_other())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getOther_child_clinical_care_services_received())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIs_the_child_caregiver_an_fsw())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFsw_child_tested())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFsw_child_positive())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFsw_prevention_intervention())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFsw_economic_strengthening_intervention())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_screened())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getApproved_by())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getConsent_check_box())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop1())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop2())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop3())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop4())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop5())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getScreening_location())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getSubpop())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getFirst_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getLast_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getGender())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getBirthdate())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getIndex_check_box())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCase_status())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_referred())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_offered_enrollment())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAcceptance())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_enrolled())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_hiv_known())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getArt_check_box())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_started_art())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getVl_check_box())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_last_vl())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getVl_last_result())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDate_next_vl())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getChild_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getLevel_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_name())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_nrc())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_sex())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_birth_date())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_hiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getRelation())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getCaregiver_phone())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDe_registration_date())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getReason())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getTransfer_reason())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getOther_reason())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getExited_graduation_reason())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_years())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_sexually_active())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_preventions())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_preventions_other())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_sex_older_women())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_transactional_sex())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_sex_work())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_economically_insecure())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_violent_partner())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_diagnosed())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_hiv_tested())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_test_positive())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_undergone_vmmc())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_in_school())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getAbym_economic_strengthening())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getVca_receiving_caseworker())).append(",");
                    fileWriter.append(escapeCsvValue(vcaCSVModel.getDistrict_moved_to())).append("\n");
                }


            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createVcaServicesCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "services_for_VCAs.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("unique_id, is_hiv_positive, date, art_clinic, date_last_vl, vl_last_result, date_next_vl, child_mmd, level_mmd, services, other_service, schooled_services, other_schooled_services, safe_services, other_safe_services, stable_services, other_stable_services, delete_status, vca_service_location, signature\n");
            List<VCAServiceModel> vcaServiceModel = VCAServiceReportDao.getVCAServicesCSV();

            if (vcaServiceModel != null && !vcaServiceModel.isEmpty()) {

                for (VCAServiceModel serviceModel : vcaServiceModel) {
                    fileWriter.append(escapeCsvValue(serviceModel.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getIs_hiv_positive())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getArt_clinic())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate_last_vl())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getVl_last_result())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate_next_vl())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getChild_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getLevel_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getServices())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_service())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSchooled_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_schooled_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSafe_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_safe_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getStable_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_stable_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDelete_status())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getVca_service_location())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSignature())).append("\n");
                }



            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createHouseholdServicesCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "services_for_Households.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("services, services_household, services_caregiver, health_services, other_health_services, schooled_services, other_schooled_services, safe_services, other_safe_services, stable_services, other_stable_services, hh_level_services, other_hh_level_services, date, is_hiv_positive, art_clinic, date_last_vl, vl_last_result, date_next_vl, caregiver_mmd, level_mmd, household_id, base_entity_id, other_services_caregiver, other_services_household, delete_status, hh_service_location, signature\n");
            List<HouseholdServiceReportModel> householdServiceModel = HouseholdServiceReportDao.getCSVHouseholdServices();

            if (householdServiceModel != null && !householdServiceModel.isEmpty()) {

                for (HouseholdServiceReportModel serviceModel : householdServiceModel) {
                    fileWriter.append(escapeCsvValue(serviceModel.getServices())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getServices_household())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getServices_caregiver())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getHealth_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_health_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSchooled_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_schooled_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSafe_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_safe_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getStable_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_stable_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getHh_level_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_hh_level_services())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getIs_hiv_positive())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getArt_clinic())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate_last_vl())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getVl_last_result())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDate_next_vl())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getCaregiver_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getLevel_mmd())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_services_caregiver())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getOther_services_household())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getDelete_status())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getHh_service_location())).append(",");
                    fileWriter.append(escapeCsvValue(serviceModel.getSignature())).append("\n");
                }




            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createVCAVisitationsCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "quarterly_assessment_for_VCAs.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("unique_id, first_name, last_name, birthdate, vca_visit_location, hei, base_entity_id, age, visit_date, is_hiv_positive, hiv_status, child_art, clinical_care, art_appointment, counselling, art_medication, mmd, mmd_months, drug_regimen, date_art, six_months, health_facility, vl_last_result, date_hiv, visit_hiv_test, referred_for_testing, hiv_risk, hiv_muac, hiv_assessment, referred_facility, eid_test, age_appropriate, not_appropriate_age, breastfeeding, not_breastfeeding, not_tested, hiv_infection, infection_risk, against_hiv_risk, prevention_support, substance_support, under_five, attending_under_five_clinic, appropriate_vaccinations, all_appropriate_vaccinations, muac_measure, nutrition_counselling_a, heps_a, muac_other_a, muac_measurement_b, nutrition_counselling_b, heps_b, muac_other_b, nutrition_status, neglected, neglected_child_exploitation, neglected_child_relationships, child_above_12_a, type_of_neglect, signs_of_violence, relationships_neglected, physical_violence, experiencing_neglected, type_of_neglect_physical, sexually_abused, post_gbv_care, vsu_legal_support, parenting_intervention, sexual_abuse, type_of_neglect_sexual, child_psychosocial, psychosocial_needs, psychosocial_services, child_other_services, referred_services, currently_in_school, not_in_school, child_missed, challenges_barriers, child_household, child_household_services, ovc_caregiver, verified_by_school_days, verified_by_school, current_calendar, did_not_progress, progression_child_household, progression_child_household_services, caseworker_name, caseworker_date_signed, caseworker_signature, manager_name, manager_date_signed, manager_signature, school_administration_name, telephone_number, school_administration_date_signed, school_administration_signature, phone, indicate_vl_result, length_on_art, delete_status, status_color, signature\n");
            List<VcaVisitationModel> vcaVisitationModel = VcaVisitationDao.getVcaVisitationCSV();

            if (vcaVisitationModel != null && !vcaVisitationModel.isEmpty()) {

                for (VcaVisitationModel visitationModel : vcaVisitationModel) {
                    fileWriter.append(escapeCsvValue(visitationModel.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getFirst_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getLast_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getBirthdate())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVca_visit_location())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHei())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAge())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVisit_date())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getIs_hiv_positive())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_art())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getClinical_care())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getArt_appointment())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCounselling())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getArt_medication())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMmd())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMmd_months())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDrug_regimen())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDate_art())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSix_months())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHealth_facility())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVl_last_result())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDate_hiv())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVisit_hiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_for_testing())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_risk())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_muac())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_assessment())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_facility())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getEid_test())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAge_appropriate())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNot_appropriate_age())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getBreastfeeding())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNot_breastfeeding())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNot_tested())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_infection())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getInfection_risk())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAgainst_hiv_risk())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPrevention_support())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSubstance_support())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getUnder_five())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAttending_under_five_clinic())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAppropriate_vaccinations())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getAll_appropriate_vaccinations())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMuac_measure())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNutrition_counselling_a())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHeps_a())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMuac_other_a())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMuac_measurement_b())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNutrition_counselling_b())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHeps_b())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMuac_other_b())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNutrition_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNeglected())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNeglected_child_exploitation())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNeglected_child_relationships())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_above_12_a())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getType_of_neglect())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSigns_of_violence())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getRelationships_neglected())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPhysical_violence())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getExperiencing_neglected())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getType_of_neglect_physical())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSexually_abused())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPost_gbv_care())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVsu_legal_support())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getParenting_intervention())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSexual_abuse())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getType_of_neglect_sexual())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_psychosocial())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPsychosocial_needs())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPsychosocial_services())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_other_services())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_services())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCurrently_in_school())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNot_in_school())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_missed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChallenges_barriers())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_household())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getChild_household_services())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getOvc_caregiver())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVerified_by_school_days())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVerified_by_school())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCurrent_calendar())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDid_not_progress())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getProgression_child_household())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getProgression_child_household_services())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaseworker_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaseworker_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaseworker_signature())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getManager_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getManager_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getManager_signature())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_administration_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getTelephone_number())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_administration_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_administration_signature())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getIndicate_vl_result())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getLength_on_art())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDelete_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getStatus_color())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSignature())).append("\n");
                }





            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createHouseholdsVisitationsCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "quarterly_assessment_for_Households.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("visit_date, household_id, caregiver_hiv_status, caregiver_art, clinical_care, date_art, art_appointment, counselling, art_medication, mmd, mmd_months, six_months, relation, health_facility, vl_last_result, date_hiv, visit_hiv_test, referred_for_testing, hiv_risk, hiv_assessment, prevention_support, referred_facility, number_children, hiv_awareness_status, caregiver_not_aware, hiv_test_referral, school_fees, unpaid_school_fees, linked_economic, referred_mcdss, source_income, list_source, bills_associated, barriers_challenges, economic_strengthening, social_cash, income_source_medical, list_source_medical, case_worker, caseworker_date_signed, case_manager, manager_date_signed, school_administration_name, telephone, school_administration_date_signed, visit_location, delete_status, signature\n");
            List<CaregiverVisitationModel> householdVisitationModel = CaregiverVisitationDao.getCSVVisits();

            if (householdVisitationModel != null && !householdVisitationModel.isEmpty()) {

                for (CaregiverVisitationModel visitationModel : householdVisitationModel) {
                    fileWriter.append(escapeCsvValue(visitationModel.getVisit_date())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaregiver_hiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaregiver_art())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getClinical_care())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDate_art())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getArt_appointment())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCounselling())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getArt_medication())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMmd())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getMmd_months())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSix_months())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getRelation())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHealth_facility())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVl_last_result())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDate_hiv())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVisit_hiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_for_testing())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_risk())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_assessment())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getPrevention_support())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_facility())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getNumber_children())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_awareness_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaregiver_not_aware())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getHiv_test_referral())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_fees())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getUnpaid_school_fees())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getLinked_economic())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getReferred_mcdss())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSource_income())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getList_source())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getBills_associated())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getBarriers_challenges())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getEconomic_strengthening())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSocial_cash())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getIncome_source_medical())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getList_source_medical())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCase_worker())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCaseworker_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getCase_manager())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getManager_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_administration_name())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getTelephone())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSchool_administration_date_signed())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getVisit_location())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getDelete_status())).append(",");
                    fileWriter.append(escapeCsvValue(visitationModel.getSignature())).append("\n");
                }




            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createCaregiverHivAssessmentCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "Caregiver_Hiv_Assessment.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("household_id, phone, base_entity_id, date_edited, caseworker_name, informed_consent, hiv_test, hiv_status, on_art, start_date,  art_number, symptoms, private_parts, exposed_to_HIV, unprotected_sex, pregnant_breastfeeding, hiv_tb_sti, hiv_test_result, date_of_hiv_test, delete_status\n");
            List<CaregiverHivAssessmentModel> caregiverHivAssessmentModel = CaregiverHivAssessmentDao.getAllHivAssessment();

            if (caregiverHivAssessmentModel != null && !caregiverHivAssessmentModel.isEmpty()) {

                for (CaregiverHivAssessmentModel caregiverHivAssessmentModel2 : caregiverHivAssessmentModel) {
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getDate_edited())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getCaseworker_name())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getInformed_consent())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getHiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getHiv_status())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getOn_art())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getStart_date())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getArt_number())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getSymptoms())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getPrivate_parts())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getExposed_to_HIV())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getUnprotected_sex())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getPregnant_breastfeeding())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getHiv_tb_sti())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getHiv_test_result())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getDate_of_hiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(caregiverHivAssessmentModel2.getDelete_status())).append("\n");
                }
            }

            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }

    public void createVcaCasePlansCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "Case_plans_for_VCA.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("unique_id, case_plan_id, case_plan_date, case_plan_status, type, vulnerability, goal, services, service_referred, institution, due_date, status, comment, delete_status\n");
            List<CasePlanModel> casePlanModels = IndexPersonDao.getAllDomainsById();

            if (casePlanModels != null && !casePlanModels.isEmpty()) {

                for (CasePlanModel casePlanModel : casePlanModels) {
                    fileWriter.append(escapeCsvValue(casePlanModel.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_id())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_date())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_status())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getType())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getVulnerability())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getGoal())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getServices())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getService_referred())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getInstitution())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getDue_date())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getStatus())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getComment())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getDelete_status())).append(",");
                }
            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createHouseholdCasePlansCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "Case_plans_for_household.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("household_id, case_plan_id, case_plan_date, case_plan_status, type, vulnerability, goal, services, service_referred, institution, due_date, status, comment, delete_status\n");
            List<CasePlanModel> casePlanModels = HouseholdDao.getAllDomainsById();

            if (casePlanModels != null && !casePlanModels.isEmpty()) {

                for (CasePlanModel casePlanModel : casePlanModels) {
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_id())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_date())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getCase_plan_status())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getType())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getVulnerability())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getGoal())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getServices())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getService_referred())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getInstitution())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getDue_date())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getStatus())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getComment())).append(",");
                    fileWriter.append(escapeCsvValue(casePlanModel.getDelete_status())).append(",");
                }
            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }
    public void createVcaHivAssessmentCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "Hiv_assessment_for_vca.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("unique_id, caseworker_name, phone, informed_consent, assessment_date, hiv_test, on_art, start_date, health_facility, art_number, biological_mother, deceased_parents, tb_symptoms, child_been_sick, frequent_rashes, child_had_pus, hiv_risk, hiv_tb, hiv_test_result, date_of_hiv_test, hiv_exposure, delete_status, facility\n");

            List<HivRiskAssessmentUnder15Model> hivRiskAssessmentUnder15Model = HivAssessmentUnder15Dao.getAllHivAssessmentUnder15();

            if (hivRiskAssessmentUnder15Model != null && !hivRiskAssessmentUnder15Model.isEmpty()) {

                for (HivRiskAssessmentUnder15Model hivRiskAssessmentUnder15Models : hivRiskAssessmentUnder15Model) {
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getCaseworker_name())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getInformed_consent())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getAssessment_date())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getOn_art())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getStart_date())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHealth_facility())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getArt_number())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getBiological_mother())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getDeceased_parents())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getTb_symptoms())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getChild_been_sick())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getFrequent_rashes())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getChild_had_pus())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHiv_risk())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHiv_tb())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHiv_test_result())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getDate_of_hiv_test())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getHiv_exposure())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getDelete_status())).append(",");
                    fileWriter.append(escapeCsvValue(hivRiskAssessmentUnder15Models.getFacility())).append("\n");
                }
            }


            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }

    public void createReferralsCSVFile(CSVCallback callback) {
        // Get the directory to save the file
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String fileName = "referrals.csv";
        File file = new File(baseDir, fileName);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append("unique_id, household_id, base_entity_id, relational_id, referral_location, referred_date, date_edited, caseworker_name, phone, receiving_organization, date_referred, covid_19, cd4, hiv_adherence, hiv_counseling_testing, post_gbv, substance_abuse, tb_screening, supplementary, prep, f_planning, insecticide, hiv_aids_treatment, f_w_health, vmmc, immunization, condom, routine_care, emergency_care, age_counselling, h_treatment_care, pmtct, hygiene_counselling, transmitted_infections, plha, viral_load, other_health_services, care_facility, post_violence_trauma, legal_assistance, other_safety_services, vca_uniforms_books, re_enrollment, bursaries, other_schooled_services, cash_transfer, cash_support, food_security, other_stability_services, dateCovidProvided, dateCD4Provided, dateHivAdherenceProvided, dateHivCounselingProvided, datePostGbvProvided, dateSubstanceAbuseProvided, dateTBScreeningProvided, dateSupplementaryProvided, datePlanningProvided, dateInsecticideProvided, dateTreatmentProvided, dateHealthProvided, dateVmmcProvided, dateImmunizationProvided, dateCondomProvided, dateCareProvided, dateEmergencyProvided, dateAgeCounsellingProvided, dateTreatmentCareProvided, datePmtctProvided, dateHygienceProvided, datePlhaProvided, dateViralLoadProvided, otherHealth, dateInfectionProvided, datePrepProvided, dateFacilityProvided, dateTraumaProvided, dateAssistanceProvided, dateOtherSafetyProvided, dateUniformsProvided, dateEnrollmentProvided, dateBursariesProvided, dateSchooledProvided, dateCashProvided, dateSupportProvided, dateSecurityProvided, dateStabilityProvided, specify_education, specify_safety, specify_school, specify_stability, delete_status\n");
            List<ReferralModel> referralModel = ReferralDao.getAllReferrals();

            if (referralModel != null && !referralModel.isEmpty()) {

                for (ReferralModel referralModel2 : referralModel) {
                    fileWriter.append(escapeCsvValue(referralModel2.getUnique_id())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getHousehold_id())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getBase_entity_id())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getRelational_id())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getReferral_location())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getReferred_date())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDate_edited())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCaseworker_name())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPhone())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getReceiving_organization())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDate_referred())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCovid_19())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCd4())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getHiv_adherence())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getHiv_counseling_testing())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPost_gbv())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSubstance_abuse())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getTb_screening())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSupplementary())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPrep())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getF_planning())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getInsecticide())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getHiv_aids_treatment())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getF_w_health())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getVmmc())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getImmunization())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCondom())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getRoutine_care())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getEmergency_care())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getAge_counselling())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getH_treatment_care())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPmtct())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getHygiene_counselling())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getTransmitted_infections())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPlha())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getViral_load())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getOther_health_services())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCare_facility())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getPost_violence_trauma())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getLegal_assistance())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getOther_safety_services())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getVca_uniforms_books())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getRe_enrollment())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getBursaries())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getOther_schooled_services())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCash_transfer())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getCash_support())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getFood_security())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getOther_stability_services())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateCovidProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateCD4Provided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateHivAdherenceProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateHivCounselingProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDatePostGbvProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateSubstanceAbuseProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateTBScreeningProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateSupplementaryProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDatePlanningProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateInsecticideProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateTreatmentProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateHealthProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateVmmcProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateImmunizationProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateCondomProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateCareProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateEmergencyProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateAgeCounsellingProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateTreatmentCareProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDatePmtctProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateHygienceProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDatePlhaProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateViralLoadProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getOtherHealth())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateInfectionProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDatePrepProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateFacilityProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateTraumaProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateAssistanceProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateOtherSafetyProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateUniformsProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateEnrollmentProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateBursariesProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateSchooledProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateCashProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateSupportProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateSecurityProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDateStabilityProvided())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSpecify_education())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSpecify_safety())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSpecify_school())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getSpecify_stability())).append(",");
                    fileWriter.append(escapeCsvValue(referralModel2.getDelete_status())).append("\n");
                }
            }



            fileWriter.flush();
            fileWriter.close();

            callback.onSuccess(file.getAbsolutePath());
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }


    public String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        String escapedValue = value.replace("\"", "\"\"");
        if (escapedValue.contains(",") || escapedValue.contains("\n") || escapedValue.contains("\"")) {
            escapedValue = "\"" + escapedValue + "\"";
        }
        return escapedValue;
    }
}
