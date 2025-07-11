package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.anc.domain.GroupedVisit;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.MedicalHistoryAdapter;
import org.smartregister.chw.core.adapter.PncMedicalHistoryAdapter;
import org.smartregister.chw.core.domain.MedicalHistory;
import org.smartregister.chw.core.utils.PncMedicalHistoryViewBuilder;
import org.smartregister.chw.pnc.PncLibrary;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public abstract class DefaultPncMedicalHistoryActivityFlv implements CorePncMedicalHistoryActivity.Flavor {

    protected LayoutInflater inflater;
    protected List<Visit> visits;
    protected LinearLayout parentView;
    @LayoutRes
    protected int childLayout = R.layout.pnc_medical_history_nested_sub_item;
    @LayoutRes
    protected int rootLayout = R.layout.pnc_medical_history_nested;
    protected List<MedicalHistory> medicalHistories = null;
    private Context context;

    protected static String toCSV(List<String> list) {
        String result = "";
        if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

    @Override
    public View bindViews(Activity activity) {
        inflater = activity.getLayoutInflater();
        this.context = activity;
        parentView = new LinearLayout(activity);
        parentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parentView.setOrientation(LinearLayout.VERTICAL);
        return parentView;
    }

    @Override
    public void processViewData(List<GroupedVisit> groupedVisits, Context context, MemberObject memberObject) {
        if (groupedVisits.size() > 0) {
            for (GroupedVisit groupedVisit : groupedVisits) {
                // Process mother's details
                if (groupedVisit.getBaseEntityId().equals(memberObject.getBaseEntityId())) {
                    processMotherDetails(groupedVisit.getVisitList(), memberObject);
                } else {
                    // Process child's details
                    processChildDetails(groupedVisit.getVisitList(), groupedVisit.getName());
                }
            }
        }
    }

    @NotNull
    protected String getText(@Nullable List<VisitDetail> visitDetails) {
        if (visitDetails == null) {
            return "";
        }
        List<String> vals = new ArrayList<>();
        for (VisitDetail vd : visitDetails) {
            String val = getText(vd);
            if (StringUtils.isNotBlank(val)) {
                vals.add(val);
            }
        }
        return toCSV(vals);
    }

    protected void processMotherDetails(List<Visit> visits, MemberObject memberObject) {
        this.visits = visits;
        // process the data
        Map<String, Map<String, String>> healthFacilityVisitMap = new HashMap<>();

        for (Visit visit : visits) {
            for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                String val = getText(entry.getValue());

                switch (entry.getKey()) {
                    // health facility
                    case "pnc_visit_1":
                    case "pnc_visit_2":
                    case "pnc_visit_3":

                        String date_key = "pnc_hf_visit1_date";
                        if (entry.getKey().equals("pnc_visit_2")) {
                            date_key = "pnc_hf_visit2_date";
                        }
                        if (entry.getKey().equals("pnc_visit_3")) {
                            date_key = "pnc_hf_visit3_date";
                        }

                        if ("Yes".equalsIgnoreCase(val)) {
                            Map<String, String> map = new HashMap<>();
                            // add details
                            map.put("pnc_hf_visit_date", getText(visit.getVisitDetails().get(date_key)));
                            map.put("baby_weight", getText(visit.getVisitDetails().get("baby_weight")));
                            map.put("baby_temp", getText(visit.getVisitDetails().get("baby_temp")));
                            healthFacilityVisitMap.put(entry.getKey(), map);
                        }
                        break;
                }
            }
        }

        processLastVisitDate(memberObject.getBaseEntityId());
        addMotherDetailsView(memberObject.getFullName());

        medicalHistories = new ArrayList<>(); // New history list for mother's details
        processHealthFacilityVisit(healthFacilityVisitMap);
        processFamilyPlanning(visits);
        addMedicalHistoriesView();
    }

    protected void processLastVisitDate(String baseEntityId) {
        medicalHistories = new ArrayList<>();
        Long pncLastVisitDate = PncLibrary.getInstance().profileRepository().getLastVisit(baseEntityId);
        if (pncLastVisitDate != null) {
            MedicalHistory history = new MedicalHistory();
            int days = Days.daysBetween(new DateTime(pncLastVisitDate), new DateTime()).getDays();
            history.setTitle(StringUtils.capitalize(MessageFormat.format(context.getString(R.string.days_ago_for_pnc_home_visit), String.valueOf(days))));

            medicalHistories.add(history);

            View view = new PncMedicalHistoryViewBuilder(inflater, context)
                    .withAdapter(getAdapter())
                    .withRootLayout(rootLayout)
                    .withSeparator(false)
                    .withTitle(context.getString(R.string.last_visit))
                    .build();
            parentView.addView(view);
        }
    }

    protected void processHealthFacilityVisit(Map<String, Map<String, String>> healthFacilityVisit) {
        if (healthFacilityVisit != null && healthFacilityVisit.size() > 0) {
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_health_facility_visits_title));
            Iterator<Map.Entry<String, Map<String, String>>> mapIterator = healthFacilityVisit.entrySet().iterator();
            List<String> hfDetails = new ArrayList<>();
            while (mapIterator.hasNext()) {
                Map.Entry<String, Map<String, String>> entry = mapIterator.next();
                hfDetails.add(MessageFormat.format(context.getString(R.string.pnc_wcaro_health_facility_visit), entry.getValue().get("pnc_hf_visit_date")));
                if (entry.getValue().get("baby_weight") != null) {
                    hfDetails.add(context.getString(R.string.pnc_baby_weight, entry.getValue().get("baby_weight")));
                }
                if (entry.getValue().get("baby_temp") != null) {
                    hfDetails.add(context.getString(R.string.pnc_baby_temp, entry.getValue().get("baby_temp")));
                }

                if (mapIterator.hasNext()) {
                    hfDetails.add("");
                }
            }
            medicalHistory.setText(hfDetails);
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processFamilyPlanning(List<Visit> visits) {
        Map<String, String> familyPlanningMap = new HashMap<>();
        extractFamilyPlanningMethods(visits, familyPlanningMap);

        if (familyPlanningMap.size() > 0) {
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_medical_history_family_planning_title));
            List<String> fpDetails = new ArrayList<>();
            Iterator<Map.Entry<String, String>> mapIterator = familyPlanningMap.entrySet().iterator();
            while (mapIterator.hasNext()) {
                Map.Entry<String, String> entry = mapIterator.next();
                if (entry.getKey() != null) {
                    String method = "";
                    switch (entry.getKey()) {
                        case "None":
                            method = context.getString(R.string.pnc_none);
                            break;
                        case "Abstinence":
                            method = context.getString(R.string.pnc_abstinence);
                            break;
                        case "Condom":
                            method = context.getString(R.string.pnc_condom);
                            break;
                        case "Tablets":
                            method = context.getString(R.string.pnc_tablets);
                            break;
                        case "Injectable":
                            method = context.getString(R.string.pnc_injectable);
                            break;
                        case "IUD":
                            method = context.getString(R.string.pnc_iud);
                            break;
                        case "Implant":
                            method = context.getString(R.string.pnc_implant);
                            break;
                        case "Other":
                            method = context.getString(R.string.pnc_other);
                            break;
                    }
                    fpDetails.add(MessageFormat.format(context.getString(R.string.pnc_family_planning_method), method));
                }
                if (entry.getValue() != null) {
                    fpDetails.add(MessageFormat.format(context.getString(R.string.pnc_family_planning_date), StringUtils.isNotBlank(entry.getValue()) ? entry.getValue() : "n/a"));
                }
                if (mapIterator.hasNext()) {
                    fpDetails.add("");
                }
            }
            medicalHistory.setText(fpDetails);

            if (medicalHistories == null) {
                medicalHistories = new ArrayList<>();
            }
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processChildDetails(List<Visit> visits, String memberName) {
        this.visits = visits;
        String vaccineCard = context.getString(R.string.pnc_no);
        String vaccineCardDate = "";
        Map<String, String> immunization = new HashMap<>();
        Map<String, String> growthData = new HashMap<>();
        String childName = StringUtils.isNotBlank(memberName) ? memberName : "";

        for (Visit visit : visits) {
            for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                String val = getText(entry.getValue());

                switch (entry.getKey()) {
                    // vaccine card
                    case "vaccine_card":
                        if ("No".equalsIgnoreCase(vaccineCard) && "Yes".equalsIgnoreCase(val)) {
                            vaccineCard = context.getString(R.string.pnc_yes);
                        }
                        break;
                    // immunization
                    case "opv0":
                    case "bcg":
                        immunization.put(entry.getKey(), val);
                        break;
                    // growth and nutrition
                    case "exclusive_breast_feeding":
                        growthData.put(entry.getKey(), val);
                        break;
                }
            }
        }

        addChildDetailsView(childName);

        processVaccineCard(vaccineCard, vaccineCardDate);
        processImmunization(immunization);
        processGrowthAndNutrition(growthData);

        addMedicalHistoriesView();
    }

    protected void processVaccineCard(String received, String vaccineCardDate) {
        if (received != null) {
            medicalHistories = new ArrayList<>();

            List<String> vaccinationDetails = new ArrayList<>();
            vaccinationDetails.add(MessageFormat.format(context.getString(R.string.pnc_medical_history_child_vaccine_card_received), received));

            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setText(vaccinationDetails);
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processImmunization(Map<String, String> immunization) {
        if (immunization != null && immunization.size() > 0) {

            List<String> immunizationDetails = new ArrayList<>();
            for (Map.Entry<String, String> entry : immunization.entrySet()) {
                if (entry.getValue() != null) {
                    String entryValue = entry.getValue().equalsIgnoreCase("Vaccine not given") ? context.getString(R.string.pnc_vaccine_not_given) : entry.getValue();
                    if (entry.getKey().equals("bcg")) {
                        if (entryValue.equalsIgnoreCase(context.getString(R.string.pnc_vaccine_not_given)))
                            immunizationDetails.add(MessageFormat.format(context.getString(R.string.pnc_bcg_not_done), entryValue));
                        else
                            immunizationDetails.add(MessageFormat.format(context.getString(R.string.pnc_bcg), entryValue));
                    } else if (entry.getKey().equals("opv0")) {
                        if (entryValue.equalsIgnoreCase(context.getString(R.string.pnc_vaccine_not_given)))
                            immunizationDetails.add(MessageFormat.format(context.getString(R.string.pnc_opv0_not_done), entryValue));
                        else
                            immunizationDetails.add(MessageFormat.format(context.getString(R.string.pnc_opv0), entryValue));
                    }
                }
            }

            if (medicalHistories == null) {
                medicalHistories = new ArrayList<>();
            }

            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_medical_history_child_immunizations_title));
            medicalHistory.setText(immunizationDetails);
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processGrowthAndNutrition(Map<String, String> growth_data) {
        if (growth_data != null && growth_data.size() > 0) {

            List<String> nutritionDetails = new ArrayList<>();
            for (Map.Entry<String, String> entry : growth_data.entrySet()) {
                if (entry.getValue().equalsIgnoreCase("YES")) {
                    nutritionDetails.add(MessageFormat.format(context.getString(R.string.pnc_exclusive_bf_0_months), context.getString(R.string.pnc_no)));
                } else {
                    nutritionDetails.add(MessageFormat.format(context.getString(R.string.pnc_exclusive_bf_0_months), context.getString(R.string.pnc_yes)));
                }
            }
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_medical_history_child_exclusive_breastfeeding_title));
            medicalHistory.setText(nutritionDetails);

            if (medicalHistories == null) {
                medicalHistories = new ArrayList<>();
            }

            medicalHistories.add(medicalHistory);
        }
    }

    /**
     * Extract value from VisitDetail
     *
     * @return
     */
    @NotNull
    protected String getText(@Nullable VisitDetail visitDetail) {
        if (visitDetail == null) {
            return "";
        }

        String val = visitDetail.getHumanReadable();
        if (StringUtils.isNotBlank(val)) {
            return val.trim();
        }

        return (StringUtils.isNotBlank(visitDetail.getDetails())) ? visitDetail.getDetails().trim() : "";
    }

    protected void extractFamilyPlanningMethods(List<Visit> visits, Map<String, String> familyPlanningMap) {
        for (Visit v : visits) {
            for (Map.Entry<String, List<VisitDetail>> entry : v.getVisitDetails().entrySet()) {
                switch (entry.getKey()) {
                    case "fp_method":
                    case "fp_start_date":
                        familyPlanningMap.put(getText(v.getVisitDetails().get("fp_method")), getText(v.getVisitDetails().get("fp_start_date")));
                        break;
                }
            }
        }
    }

    protected MedicalHistoryAdapter getAdapter() {
        return new PncMedicalHistoryAdapter(medicalHistories, childLayout);
    }

    protected void addMotherDetailsView(String motherName) {
        View view = new PncMedicalHistoryViewBuilder(inflater, context)
                .withChildLayout(childLayout)
                .withRootLayout(rootLayout)
                .withSeparator(true)
                .withTitle(MessageFormat.format(context.getString(R.string.pnc_medical_history_mother_title), motherName).toUpperCase())
                .build();
        parentView.addView(view);
    }

    protected void addChildDetailsView(String childName) {
        View view = new PncMedicalHistoryViewBuilder(inflater, context)
                .withChildLayout(childLayout)
                .withSeparator(true)
                .withTitle(MessageFormat.format(context.getString(R.string.pnc_medical_history_child_title), childName).toUpperCase())
                .build();
        parentView.addView(view);
    }

    protected void addMedicalHistoriesView() {
        if (medicalHistories != null) {
            View view = new PncMedicalHistoryViewBuilder(inflater, context)
                    .withAdapter(getAdapter())
                    .withRootLayout(rootLayout)
                    .withSeparator(true)
                    .build();

            parentView.addView(view);
        }
    }
}