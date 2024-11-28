package org.smartregister.chw.core.helper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.anc.domain.GroupedVisit;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.DefaultPncMedicalHistoryActivityFlv;
import org.smartregister.chw.core.domain.MedicalHistory;
import org.smartregister.dao.AbstractDao;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaMedicalHistoryActivityHelper extends DefaultPncMedicalHistoryActivityFlv {
    private Date deliveryDate;
    private String visitDateFormattedString;
    private Context context;

    public void processViewData(List<GroupedVisit> groupedVisits, Context context, MemberObject memberObject) {
        this.context = context;
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

    protected void extractHealthFacilityVisits(List<Visit> visits, Map<String, String> healthFacilityVisitMap, int count) {
        int x = 1;
        int maxNumOfVisits = 4;
        while (x <= maxNumOfVisits) {
            if (visits != null) {
                List<VisitDetail> visitDetails = visits.get(count).getVisitDetails().get("pnc_visit_" + x);
                if (visitDetails != null) {
                    for (VisitDetail visitDetail : visitDetails
                    ) {
                        if (visitDetail.getHumanReadable().equalsIgnoreCase("Yes")) {

                            List<VisitDetail> hf_details_dates = visits.get(count).getVisitDetails().get("pnc_hf_visit" + x + "_date");

                            if (hf_details_dates != null) {
                                for (VisitDetail hfDate : hf_details_dates) {
                                    healthFacilityVisitMap.put(visitDetail.getVisitKey(), hfDate.getDetails());
                                }
                            }
                        }
                    }
                }
                x++;
            }
        }
    }

    protected void extractHomeVisits(List<Visit> visits, Map<Integer, String> homeVisitMap) {
        if (visits != null) {
            String baseEntityId = visits.get(0).getBaseEntityId();
            if (baseEntityId != null) {
                String deliveryDateSql = "SELECT delivery_date FROM ec_pregnancy_outcome where base_entity_id = ? ";
                List<Map<String, Object>> deliveryDateResults = AbstractDao.readData(deliveryDateSql, new String[]{baseEntityId});
                if (deliveryDateResults.size() > 0) {
                    String deliveryDate = deliveryDateResults.get(0).get("delivery_date").toString();
                    for (Visit visit : visits) {
                        try {
                            DateFormat deliveryDateDF = new SimpleDateFormat("dd-MM-yyyy");
                            DateFormat visitDateDF = new SimpleDateFormat("yyyy-MM-dd");
                            this.deliveryDate = deliveryDateDF.parse(deliveryDate);
                            visitDateFormattedString = visitDateDF.format(visit.getDate());
                        } catch (ParseException e) {
                            Timber.e(e, e.toString());
                        }
                        int daysBetween = Days.daysBetween((new DateTime(this.deliveryDate)), new DateTime(visit.getDate())).getDays();
                        homeVisitMap.put(daysBetween, visitDateFormattedString);
                    }
                }
            }
        }
    }

    protected void processMotherDetails(List<Visit> visits, MemberObject memberObject) {
        this.visits = visits;
        Map<Integer, String> homeVisitMap = new LinkedHashMap<>();
        Map<String, String> healthFacilityVisitMap = new LinkedHashMap<>();
        int x = 0;
        while (x < visits.size()) {
            extractHealthFacilityVisits(visits, healthFacilityVisitMap, x);
            x++;
        }

        extractHomeVisits(visits, homeVisitMap);
        processLastVisitDate(memberObject.getBaseEntityId());
        addMotherDetailsView(memberObject.getFullName());

        medicalHistories = new ArrayList<>(); // New history list for mother's details
        processHFacilityVisit(healthFacilityVisitMap);
        processHomeVisits(homeVisitMap);
        processFamilyPlanning(visits);
        addMedicalHistoriesView();
    }

    protected void processChildDetails(List<Visit> visits, String memberName) {
        this.visits = visits;
        Map<String, String> immunization = new HashMap<>();
        Map<String, String> growthData = new HashMap<>();
        String childName = StringUtils.isNotBlank(memberName) ? memberName : "";

        for (Visit visit : visits) {
            for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                String val = getText(entry.getValue());

                switch (entry.getKey()) {
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

        medicalHistories = new ArrayList<>(); // New history list for child details
        super.processImmunization(immunization);
        super.processGrowthAndNutrition(growthData);

        addMedicalHistoriesView();
    }

    protected void processHomeVisits(Map<Integer, String> homeVisits) {
        if (homeVisits != null && homeVisits.size() > 0) {
            List<String> homeVisitDetails = new ArrayList<>();
            int key;
            String date;
            for (Map.Entry<Integer, String> entry : homeVisits.entrySet()) {
                key = entry.getKey();
                date = entry.getValue();
                homeVisitDetails.add(getHomeVisitDetail(key, date));
            }

            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_home_visits_title));
            medicalHistory.setText(homeVisitDetails);

            if (medicalHistories == null) {
                medicalHistories = new ArrayList<>();
            }
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processHFacilityVisit(Map<String, String> healthFacilityVisits) {
        if (healthFacilityVisits != null && healthFacilityVisits.size() > 0) {
            List<String> hfVisitDetails = new ArrayList<>();
            for (Map.Entry<String, String> entry : healthFacilityVisits.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("pnc_visit_3")) {
                    hfVisitDetails.add(MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_eight_to_twenty_eight), entry.getValue()));
                }
                if (entry.getKey().equalsIgnoreCase("pnc_visit_1")) {
                    hfVisitDetails.add(MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_48_hours), entry.getValue()));
                }
                if (entry.getKey().equalsIgnoreCase("pnc_visit_2")) {
                    hfVisitDetails.add(MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_three_to_seven_days), entry.getValue()));
                }
                if (entry.getKey().equalsIgnoreCase("pnc_visit_4")) {
                    hfVisitDetails.add(MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_twenty_nine_to_forty_two), entry.getValue()));
                }
            }

            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setText(hfVisitDetails);
            medicalHistory.setTitle(context.getString(R.string.pnc_health_facility_visits_title));
            medicalHistories.add(medicalHistory);
        }
    }

    protected void processFamilyPlanning(List<Visit> visits) {
        Map<String, String> familyPlanningMap = new HashMap<>();
        extractFamilyPlanningMethods(visits, familyPlanningMap);

        if (familyPlanningMap.size() > 0) {
            List<String> fpDetails = new ArrayList<>();
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_medical_history_family_planning_title));
            Iterator<Map.Entry<String, String>> mapIterator = familyPlanningMap.entrySet().iterator();
            while (mapIterator.hasNext()) {
                Map.Entry<String, String> entry = mapIterator.next();
                if (entry.getKey() != null) {
                    String method = "";
                    switch (entry.getKey()) {
                        case "None":
                            method = context.getString(R.string.pnc_none);
                            break;
                        case "PPIUCD":
                            method = context.getString(R.string.ppiucd);
                            break;
                        case "Pills":
                            method = context.getString(R.string.pills);
                            break;
                        case "Condoms":
                            method = context.getString(R.string.condoms);
                            break;
                        case "LAM":
                            method = context.getString(R.string.lam);
                            break;
                        case "Bead Counting":
                            method = context.getString(R.string.standard_day_method);
                            break;
                        case "Permanent (BTL)":
                            method = context.getString(R.string.permanent_blt);
                            break;
                        case "Permanent (Vasectomy)":
                            method = context.getString(R.string.permanent_vasectomy);
                            break;
                    }
                    fpDetails.add(MessageFormat.format(context.getString(R.string.pnc_family_planning_method), method));
                }
                if (entry.getValue() != null) {
                    fpDetails.add(MessageFormat.format(context.getString(R.string.pnc_family_planning_date), entry.getValue()));
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

    protected void processHealthFacilityVisit(Map<String, Map<String, String>> healthFacilityVisitMap) {
        if (healthFacilityVisitMap != null && healthFacilityVisitMap.size() > 0) {
            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setTitle(context.getString(R.string.pnc_health_facility_visits_title));
            Iterator<Map.Entry<String, Map<String, String>>> mapIterator = healthFacilityVisitMap.entrySet().iterator();
            List<String> hfVisitDetails = new ArrayList<>();
            while (mapIterator.hasNext()) {
                Map.Entry<String, Map<String, String>> entry = mapIterator.next();
                hfVisitDetails.add(MessageFormat.format(context.getString(R.string.pnc_wcaro_health_facility_visit), entry.getValue().get("pnc_hf_visit_date")));
                if (entry.getValue().get("baby_temp") != null)
                    hfVisitDetails.add(context.getString(R.string.pnc_baby_temp, entry.getValue().get("baby_temp")));
                if (mapIterator.hasNext()) {
                    hfVisitDetails.add("");
                }
            }
            medicalHistory.setText(hfVisitDetails);
            if (medicalHistories == null) {
                medicalHistories = new ArrayList<>();
            }
            medicalHistories.add(medicalHistory);
        }
    }

    private String getHomeVisitDetail(int key, String date) {
        if (key <= 2) {
            return MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_home_day_one_visit), date);
        }
        if (key == 3) {
            return MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_home_day_three_visit), date);
        }
        if (key <= 8) {
            return MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_home_day_eight_visit), date);
        }
        if (key <= 27) {
            return MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_home_day_twenty_one_to_twenty_seven_visit), date);
        }
        if (key <= 42) {
            return MessageFormat.format(context.getString(R.string.pnc_visit_date), context.getString(R.string.pnc_home_day_thirty_five_to_forty_one_visit), date);
        } else {
            return "";
        }
    }
}
