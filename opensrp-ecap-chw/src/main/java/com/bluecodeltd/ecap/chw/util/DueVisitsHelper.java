package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared source of truth for VCA due-visit status, reusing the same red/yellow/green
 * bucketing (VcaVisitationDao.getVcaVisitationNotification) that IndexRegisterViewHolder's
 * due_button and VcaVisitsActivity already rely on.
 */
public final class DueVisitsHelper {

    private DueVisitsHelper() {
    }

    public static class DueVisit {
        public final String uniqueId;
        public final String name;
        public final String birthdate;
        public final String statusColor;
        public final String visitDate;

        public DueVisit(String uniqueId, String name, String birthdate, String statusColor, String visitDate) {
            this.uniqueId = uniqueId;
            this.name = name;
            this.birthdate = birthdate;
            this.statusColor = statusColor;
            this.visitDate = visitDate;
        }
    }

    public static List<DueVisit> getDueVisits(String caseworkerPhone) {
        List<DueVisit> result = new ArrayList<>();

        List<Child> children = null;
        try {
            children = IndexPersonDao.getAllChildrenSubpopsByCaseworkerPhoneNumber(caseworkerPhone);
        } catch (Exception ignored) {
        }
        if (children == null) {
            return result;
        }

        // One query each for every VCA's latest visit and assessment. This used to run three queries
        // per VCA (screening, visit, assessment); for large caseloads that kept the DB lock busy long
        // enough to stall the UI every time the VCA register opened.
        Map<String, String[]> latestVisits = new HashMap<>();
        Map<String, String[]> latestAssessments = new HashMap<>();
        try {
            latestVisits = VcaVisitationDao.getLatestVisitStatusByCaseworker(caseworkerPhone);
        } catch (Exception ignored) {
        }
        try {
            latestAssessments = VcaAssessmentDao.getLatestAssessmentStatusByCaseworker(caseworkerPhone);
        } catch (Exception ignored) {
        }

        for (Child child : children) {
            try {
                String uniqueId = child.getUnique_id();

                // case_status comes from the same ec_client_index row VCAScreeningDao.getVcaScreening re-read.
                String caseStatus = child.getCase_status();
                if ("0".equals(caseStatus) || "2".equals(caseStatus)) {
                    continue;
                }

                String name = (child.getAdolescent_first_name() != null ? child.getAdolescent_first_name() : child.getFirst_name())
                        + " " +
                        (child.getAdolescent_last_name() != null ? child.getAdolescent_last_name() : child.getLast_name());
                String birthdate = child.getAdolescent_birthdate();

                String[] status = latestVisits.get(uniqueId);
                if (status == null) {
                    status = latestAssessments.get(uniqueId);
                }
                String color = status != null ? status[0] : null;
                String date = status != null ? status[1] : null;

                if (color != null && date != null) {
                    result.add(new DueVisit(uniqueId, name, birthdate, color.trim(), date));
                }
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    /**
     * "Due" for notification purposes means it needs a CHW's attention: overdue (red) or
     * due soon (yellow). "green" (recently visited, not yet due) is intentionally excluded.
     */
    public static List<DueVisit> filterDue(List<DueVisit> all) {
        List<DueVisit> due = new ArrayList<>();
        for (DueVisit visit : all) {
            if ("red".equalsIgnoreCase(visit.statusColor) || "yellow".equalsIgnoreCase(visit.statusColor)) {
                due.add(visit);
            }
        }
        return due;
    }
}
