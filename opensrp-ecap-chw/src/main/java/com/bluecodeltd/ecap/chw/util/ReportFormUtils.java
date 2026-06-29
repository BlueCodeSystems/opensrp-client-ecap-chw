package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.model.MalariaMonthlyModel;
import com.bluecodeltd.ecap.chw.model.NutritionMonthlyModel;
import com.bluecodeltd.ecap.chw.model.TbMonthlyModel;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ReportFormUtils {

    public static void prePopulateMalariaForm(JSONObject form, MalariaMonthlyModel model) {
        try {
            form.put("entity_id", model.getBaseEntityId());
            JSONArray fields = FormUtils.fields(form, "step1");
            updateField(fields, "reporting_month", model.getReportingMonth());
            updateField(fields, "reporting_year", model.getReportingYear());
            updateField(fields, "facility", model.getFacilityName());
            updateField(fields, "reporter_name", model.getReporterName());

            updateField(fields, "sa_q1_f_0_4", model.getSaQ1F04());
            updateField(fields, "sa_q1_f_5_15", model.getSaQ1F515());
            updateField(fields, "sa_q1_f_16_19", model.getSaQ1F1619());
            updateField(fields, "sa_q1_f_20_plus", model.getSaQ1F20Plus());
            updateField(fields, "sa_q1_f_calhiv", model.getSaQ1FCalhiv());
            updateField(fields, "sa_q1_f_hei", model.getSaQ1FHei());
            updateField(fields, "sa_q1_f_wlhiv", model.getSaQ1FWlhiv());
            updateField(fields, "sa_q1_f_sv", model.getSaQ1FSv());
            updateField(fields, "sa_q1_f_agyw", model.getSaQ1FAgyw());
            updateField(fields, "sa_q1_f_hiv_pos", model.getSaQ1FHivPos());
            updateField(fields, "sa_q1_f_siblings", model.getSaQ1FSiblings());
            updateField(fields, "sa_q1_f_caregivers", model.getSaQ1FCaregivers());
            updateField(fields, "sa_q1_m_0_4", model.getSaQ1M04());
            updateField(fields, "sa_q1_m_5_15", model.getSaQ1M515());
            updateField(fields, "sa_q1_m_16_19", model.getSaQ1M1619());
            updateField(fields, "sa_q1_m_20_plus", model.getSaQ1M20Plus());
            updateField(fields, "sa_q1_m_calhiv", model.getSaQ1MCalhiv());
            updateField(fields, "sa_q1_m_hei", model.getSaQ1MHei());
            updateField(fields, "sa_q1_m_wlhiv", model.getSaQ1MWlhiv());
            updateField(fields, "sa_q1_m_sv", model.getSaQ1MSv());
            updateField(fields, "sa_q1_m_siblings", model.getSaQ1MSiblings());
            updateField(fields, "sa_q1_m_caregivers", model.getSaQ1MCaregivers());

            updateField(fields, "sa_q2_f_0_4", model.getSaQ2F04());
            updateField(fields, "q2_f_5_15", model.getQ2F515());
            updateField(fields, "q2_f_16_19", model.getQ2F1619());
            updateField(fields, "q2_f_20_plus", model.getQ2F20Plus());
            updateField(fields, "q2_f_calhiv", model.getQ2FCalhiv());
            updateField(fields, "q2_f_hei", model.getQ2FHei());
            updateField(fields, "q2_f_wlhiv", model.getQ2FWlhiv());
            updateField(fields, "q2_f_sv", model.getQ2FSv());
            updateField(fields, "q2_f_agyw", model.getQ2FAgyw());
            updateField(fields, "q2_f_hiv_pos", model.getQ2FHivPos());
            updateField(fields, "q2_f_siblings", model.getQ2FSiblings());
            updateField(fields, "q2_f_caregivers", model.getQ2FCaregivers());
            updateField(fields, "q2_m_0_4", model.getQ2M04());
            updateField(fields, "q2_m_5_15", model.getQ2M515());
            updateField(fields, "q2_m_16_19", model.getQ2M1619());
            updateField(fields, "q2_m_20_plus", model.getQ2M20Plus());
            updateField(fields, "q2_m_calhiv", model.getQ2MCalhiv());
            updateField(fields, "q2_m_hei", model.getQ2MHei());
            updateField(fields, "q2_m_wlhiv", model.getQ2MWlhiv());
            updateField(fields, "q2_m_sv", model.getQ2MSv());
            updateField(fields, "q2_m_siblings", model.getQ2MSiblings());
            updateField(fields, "q2_m_caregivers", model.getQ2MCaregivers());

            updateField(fields, "q3_f_0_4", model.getQ3F04());
            updateField(fields, "q3_f_5_15", model.getQ3F515());
            updateField(fields, "q3_f_16_19", model.getQ3F1619());
            updateField(fields, "q3_f_20_plus", model.getQ3F20Plus());
            updateField(fields, "q3_f_calhiv", model.getQ3FCalhiv());
            updateField(fields, "q3_f_hei", model.getQ3FHei());
            updateField(fields, "q3_f_wlhiv", model.getQ3FWlhiv());
            updateField(fields, "q3_f_sv", model.getQ3FSv());
            updateField(fields, "q3_f_agyw", model.getQ3FAgyw());
            updateField(fields, "q3_f_hiv_pos", model.getQ3FHivPos());
            updateField(fields, "q3_f_siblings", model.getQ3FSiblings());
            updateField(fields, "q3_f_caregivers", model.getQ3FCaregivers());
            updateField(fields, "q3_m_0_4", model.getQ3M04());
            updateField(fields, "q3_m_5_15", model.getQ3M515());
            updateField(fields, "q3_m_16_19", model.getQ3M1619());
            updateField(fields, "q3_m_20_plus", model.getQ3M20Plus());
            updateField(fields, "q3_m_calhiv", model.getQ3MCalhiv());
            updateField(fields, "q3_m_hei", model.getQ3MHei());
            updateField(fields, "q3_m_wlhiv", model.getQ3MWlhiv());
            updateField(fields, "q3_m_sv", model.getQ3MSv());
            updateField(fields, "q3_m_siblings", model.getQ3MSiblings());
            updateField(fields, "q3_m_caregivers", model.getQ3MCaregivers());

            updateField(fields, "q4_f_0_4", model.getQ4F04());
            updateField(fields, "q4_f_5_15", model.getQ4F515());
            updateField(fields, "q4_f_16_19", model.getQ4F1619());
            updateField(fields, "q4_f_20_plus", model.getQ4F20Plus());
            updateField(fields, "q4_f_calhiv", model.getQ4FCalhiv());
            updateField(fields, "q4_f_hei", model.getQ4FHei());
            updateField(fields, "q4_f_wlhiv", model.getQ4FWlhiv());
            updateField(fields, "q4_f_sv", model.getQ4FSv());
            updateField(fields, "q4_f_agyw", model.getQ4FAgyw());
            updateField(fields, "q4_f_hiv_pos", model.getQ4FHivPos());
            updateField(fields, "q4_f_siblings", model.getQ4FSiblings());
            updateField(fields, "q4_f_caregivers", model.getQ4FCaregivers());
            updateField(fields, "q4_m_0_4", model.getQ4M04());
            updateField(fields, "q4_m_5_15", model.getQ4M515());
            updateField(fields, "q4_m_16_19", model.getQ4M1619());
            updateField(fields, "q4_m_20_plus", model.getQ4M20Plus());
            updateField(fields, "q4_m_calhiv", model.getQ4MCalhiv());
            updateField(fields, "q4_m_hei", model.getQ4MHei());
            updateField(fields, "q4_m_wlhiv", model.getQ4MWlhiv());
            updateField(fields, "q4_m_sv", model.getQ4MSv());
            updateField(fields, "q4_m_siblings", model.getQ4MSiblings());
            updateField(fields, "q4_m_caregivers", model.getQ4MCaregivers());

            updateField(fields, "q5_f_0_4", model.getQ5F04());
            updateField(fields, "q5_f_5_15", model.getQ5F515());
            updateField(fields, "q5_f_16_19", model.getQ5F1619());
            updateField(fields, "q5_f_20_plus", model.getQ5F20Plus());
            updateField(fields, "q5_f_calhiv", model.getQ5FCalhiv());
            updateField(fields, "q5_f_hei", model.getQ5FHei());
            updateField(fields, "q5_f_wlhiv", model.getQ5FWlhiv());
            updateField(fields, "q5_f_sv", model.getQ5FSv());
            updateField(fields, "q5_f_agyw", model.getQ5FAgyw());
            updateField(fields, "q5_f_hiv_pos", model.getQ5FHivPos());
            updateField(fields, "q5_f_siblings", model.getQ5FSiblings());
            updateField(fields, "q5_f_caregivers", model.getQ5FCaregivers());
            updateField(fields, "q5_m_0_4", model.getQ5M04());
            updateField(fields, "q5_m_5_15", model.getQ5M515());
            updateField(fields, "q5_m_16_19", model.getQ5M1619());
            updateField(fields, "q5_m_20_plus", model.getQ5M20Plus());
            updateField(fields, "q5_m_calhiv", model.getQ5MCalhiv());
            updateField(fields, "q5_m_hei", model.getQ5MHei());
            updateField(fields, "q5_m_wlhiv", model.getQ5MWlhiv());
            updateField(fields, "q5_m_sv", model.getQ5MSv());
            updateField(fields, "q5_m_siblings", model.getQ5MSiblings());
            updateField(fields, "q5_m_caregivers", model.getQ5MCaregivers());

            updateField(fields, "q6_f_0_4", model.getQ6F04());
            updateField(fields, "q6_f_5_15", model.getQ6F515());
            updateField(fields, "q6_f_16_19", model.getQ6F1619());
            updateField(fields, "q6_f_20_plus", model.getQ6F20Plus());
            updateField(fields, "q6_f_calhiv", model.getQ6FCalhiv());
            updateField(fields, "q6_f_hei", model.getQ6FHei());
            updateField(fields, "q6_f_wlhiv", model.getQ6FWlhiv());
            updateField(fields, "q6_f_sv", model.getQ6FSv());
            updateField(fields, "q6_f_agyw", model.getQ6FAgyw());
            updateField(fields, "q6_f_hiv_pos", model.getQ6FHivPos());
            updateField(fields, "q6_f_siblings", model.getQ6FSiblings());
            updateField(fields, "q6_f_caregivers", model.getQ6FCaregivers());
            updateField(fields, "q6_m_0_4", model.getQ6M04());
            updateField(fields, "q6_m_5_15", model.getQ6M515());
            updateField(fields, "q6_m_16_19", model.getQ6M1619());
            updateField(fields, "q6_m_20_plus", model.getQ6M20Plus());
            updateField(fields, "q6_m_calhiv", model.getQ6MCalhiv());
            updateField(fields, "q6_m_hei", model.getQ6MHei());
            updateField(fields, "q6_m_wlhiv", model.getQ6MWlhiv());
            updateField(fields, "q6_m_sv", model.getQ6MSv());
            updateField(fields, "q6_m_siblings", model.getQ6MSiblings());
            updateField(fields, "q6_m_caregivers", model.getQ6MCaregivers());

            updateField(fields, "q7_f_0_4", model.getQ7F04());
            updateField(fields, "q7_f_5_15", model.getQ7F515());
            updateField(fields, "q7_f_16_19", model.getQ7F1619());
            updateField(fields, "q7_f_20_plus", model.getQ7F20Plus());
            updateField(fields, "q7_f_calhiv", model.getQ7FCalhiv());
            updateField(fields, "q7_f_hei", model.getQ7FHei());
            updateField(fields, "q7_f_wlhiv", model.getQ7FWlhiv());
            updateField(fields, "q7_f_sv", model.getQ7FSv());
            updateField(fields, "q7_f_agyw", model.getQ7FAgyw());
            updateField(fields, "q7_f_hiv_pos", model.getQ7FHivPos());
            updateField(fields, "q7_f_siblings", model.getQ7FSiblings());
            updateField(fields, "q7_f_caregivers", model.getQ7FCaregivers());
            updateField(fields, "q7_m_0_4", model.getQ7M04());
            updateField(fields, "q7_m_5_15", model.getQ7M515());
            updateField(fields, "q7_m_16_19", model.getQ7M1619());
            updateField(fields, "q7_m_20_plus", model.getQ7M20Plus());
            updateField(fields, "q7_m_calhiv", model.getQ7MCalhiv());
            updateField(fields, "q7_m_hei", model.getQ7MHei());
            updateField(fields, "q7_m_wlhiv", model.getQ7MWlhiv());
            updateField(fields, "q7_m_sv", model.getQ7MSv());
            updateField(fields, "q7_m_siblings", model.getQ7MSiblings());
            updateField(fields, "q7_m_caregivers", model.getQ7MCaregivers());

            updateField(fields, "q8_f_0_4", model.getQ8F04());
            updateField(fields, "q8_f_5_15", model.getQ8F515());
            updateField(fields, "q8_f_16_19", model.getQ8F1619());
            updateField(fields, "q8_f_20_plus", model.getQ8F20Plus());
            updateField(fields, "q8_f_calhiv", model.getQ8FCalhiv());
            updateField(fields, "q8_f_hei", model.getQ8FHei());
            updateField(fields, "q8_f_wlhiv", model.getQ8FWlhiv());
            updateField(fields, "q8_f_sv", model.getQ8FSv());
            updateField(fields, "q8_f_agyw", model.getQ8FAgyw());
            updateField(fields, "q8_f_hiv_pos", model.getQ8FHivPos());
            updateField(fields, "q8_f_siblings", model.getQ8FSiblings());
            updateField(fields, "q8_f_caregivers", model.getQ8FCaregivers());
            updateField(fields, "q8_m_0_4", model.getQ8M04());
            updateField(fields, "q8_m_5_15", model.getQ8M515());
            updateField(fields, "q8_m_16_19", model.getQ8M1619());
            updateField(fields, "q8_m_20_plus", model.getQ8M20Plus());
            updateField(fields, "q8_m_calhiv", model.getQ8MCalhiv());
            updateField(fields, "q8_m_hei", model.getQ8MHei());
            updateField(fields, "q8_m_wlhiv", model.getQ8MWlhiv());
            updateField(fields, "q8_m_sv", model.getQ8MSv());
            updateField(fields, "q8_m_siblings", model.getQ8MSiblings());
            updateField(fields, "q8_m_caregivers", model.getQ8MCaregivers());

            updateField(fields, "q9_f_0_4", model.getQ9F04());
            updateField(fields, "q9_f_5_15", model.getQ9F515());
            updateField(fields, "q9_f_16_19", model.getQ9F1619());
            updateField(fields, "q9_f_20_plus", model.getQ9F20Plus());
            updateField(fields, "q9_f_calhiv", model.getQ9FCalhiv());
            updateField(fields, "q9_f_hei", model.getQ9FHei());
            updateField(fields, "q9_f_wlhiv", model.getQ9FWlhiv());
            updateField(fields, "q9_f_sv", model.getQ9FSv());
            updateField(fields, "q9_f_agyw", model.getQ9FAgyw());
            updateField(fields, "q9_f_hiv_pos", model.getQ9FHivPos());
            updateField(fields, "q9_f_siblings", model.getQ9FSiblings());
            updateField(fields, "q9_f_caregivers", model.getQ9FCaregivers());
            updateField(fields, "q9_m_0_4", model.getQ9M04());
            updateField(fields, "q9_m_5_15", model.getQ9M515());
            updateField(fields, "q9_m_16_19", model.getQ9M1619());
            updateField(fields, "q9_m_20_plus", model.getQ9M20Plus());
            updateField(fields, "q9_m_calhiv", model.getQ9MCalhiv());
            updateField(fields, "q9_m_hei", model.getQ9MHei());
            updateField(fields, "q9_m_wlhiv", model.getQ9MWlhiv());
            updateField(fields, "q9_m_sv", model.getQ9MSv());
            updateField(fields, "q9_m_siblings", model.getQ9MSiblings());
            updateField(fields, "q9_m_caregivers", model.getQ9MCaregivers());

            updateField(fields, "q10_f_0_4", model.getQ10F04());
            updateField(fields, "q10_f_5_15", model.getQ10F515());
            updateField(fields, "q10_f_16_19", model.getQ10F1619());
            updateField(fields, "q10_f_20_plus", model.getQ10F20Plus());
            updateField(fields, "q10_f_calhiv", model.getQ10FCalhiv());
            updateField(fields, "q10_f_hei", model.getQ10FHei());
            updateField(fields, "q10_f_wlhiv", model.getQ10FWlhiv());
            updateField(fields, "q10_f_sv", model.getQ10FSv());
            updateField(fields, "q10_f_agyw", model.getQ10FAgyw());
            updateField(fields, "q10_f_hiv_pos", model.getQ10FHivPos());
            updateField(fields, "q10_f_siblings", model.getQ10FSiblings());
            updateField(fields, "q10_f_caregivers", model.getQ10FCaregivers());
            updateField(fields, "q10_m_0_4", model.getQ10M04());
            updateField(fields, "q10_m_5_15", model.getQ10M515());
            updateField(fields, "q10_m_16_19", model.getQ10M1619());
            updateField(fields, "q10_m_20_plus", model.getQ10M20Plus());
            updateField(fields, "q10_m_calhiv", model.getQ10MCalhiv());
            updateField(fields, "q10_m_hei", model.getQ10MHei());
            updateField(fields, "q10_m_wlhiv", model.getQ10MWlhiv());
            updateField(fields, "q10_m_sv", model.getQ10MSv());
            updateField(fields, "q10_m_siblings", model.getQ10MSiblings());
            updateField(fields, "q10_m_caregivers", model.getQ10MCaregivers());


            JSONArray fieldsStep2 = FormUtils.fields(form, "step2");
            updateField(fieldsStep2, "sb_q1_f_0_4", model.getSbQ1F04());
            updateField(fieldsStep2, "sb_q1_f_5_15", model.getSbQ1F515());
            updateField(fieldsStep2, "sb_q1_f_16_19", model.getSbQ1F1619());
            updateField(fieldsStep2, "sb_q1_f_20_plus", model.getSbQ1F20Plus());
            updateField(fieldsStep2, "sb_q1_m_0_4", model.getSbQ1M04());
            updateField(fieldsStep2, "sb_q1_m_5_15", model.getSbQ1M515());
            updateField(fieldsStep2, "sb_q1_m_16_19", model.getSbQ1M1619());
            updateField(fieldsStep2, "sb_q1_m_20_plus", model.getSbQ1M20Plus());

            updateField(fieldsStep2, "sb_q2_f_0_4", model.getSbQ2F04());
            updateField(fieldsStep2, "sb_q2_f_5_15", model.getSbQ2F515());
            updateField(fieldsStep2, "sb_q2_f_16_19", model.getSbQ2F1619());
            updateField(fieldsStep2, "sb_q2_f_20_plus", model.getSbQ2F20Plus());
            updateField(fieldsStep2, "sb_q2_m_0_4", model.getSbQ2M04());
            updateField(fieldsStep2, "sb_q2_m_5_15", model.getSbQ2M515());
            updateField(fieldsStep2, "sb_q2_m_16_19", model.getSbQ2M1619());
            updateField(fieldsStep2, "sb_q2_m_20_plus", model.getSbQ2M20Plus());

            updateField(fieldsStep2, "sb_q3_f_0_4", model.getSbQ3F04());
            updateField(fieldsStep2, "sb_q3_f_5_15", model.getSbQ3F515());
            updateField(fieldsStep2, "sb_q3_f_16_19", model.getSbQ3F1619());
            updateField(fieldsStep2, "sb_q3_f_20_plus", model.getSbQ3F20Plus());
            updateField(fieldsStep2, "sb_q3_m_0_4", model.getSbQ3M04());
            updateField(fieldsStep2, "sb_q3_m_5_15", model.getSbQ3M515());
            updateField(fieldsStep2, "sb_q3_m_16_19", model.getSbQ3M1619());
            updateField(fieldsStep2, "sb_q3_m_20_plus", model.getSbQ3M20Plus());

            updateField(fieldsStep2, "sb_q4_f_0_4", model.getSbQ4F04());
            updateField(fieldsStep2, "sb_q4_f_5_15", model.getSbQ4F515());
            updateField(fieldsStep2, "sb_q4_f_16_19", model.getSbQ4F1619());
            updateField(fieldsStep2, "sb_q4_f_20_plus", model.getSbQ4F20Plus());
            updateField(fieldsStep2, "sb_q4_m_0_4", model.getSbQ4M04());
            updateField(fieldsStep2, "sb_q4_m_5_15", model.getSbQ4M515());
            updateField(fieldsStep2, "sb_q4_m_16_19", model.getSbQ4M1619());
            updateField(fieldsStep2, "sb_q4_m_20_plus", model.getSbQ4M20Plus());

            updateField(fieldsStep2, "sb_q5_f_0_4", model.getSbQ5F04());
            updateField(fieldsStep2, "sb_q5_f_5_15", model.getSbQ5F515());
            updateField(fieldsStep2, "sb_q5_f_16_19", model.getSbQ5F1619());
            updateField(fieldsStep2, "sb_q5_f_20_plus", model.getSbQ5F20Plus());
            updateField(fieldsStep2, "sb_q5_m_0_4", model.getSbQ5M04());
            updateField(fieldsStep2, "sb_q5_m_5_15", model.getSbQ5M515());
            updateField(fieldsStep2, "sb_q5_m_16_19", model.getSbQ5M1619());
            updateField(fieldsStep2, "sb_q5_m_20_plus", model.getSbQ5M20Plus());

            updateField(fieldsStep2, "sb_q6_f_0_4", model.getSbQ6F04());
            updateField(fieldsStep2, "sb_q6_f_5_15", model.getSbQ6F515());
            updateField(fieldsStep2, "sb_q6_f_16_19", model.getSbQ6F1619());
            updateField(fieldsStep2, "sb_q6_f_20_plus", model.getSbQ6F20Plus());
            updateField(fieldsStep2, "sb_q6_m_0_4", model.getSbQ6M04());
            updateField(fieldsStep2, "sb_q6_m_5_15", model.getSbQ6M515());
            updateField(fieldsStep2, "sb_q6_m_16_19", model.getSbQ6M1619());
            updateField(fieldsStep2, "sb_q6_m_20_plus", model.getSbQ6M20Plus());

            updateField(fieldsStep2, "sb_q7_f_0_4", model.getSbQ7F04());
            updateField(fieldsStep2, "sb_q7_f_5_15", model.getSbQ7F515());
            updateField(fieldsStep2, "sb_q7_f_16_19", model.getSbQ7F1619());
            updateField(fieldsStep2, "sb_q7_f_20_plus", model.getSbQ7F20Plus());
            updateField(fieldsStep2, "sb_q7_m_0_4", model.getSbQ7M04());
            updateField(fieldsStep2, "sb_q7_m_5_15", model.getSbQ7M515());
            updateField(fieldsStep2, "sb_q7_m_16_19", model.getSbQ7M1619());
            updateField(fieldsStep2, "sb_q7_m_20_plus", model.getSbQ7M20Plus());

            updateField(fieldsStep2, "sb_q8_f_0_4", model.getSbQ8F04());
            updateField(fieldsStep2, "sb_q8_f_5_15", model.getSbQ8F515());
            updateField(fieldsStep2, "sb_q8_f_16_19", model.getSbQ8F1619());
            updateField(fieldsStep2, "sb_q8_f_20_plus", model.getSbQ8F20Plus());
            updateField(fieldsStep2, "sb_q8_m_0_4", model.getSbQ8M04());
            updateField(fieldsStep2, "sb_q8_m_5_15", model.getSbQ8M515());
            updateField(fieldsStep2, "sb_q8_m_16_19", model.getSbQ8M1619());
            updateField(fieldsStep2, "sb_q8_m_20_plus", model.getSbQ8M20Plus());

            updateField(fieldsStep2, "sb_q9_f_0_4", model.getSbQ9F04());
            updateField(fieldsStep2, "sb_q9_f_5_15", model.getSbQ9F515());
            updateField(fieldsStep2, "sb_q9_f_16_19", model.getSbQ9F1619());
            updateField(fieldsStep2, "sb_q9_f_20_plus", model.getSbQ9F20Plus());
            updateField(fieldsStep2, "sb_q9_m_0_4", model.getSbQ9M04());
            updateField(fieldsStep2, "sb_q9_m_5_15", model.getSbQ9M515());
            updateField(fieldsStep2, "sb_q9_m_16_19", model.getSbQ9M1619());
            updateField(fieldsStep2, "sb_q9_m_20_plus", model.getSbQ9M20Plus());

            updateField(fieldsStep2, "sb_q10_f_0_4", model.getSbQ10F04());
            updateField(fieldsStep2, "sb_q10_f_5_15", model.getSbQ10F515());
            updateField(fieldsStep2, "sb_q10_f_16_19", model.getSbQ10F1619());
            updateField(fieldsStep2, "sb_q10_f_20_plus", model.getSbQ10F20Plus());
            updateField(fieldsStep2, "sb_q10_m_0_4", model.getSbQ10M04());
            updateField(fieldsStep2, "sb_q10_m_5_15", model.getSbQ10M515());
            updateField(fieldsStep2, "sb_q10_m_16_19", model.getSbQ10M1619());
            updateField(fieldsStep2, "sb_q10_m_20_plus", model.getSbQ10M20Plus());

            JSONArray fieldsStep3 = FormUtils.fields(form, "step3");
            updateField(fieldsStep3, "sc_q1", model.getScQ1());
            updateField(fieldsStep3, "sc_q2", model.getScQ2());
            updateField(fieldsStep3, "sc_q3", model.getScQ3());
            updateField(fieldsStep3, "sc_q4", model.getScQ4());
            updateField(fieldsStep3, "sc_q5", model.getScQ5());
            updateField(fieldsStep3, "sc_q6", model.getScQ6());
            updateField(fieldsStep3, "sc_q7", model.getScQ7());

            // Open all sections that have data
            if (hasData(model.getSaQ1F04(), model.getSaQ1F515(), model.getSaQ1F1619(), model.getSaQ1F20Plus(),
                    model.getSaQ1FCalhiv(), model.getSaQ1FHei(), model.getSaQ1FWlhiv(), model.getSaQ1FSv(),
                    model.getSaQ1FAgyw(), model.getSaQ1FHivPos(), model.getSaQ1FSiblings(), model.getSaQ1FCaregivers(),
                    model.getSaQ1M04(), model.getSaQ1M515(), model.getSaQ1M1619(), model.getSaQ1M20Plus(),
                    model.getSaQ1MCalhiv(), model.getSaQ1MHei(), model.getSaQ1MWlhiv(), model.getSaQ1MSv(),
                    model.getSaQ1MSiblings(), model.getSaQ1MCaregivers())) {
                updateField(fields, "sec_a_q1_status", "open");
            }
            if (hasData(model.getSaQ2F04(), model.getQ2F515(), model.getQ2F1619(), model.getQ2F20Plus(),
                    model.getQ2FCalhiv(), model.getQ2FHei(), model.getQ2FWlhiv(), model.getQ2FSv(),
                    model.getQ2FAgyw(), model.getQ2FHivPos(), model.getQ2FSiblings(), model.getQ2FCaregivers(),
                    model.getQ2M04(), model.getQ2M515(), model.getQ2M1619(), model.getQ2M20Plus(),
                    model.getQ2MCalhiv(), model.getQ2MHei(), model.getQ2MWlhiv(), model.getQ2MSv(),
                    model.getQ2MSiblings(), model.getQ2MCaregivers())) {
                updateField(fields, "sec_a_q2_status", "open");
            }
            if (hasData(model.getQ3F04(), model.getQ3F515(), model.getQ3F1619(), model.getQ3F20Plus(),
                    model.getQ3FCalhiv(), model.getQ3FHei(), model.getQ3FWlhiv(), model.getQ3FSv(),
                    model.getQ3FAgyw(), model.getQ3FHivPos(), model.getQ3FSiblings(), model.getQ3FCaregivers(),
                    model.getQ3M04(), model.getQ3M515(), model.getQ3M1619(), model.getQ3M20Plus(),
                    model.getQ3MCalhiv(), model.getQ3MHei(), model.getQ3MWlhiv(), model.getQ3MSv(),
                    model.getQ3MSiblings(), model.getQ3MCaregivers())) {
                updateField(fields, "sec_a_q3_status", "open");
            }
            if (hasData(model.getQ4F04(), model.getQ4F515(), model.getQ4F1619(), model.getQ4F20Plus(),
                    model.getQ4FCalhiv(), model.getQ4FHei(), model.getQ4FWlhiv(), model.getQ4FSv(),
                    model.getQ4FAgyw(), model.getQ4FHivPos(), model.getQ4FSiblings(), model.getQ4FCaregivers(),
                    model.getQ4M04(), model.getQ4M515(), model.getQ4M1619(), model.getQ4M20Plus(),
                    model.getQ4MCalhiv(), model.getQ4MHei(), model.getQ4MWlhiv(), model.getQ4MSv(),
                    model.getQ4MSiblings(), model.getQ4MCaregivers())) {
                updateField(fields, "sec_a_q4_status", "open");
            }
            if (hasData(model.getQ5F04(), model.getQ5F515(), model.getQ5F1619(), model.getQ5F20Plus(),
                    model.getQ5FCalhiv(), model.getQ5FHei(), model.getQ5FWlhiv(), model.getQ5FSv(),
                    model.getQ5FAgyw(), model.getQ5FHivPos(), model.getQ5FSiblings(), model.getQ5FCaregivers(),
                    model.getQ5M04(), model.getQ5M515(), model.getQ5M1619(), model.getQ5M20Plus(),
                    model.getQ5MCalhiv(), model.getQ5MHei(), model.getQ5MWlhiv(), model.getQ5MSv(),
                    model.getQ5MSiblings(), model.getQ5MCaregivers())) {
                updateField(fields, "sec_a_q5_status", "open");
            }
            if (hasData(model.getQ6F04(), model.getQ6F515(), model.getQ6F1619(), model.getQ6F20Plus(),
                    model.getQ6FCalhiv(), model.getQ6FHei(), model.getQ6FWlhiv(), model.getQ6FSv(),
                    model.getQ6FAgyw(), model.getQ6FHivPos(), model.getQ6FSiblings(), model.getQ6FCaregivers(),
                    model.getQ6M04(), model.getQ6M515(), model.getQ6M1619(), model.getQ6M20Plus(),
                    model.getQ6MCalhiv(), model.getQ6MHei(), model.getQ6MWlhiv(), model.getQ6MSv(),
                    model.getQ6MSiblings(), model.getQ6MCaregivers())) {
                updateField(fields, "sec_a_q6_status", "open");
            }
            if (hasData(model.getQ7F04(), model.getQ7F515(), model.getQ7F1619(), model.getQ7F20Plus(),
                    model.getQ7FCalhiv(), model.getQ7FHei(), model.getQ7FWlhiv(), model.getQ7FSv(),
                    model.getQ7FAgyw(), model.getQ7FHivPos(), model.getQ7FSiblings(), model.getQ7FCaregivers(),
                    model.getQ7M04(), model.getQ7M515(), model.getQ7M1619(), model.getQ7M20Plus(),
                    model.getQ7MCalhiv(), model.getQ7MHei(), model.getQ7MWlhiv(), model.getQ7MSv(),
                    model.getQ7MSiblings(), model.getQ7MCaregivers())) {
                updateField(fields, "sec_a_q7_status", "open");
            }
            if (hasData(model.getQ8F04(), model.getQ8F515(), model.getQ8F1619(), model.getQ8F20Plus(),
                    model.getQ8FCalhiv(), model.getQ8FHei(), model.getQ8FWlhiv(), model.getQ8FSv(),
                    model.getQ8FAgyw(), model.getQ8FHivPos(), model.getQ8FSiblings(), model.getQ8FCaregivers(),
                    model.getQ8M04(), model.getQ8M515(), model.getQ8M1619(), model.getQ8M20Plus(),
                    model.getQ8MCalhiv(), model.getQ8MHei(), model.getQ8MWlhiv(), model.getQ8MSv(),
                    model.getQ8MSiblings(), model.getQ8MCaregivers())) {
                updateField(fields, "sec_a_q8_status", "open");
            }
            if (hasData(model.getQ9F04(), model.getQ9F515(), model.getQ9F1619(), model.getQ9F20Plus(),
                    model.getQ9FCalhiv(), model.getQ9FHei(), model.getQ9FWlhiv(), model.getQ9FSv(),
                    model.getQ9FAgyw(), model.getQ9FHivPos(), model.getQ9FSiblings(), model.getQ9FCaregivers(),
                    model.getQ9M04(), model.getQ9M515(), model.getQ9M1619(), model.getQ9M20Plus(),
                    model.getQ9MCalhiv(), model.getQ9MHei(), model.getQ9MWlhiv(), model.getQ9MSv(),
                    model.getQ9MSiblings(), model.getQ9MCaregivers())) {
                updateField(fields, "sec_a_q9_status", "open");
            }
            if (hasData(model.getQ10F04(), model.getQ10F515(), model.getQ10F1619(), model.getQ10F20Plus(),
                    model.getQ10FCalhiv(), model.getQ10FHei(), model.getQ10FWlhiv(), model.getQ10FSv(),
                    model.getQ10FAgyw(), model.getQ10FHivPos(), model.getQ10FSiblings(), model.getQ10FCaregivers(),
                    model.getQ10M04(), model.getQ10M515(), model.getQ10M1619(), model.getQ10M20Plus(),
                    model.getQ10MCalhiv(), model.getQ10MHei(), model.getQ10MWlhiv(), model.getQ10MSv(),
                    model.getQ10MSiblings(), model.getQ10MCaregivers())) {
                updateField(fields, "sec_a_q10_status", "open");
            }

            if (hasData(model.getSbQ1F04(), model.getSbQ1F515(), model.getSbQ1F1619(), model.getSbQ1F20Plus(),
                    model.getSbQ1M04(), model.getSbQ1M515(), model.getSbQ1M1619(), model.getSbQ1M20Plus())) {
                updateField(fieldsStep2, "sec_b_q1_status", "open");
            }
            if (hasData(model.getSbQ2F04(), model.getSbQ2F515(), model.getSbQ2F1619(), model.getSbQ2F20Plus(),
                    model.getSbQ2M04(), model.getSbQ2M515(), model.getSbQ2M1619(), model.getSbQ2M20Plus())) {
                updateField(fieldsStep2, "sec_b_q2_status", "open");
            }
            if (hasData(model.getSbQ3F04(), model.getSbQ3F515(), model.getSbQ3F1619(), model.getSbQ3F20Plus(),
                    model.getSbQ3M04(), model.getSbQ3M515(), model.getSbQ3M1619(), model.getSbQ3M20Plus())) {
                updateField(fieldsStep2, "sec_b_q3_status", "open");
            }
            if (hasData(model.getSbQ4F04(), model.getSbQ4F515(), model.getSbQ4F1619(), model.getSbQ4F20Plus(),
                    model.getSbQ4M04(), model.getSbQ4M515(), model.getSbQ4M1619(), model.getSbQ4M20Plus())) {
                updateField(fieldsStep2, "sec_b_q4_status", "open");
            }
            if (hasData(model.getSbQ5F04(), model.getSbQ5F515(), model.getSbQ5F1619(), model.getSbQ5F20Plus(),
                    model.getSbQ5M04(), model.getSbQ5M515(), model.getSbQ5M1619(), model.getSbQ5M20Plus())) {
                updateField(fieldsStep2, "sec_b_q5_status", "open");
            }
            if (hasData(model.getSbQ6F04(), model.getSbQ6F515(), model.getSbQ6F1619(), model.getSbQ6F20Plus(),
                    model.getSbQ6M04(), model.getSbQ6M515(), model.getSbQ6M1619(), model.getSbQ6M20Plus())) {
                updateField(fieldsStep2, "sec_b_q6_status", "open");
            }
            if (hasData(model.getSbQ7F04(), model.getSbQ7F515(), model.getSbQ7F1619(), model.getSbQ7F20Plus(),
                    model.getSbQ7M04(), model.getSbQ7M515(), model.getSbQ7M1619(), model.getSbQ7M20Plus())) {
                updateField(fieldsStep2, "sec_b_q7_status", "open");
            }
            if (hasData(model.getSbQ8F04(), model.getSbQ8F515(), model.getSbQ8F1619(), model.getSbQ8F20Plus(),
                    model.getSbQ8M04(), model.getSbQ8M515(), model.getSbQ8M1619(), model.getSbQ8M20Plus())) {
                updateField(fieldsStep2, "sec_b_q8_status", "open");
            }
            if (hasData(model.getSbQ9F04(), model.getSbQ9F515(), model.getSbQ9F1619(), model.getSbQ9F20Plus(),
                    model.getSbQ9M04(), model.getSbQ9M515(), model.getSbQ9M1619(), model.getSbQ9M20Plus())) {
                updateField(fieldsStep2, "sec_b_q9_status", "open");
            }
            if (hasData(model.getSbQ10F04(), model.getSbQ10F515(), model.getSbQ10F1619(), model.getSbQ10F20Plus(),
                    model.getSbQ10M04(), model.getSbQ10M515(), model.getSbQ10M1619(), model.getSbQ10M20Plus())) {
                updateField(fieldsStep2, "sec_b_q10_status", "open");
            }

            if (hasData(model.getScQ1())) updateField(fieldsStep3, "sec_c_q1_status", "open");
            if (hasData(model.getScQ2())) updateField(fieldsStep3, "sec_c_q2_status", "open");
            if (hasData(model.getScQ3())) updateField(fieldsStep3, "sec_c_q3_status", "open");
            if (hasData(model.getScQ4())) updateField(fieldsStep3, "sec_c_q4_status", "open");
            if (hasData(model.getScQ5())) updateField(fieldsStep3, "sec_c_q5_status", "open");
            if (hasData(model.getScQ6())) updateField(fieldsStep3, "sec_c_q6_status", "open");
            if (hasData(model.getScQ7())) updateField(fieldsStep3, "sec_c_q7_status", "open");

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void prePopulateTbForm(JSONObject form, TbMonthlyModel model) {
        try {
            form.put("entity_id", model.getBaseEntityId());
            JSONArray fields = FormUtils.fields(form, "step1");
            updateField(fields, "reporting_month", model.getReportingMonth());
            updateField(fields, "reporting_year", model.getReportingYear());
            updateField(fields, "facility", model.getFacilityName());

            // Q1
            updateField(fields, "q1_f_lt_1", model.getQ1FLt1());
            updateField(fields, "q1_f_1_4", model.getQ1F14());
            updateField(fields, "q1_f_5_9", model.getQ1F59());
            updateField(fields, "q1_f_10_14", model.getQ1F1014());
            updateField(fields, "q1_f_15_19", model.getQ1F1519());
            updateField(fields, "q1_f_20_plus", model.getQ1F20Plus());
            updateField(fields, "q1_f_pc_18_plus", model.getQ1FPc18Plus());
            updateField(fields, "q1_f_total", model.getQ1FTotal());
            updateField(fields, "q1_m_lt_1", model.getQ1MLt1());
            updateField(fields, "q1_m_1_4", model.getQ1M14());
            updateField(fields, "q1_m_5_9", model.getQ1M59());
            updateField(fields, "q1_m_10_14", model.getQ1M1014());
            updateField(fields, "q1_m_15_19", model.getQ1M1519());
            updateField(fields, "q1_m_20_plus", model.getQ1M20Plus());
            updateField(fields, "q1_m_pc_18_plus", model.getQ1MPc18Plus());
            updateField(fields, "q1_m_total", model.getQ1MTotal());
            updateField(fields, "q1_hei", model.getQ1SubHei());
            updateField(fields, "q1_calhiv", model.getQ1SubCalhiv());
            updateField(fields, "q1_wlhiv", model.getQ1SubCPlhiv());
            updateField(fields, "q1_pc_lhiv", model.getQ1SubPcLhiv());
            updateField(fields, "q1_other", model.getQ1SubOther());

            // Q2
            updateField(fields, "q2_f_lt_1", model.getQ2FLt1());
            updateField(fields, "q2_f_1_4", model.getQ2F14());
            updateField(fields, "q2_f_5_9", model.getQ2F59());
            updateField(fields, "q2_f_10_14", model.getQ2F1014());
            updateField(fields, "q2_f_15_19", model.getQ2F1519());
            updateField(fields, "q2_f_20_plus", model.getQ2F20Plus());
            updateField(fields, "q2_f_pc_18_plus", model.getQ2FPc18Plus());
            updateField(fields, "q2_f_total", model.getQ2FTotal());
            updateField(fields, "q2_m_lt_1", model.getQ2MLt1());
            updateField(fields, "q2_m_1_4", model.getQ2M14());
            updateField(fields, "q2_m_5_9", model.getQ2M59());
            updateField(fields, "q2_m_10_14", model.getQ2M1014());
            updateField(fields, "q2_m_15_19", model.getQ2M1519());
            updateField(fields, "q2_m_20_plus", model.getQ2M20Plus());
            updateField(fields, "q2_m_pc_18_plus", model.getQ2MPc18Plus());
            updateField(fields, "q2_m_total", model.getQ2MTotal());
            updateField(fields, "q2_hei", model.getQ2SubHei());
            updateField(fields, "q2_calhiv", model.getQ2SubCalhiv());
            updateField(fields, "q2_wlhiv", model.getQ2SubCPlhiv());
            updateField(fields, "q2_pc_lhiv", model.getQ2SubPcLhiv());
            updateField(fields, "q2_other", model.getQ2SubOther());

            // Q3
            updateField(fields, "q3_f_lt_1", model.getQ3FLt1());
            updateField(fields, "q3_f_1_4", model.getQ3F14());
            updateField(fields, "q3_f_5_9", model.getQ3F59());
            updateField(fields, "q3_f_10_14", model.getQ3F1014());
            updateField(fields, "q3_f_15_19", model.getQ3F1519());
            updateField(fields, "q3_f_20_plus", model.getQ3F20Plus());
            updateField(fields, "q3_f_pc_18_plus", model.getQ3FPc18Plus());
            updateField(fields, "q3_f_total", model.getQ3FTotal());
            updateField(fields, "q3_m_lt_1", model.getQ3MLt1());
            updateField(fields, "q3_m_1_4", model.getQ3M14());
            updateField(fields, "q3_m_5_9", model.getQ3M59());
            updateField(fields, "q3_m_10_14", model.getQ3M1014());
            updateField(fields, "q3_m_15_19", model.getQ3M1519());
            updateField(fields, "q3_m_20_plus", model.getQ3M20Plus());
            updateField(fields, "q3_m_pc_18_plus", model.getQ3MPc18Plus());
            updateField(fields, "q3_m_total", model.getQ3MTotal());
            updateField(fields, "q3_hei", model.getQ3SubHei());
            updateField(fields, "q3_calhiv", model.getQ3SubCalhiv());
            updateField(fields, "q3_wlhiv", model.getQ3SubCPlhiv());
            updateField(fields, "q3_pc_lhiv", model.getQ3SubPcLhiv());
            updateField(fields, "q3_other", model.getQ3SubOther());

            // Q4
            updateField(fields, "q4_f_lt_1", model.getQ4FLt1());
            updateField(fields, "q4_f_1_4", model.getQ4F14());
            updateField(fields, "q4_f_5_9", model.getQ4F59());
            updateField(fields, "q4_f_10_14", model.getQ4F1014());
            updateField(fields, "q4_f_15_19", model.getQ4F1519());
            updateField(fields, "q4_f_20_plus", model.getQ4F20Plus());
            updateField(fields, "q4_f_pc_18_plus", model.getQ4FPc18Plus());
            updateField(fields, "q4_f_total", model.getQ4FTotal());
            updateField(fields, "q4_m_lt_1", model.getQ4MLt1());
            updateField(fields, "q4_m_1_4", model.getQ4M14());
            updateField(fields, "q4_m_5_9", model.getQ4M59());
            updateField(fields, "q4_m_10_14", model.getQ4M1014());
            updateField(fields, "q4_m_15_19", model.getQ4M1519());
            updateField(fields, "q4_m_20_plus", model.getQ4M20Plus());
            updateField(fields, "q4_m_pc_18_plus", model.getQ4MPc18Plus());
            updateField(fields, "q4_m_total", model.getQ4MTotal());
            updateField(fields, "q4_hei", model.getQ4SubHei());
            updateField(fields, "q4_calhiv", model.getQ4SubCalhiv());
            updateField(fields, "q4_wlhiv", model.getQ4SubCPlhiv());
            updateField(fields, "q4_pc_lhiv", model.getQ4SubPcLhiv());
            updateField(fields, "q4_other", model.getQ4SubOther());

            // Q5
            updateField(fields, "q5_f_lt_1", model.getQ5FLt1());
            updateField(fields, "q5_f_1_4", model.getQ5F14());
            updateField(fields, "q5_f_5_9", model.getQ5F59());
            updateField(fields, "q5_f_10_14", model.getQ5F1014());
            updateField(fields, "q5_f_15_19", model.getQ5F1519());
            updateField(fields, "q5_f_20_plus", model.getQ5F20Plus());
            updateField(fields, "q5_f_pc_18_plus", model.getQ5FPc18Plus());
            updateField(fields, "q5_f_total", model.getQ5FTotal());
            updateField(fields, "q5_m_lt_1", model.getQ5MLt1());
            updateField(fields, "q5_m_1_4", model.getQ5M14());
            updateField(fields, "q5_m_5_9", model.getQ5M59());
            updateField(fields, "q5_m_10_14", model.getQ5M1014());
            updateField(fields, "q5_m_15_19", model.getQ5M1519());
            updateField(fields, "q5_m_20_plus", model.getQ5M20Plus());
            updateField(fields, "q5_m_pc_18_plus", model.getQ5MPc18Plus());
            updateField(fields, "q5_m_total", model.getQ5MTotal());
            updateField(fields, "q5_hei", model.getQ5SubHei());
            updateField(fields, "q5_calhiv", model.getQ5SubCalhiv());
            updateField(fields, "q5_wlhiv", model.getQ5SubCPlhiv());
            updateField(fields, "q5_pc_lhiv", model.getQ5SubPcLhiv());

            // Q6
            updateField(fields, "q6_f_lt_1", model.getQ6FLt1());
            updateField(fields, "q6_f_1_4", model.getQ6F14());
            updateField(fields, "q6_f_5_9", model.getQ6F59());
            updateField(fields, "q6_f_10_14", model.getQ6F1014());
            updateField(fields, "q6_f_15_19", model.getQ6F1519());
            updateField(fields, "q6_f_20_plus", model.getQ6F20Plus());
            updateField(fields, "q6_f_pc_18_plus", model.getQ6FPc18Plus());
            updateField(fields, "q6_f_total", model.getQ6FTotal());
            updateField(fields, "q6_m_lt_1", model.getQ6MLt1());
            updateField(fields, "q6_m_1_4", model.getQ6M14());
            updateField(fields, "q6_m_5_9", model.getQ6M59());
            updateField(fields, "q6_m_10_14", model.getQ6M1014());
            updateField(fields, "q6_m_15_19", model.getQ6M1519());
            updateField(fields, "q6_m_20_plus", model.getQ6M20Plus());
            updateField(fields, "q6_m_pc_18_plus", model.getQ6MPc18Plus());
            updateField(fields, "q6_m_total", model.getQ6MTotal());
            updateField(fields, "q6_hei", model.getQ6SubHei());
            updateField(fields, "q6_calhiv", model.getQ6SubCalhiv());
            updateField(fields, "q6_wlhiv", model.getQ6SubCPlhiv());
            updateField(fields, "q6_pc_lhiv", model.getQ6SubPcLhiv());
            updateField(fields, "q6_other", model.getQ6SubOther());

            // Q7
            updateField(fields, "q7_f_lt_1", model.getQ7FLt1());
            updateField(fields, "q7_f_1_4", model.getQ7F14());
            updateField(fields, "q7_f_5_9", model.getQ7F59());
            updateField(fields, "q7_f_10_14", model.getQ7F1014());
            updateField(fields, "q7_f_15_19", model.getQ7F1519());
            updateField(fields, "q7_f_20_plus", model.getQ7F20Plus());
            updateField(fields, "q7_f_pc_18_plus", model.getQ7FPc18Plus());
            updateField(fields, "q7_f_total", model.getQ7FTotal());
            updateField(fields, "q7_m_lt_1", model.getQ7MLt1());
            updateField(fields, "q7_m_1_4", model.getQ7M14());
            updateField(fields, "q7_m_5_9", model.getQ7M59());
            updateField(fields, "q7_m_10_14", model.getQ7M1014());
            updateField(fields, "q7_m_15_19", model.getQ7M1519());
            updateField(fields, "q7_m_20_plus", model.getQ7M20Plus());
            updateField(fields, "q7_m_pc_18_plus", model.getQ7MPc18Plus());
            updateField(fields, "q7_m_total", model.getQ7MTotal());
            updateField(fields, "q7_hei", model.getQ7SubHei());
            updateField(fields, "q7_calhiv", model.getQ7SubCalhiv());
            updateField(fields, "q7_wlhiv", model.getQ7SubCPlhiv());
            updateField(fields, "q7_pc_lhiv", model.getQ7SubPcLhiv());

            // Open all sections that have data
            if (hasData(model.getQ1FLt1(), model.getQ1F14(), model.getQ1F59(), model.getQ1F1014(), model.getQ1F1519(), model.getQ1F20Plus(), model.getQ1FPc18Plus(), model.getQ1FTotal(),
                    model.getQ1MLt1(), model.getQ1M14(), model.getQ1M59(), model.getQ1M1014(), model.getQ1M1519(), model.getQ1M20Plus(), model.getQ1MPc18Plus(), model.getQ1MTotal(),
                    model.getQ1SubHei(), model.getQ1SubCalhiv(), model.getQ1SubCPlhiv(), model.getQ1SubPcLhiv(), model.getQ1SubOther())) {
                updateField(fields, "q1_status", "open");
            }
            if (hasData(model.getQ2FLt1(), model.getQ2F14(), model.getQ2F59(), model.getQ2F1014(), model.getQ2F1519(), model.getQ2F20Plus(), model.getQ2FPc18Plus(), model.getQ2FTotal(),
                    model.getQ2MLt1(), model.getQ2M14(), model.getQ2M59(), model.getQ2M1014(), model.getQ2M1519(), model.getQ2M20Plus(), model.getQ2MPc18Plus(), model.getQ2MTotal(),
                    model.getQ2SubHei(), model.getQ2SubCalhiv(), model.getQ2SubCPlhiv(), model.getQ2SubPcLhiv(), model.getQ2SubOther())) {
                updateField(fields, "q2_status", "open");
            }
            if (hasData(model.getQ3FLt1(), model.getQ3F14(), model.getQ3F59(), model.getQ3F1014(), model.getQ3F1519(), model.getQ3F20Plus(), model.getQ3FPc18Plus(), model.getQ3FTotal(),
                    model.getQ3MLt1(), model.getQ3M14(), model.getQ3M59(), model.getQ3M1014(), model.getQ3M1519(), model.getQ3M20Plus(), model.getQ3MPc18Plus(), model.getQ3MTotal(),
                    model.getQ3SubHei(), model.getQ3SubCalhiv(), model.getQ3SubCPlhiv(), model.getQ3SubPcLhiv(), model.getQ3SubOther())) {
                updateField(fields, "q3_status", "open");
            }
            if (hasData(model.getQ4FLt1(), model.getQ4F14(), model.getQ4F59(), model.getQ4F1014(), model.getQ4F1519(), model.getQ4F20Plus(), model.getQ4FPc18Plus(), model.getQ4FTotal(),
                    model.getQ4MLt1(), model.getQ4M14(), model.getQ4M59(), model.getQ4M1014(), model.getQ4M1519(), model.getQ4M20Plus(), model.getQ4MPc18Plus(), model.getQ4MTotal(),
                    model.getQ4SubHei(), model.getQ4SubCalhiv(), model.getQ4SubCPlhiv(), model.getQ4SubPcLhiv(), model.getQ4SubOther())) {
                updateField(fields, "q4_status", "open");
            }
            if (hasData(model.getQ5FLt1(), model.getQ5F14(), model.getQ5F59(), model.getQ5F1014(), model.getQ5F1519(), model.getQ5F20Plus(), model.getQ5FPc18Plus(), model.getQ5FTotal(),
                    model.getQ5MLt1(), model.getQ5M14(), model.getQ5M59(), model.getQ5M1014(), model.getQ5M1519(), model.getQ5M20Plus(), model.getQ5MPc18Plus(), model.getQ5MTotal(),
                    model.getQ5SubHei(), model.getQ5SubCalhiv(), model.getQ5SubCPlhiv(), model.getQ5SubPcLhiv())) {
                updateField(fields, "q5_status", "open");
            }
            if (hasData(model.getQ6FLt1(), model.getQ6F14(), model.getQ6F59(), model.getQ6F1014(), model.getQ6F1519(), model.getQ6F20Plus(), model.getQ6FPc18Plus(), model.getQ6FTotal(),
                    model.getQ6MLt1(), model.getQ6M14(), model.getQ6M59(), model.getQ6M1014(), model.getQ6M1519(), model.getQ6M20Plus(), model.getQ6MPc18Plus(), model.getQ6MTotal(),
                    model.getQ6SubHei(), model.getQ6SubCalhiv(), model.getQ6SubCPlhiv(), model.getQ6SubPcLhiv(), model.getQ6SubOther())) {
                updateField(fields, "q6_status", "open");
            }
            if (hasData(model.getQ7FLt1(), model.getQ7F14(), model.getQ7F59(), model.getQ7F1014(), model.getQ7F1519(), model.getQ7F20Plus(), model.getQ7FPc18Plus(), model.getQ7FTotal(),
                    model.getQ7MLt1(), model.getQ7M14(), model.getQ7M59(), model.getQ7M1014(), model.getQ7M1519(), model.getQ7M20Plus(), model.getQ7MPc18Plus(), model.getQ7MTotal(),
                    model.getQ7SubHei(), model.getQ7SubCalhiv(), model.getQ7SubCPlhiv(), model.getQ7SubPcLhiv())) {
                updateField(fields, "q7_status", "open");
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void prePopulateNutritionForm(JSONObject form, NutritionMonthlyModel model) {
        try {
            form.put("entity_id", model.getBaseEntityId());
            JSONArray fields = FormUtils.fields(form, "step1");
            updateField(fields, "reporting_month", model.getReportingPeriod());
            updateField(fields, "reporting_year", model.getReportingYear());
            updateField(fields, "province", model.getProvince());
            updateField(fields, "district", model.getDistrict());
            updateField(fields, "ward", model.getWard());
            updateField(fields, "facility", model.getFacilityName());
            updateField(fields, "partner", model.getPartner());

            updateField(fields, "subpop_calhiv", model.getCAlhiv());
            updateField(fields, "subpop_hei", model.getHei());
            updateField(fields, "subpop_cml_hiv", model.getCwlhiv());
            updateField(fields, "subpop_cpbfa", model.getCPbfa());
            updateField(fields, "subpop_siblings", model.getSiblings());
            
            updateField(fields, "hh_practicing_diet_diversity", model.getHhPracticingDietDiversity());
            updateField(fields, "hh_practicing_exclusive_bf", model.getHhPracticingExclusiveBf());
            updateField(fields, "hh_practicing_complementary_feeding", model.getHhPracticingComplementaryFeeding());
            updateField(fields, "hh_wash_activities", model.getHhWashActivities());
            updateField(fields, "hh_visited_assessment", model.getHhVisitedAssessment());
            updateField(fields, "ppmam_identified", model.getPpmamIdentified());
            updateField(fields, "ppmam_referred_commenced", model.getPpmamReferredCommenced());
            updateField(fields, "other_children_pmam", model.getOtherChildrenPmam());
            updateField(fields, "plw_art_pmtct_nutrition_assessment", model.getPlwArtPmtctNutritionAssessment());
            updateField(fields, "plw_received_ifas", model.getPlwReceivedIfas());
            updateField(fields, "hh_food_insecurity_counselled", model.getHhFoodInsecurityCounselled());

            updateField(fields, "mnp_children_6_23_received", model.getMnpChildren623Received());
            updateField(fields, "mnp_plw_received", model.getMnpPlwReceived());
            updateField(fields, "vita_children_6_11_months", model.getVitaChildren611Months());
            updateField(fields, "vita_children_12_59_months", model.getVitaChildren1259Months());
            updateField(fields, "vita_plw_supplemented", model.getVitaPlwSupplemented());
            updateField(fields, "deworming_children_12_59", model.getDewormingChildren1259());
            updateField(fields, "deworming_plw", model.getDewormingPlw());

            updateField(fields, "ecd_centres_supported_monitoring", model.getEcdCentresSupportedMonitoring());
            updateField(fields, "ecd_centres_with_feeding", model.getEcdCentresWithFeeding());
            updateField(fields, "ecd_children_enrolled", model.getEcdChildrenEnrolled());
            updateField(fields, "ecd_caregivers_trained", model.getEcdCaregiversTrained());
            updateField(fields, "ecd_developmental_screening", model.getEcdDevelopmentalScreening());

            updateField(fields, "wfa_underweight", model.getWfaUnderweight());
            updateField(fields, "wfa_overweight", model.getWfaOverweight());
            updateField(fields, "wfa_normal", model.getWfaNormal());

            updateField(fields, "nutrition_grade_1", model.getNutritionGrade1());
            updateField(fields, "nutrition_grade_2", model.getNutritionGrade2());
            updateField(fields, "nutrition_nr", model.getNutritionNr());

            updateField(fields, "muac_red_below_11_5", model.getMuacRedBelow115());
            updateField(fields, "muac_yellow_11_5_to_12_5", model.getMuacYellow115To125());
            updateField(fields, "muac_green_12_5_plus", model.getMuacGreen125Plus());
            updateField(fields, "muac_oedema", model.getMuacOedema());

            updateField(fields, "sti_referred", model.getStiReferred());
            updateField(fields, "sti_treated", model.getStiTreated());

            updateField(fields, "referral_nutrition_to_health", model.getReferralNutritionToHealth());
            updateField(fields, "referral_feedback_received", model.getReferralFeedbackReceived());
            updateField(fields, "referral_date_of_referral", model.getReferralDateOfReferral());
            updateField(fields, "referral_date_of_feedback", model.getReferralDateOfFeedback());
            updateField(fields, "referral_hiv_tb_integration", model.getReferralHivTbIntegration());

            updateField(fields, "comment", model.getComment());

            // Open all sections that have data
            if (hasData(model.getCAlhiv(), model.getHei(), model.getCwlhiv(), model.getCPbfa(), model.getSiblings())) {
                updateField(fields, "sec_subpop_status", "open");
            }
            if (hasData(model.getHhPracticingDietDiversity(), model.getHhPracticingExclusiveBf(), model.getHhPracticingComplementaryFeeding(), 
                    model.getHhWashActivities(), model.getHhVisitedAssessment(), model.getPpmamIdentified(), 
                    model.getPpmamReferredCommenced(), model.getOtherChildrenPmam(), model.getPlwArtPmtctNutritionAssessment(), 
                    model.getPlwReceivedIfas(), model.getHhFoodInsecurityCounselled())) {
                updateField(fields, "sec_hh_malnutrition_status", "open");
            }
            if (hasData(model.getMnpChildren623Received(), model.getMnpPlwReceived(), model.getVitaChildren611Months(), 
                    model.getVitaChildren1259Months(), model.getVitaPlwSupplemented(), model.getDewormingChildren1259(), 
                    model.getDewormingPlw())) {
                updateField(fields, "sec_mnp_vita_status", "open");
            }
            if (hasData(model.getEcdCentresSupportedMonitoring(), model.getEcdCentresWithFeeding(), model.getEcdChildrenEnrolled(), 
                    model.getEcdCaregiversTrained(), model.getEcdDevelopmentalScreening())) {
                updateField(fields, "sec_ecd_status", "open");
            }
            if (hasData(model.getWfaUnderweight(), model.getWfaOverweight(), model.getWfaNormal())) {
                updateField(fields, "sec_weight_for_age_status", "open");
            }
            if (hasData(model.getNutritionGrade1(), model.getNutritionGrade2(), model.getNutritionNr())) {
                updateField(fields, "sec_nutrition_assessment_status", "open");
            }
            if (hasData(model.getMuacRedBelow115(), model.getMuacYellow115To125(), model.getMuacGreen125Plus(), model.getMuacOedema())) {
                updateField(fields, "sec_muac_status", "open");
            }
            if (hasData(model.getStiReferred(), model.getStiTreated())) {
                updateField(fields, "sec_sti_status", "open");
            }
            if (hasData(model.getReferralNutritionToHealth(), model.getReferralFeedbackReceived(), model.getReferralDateOfReferral(), 
                    model.getReferralDateOfFeedback(), model.getReferralHivTbIntegration())) {
                updateField(fields, "sec_referral_status", "open");
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static boolean hasData(String... values) {
        if (values == null) return false;
        for (String v : values) {
            if (v != null && !v.isEmpty()) return true;
        }
        return false;
    }

    private static void updateField(JSONArray fields, String key, String value) throws JSONException {
        JSONObject field = FormUtils.getFieldJSONObject(fields, key);
        if (field != null) {
            field.put("value", value == null ? "" : value);
        }
    }
}
