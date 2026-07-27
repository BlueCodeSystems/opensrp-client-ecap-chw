package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

import java.util.ArrayList;
import java.util.List;

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

        for (Child child : children) {
            try {
                String uniqueId = child.getUnique_id();

                VcaScreeningModel screening = null;
                try {
                    screening = VCAScreeningDao.getVcaScreening(uniqueId);
                } catch (Exception ignored) {
                }
                if (screening != null && ("0".equals(screening.getCase_status()) || "2".equals(screening.getCase_status()))) {
                    continue;
                }

                String name = (child.getAdolescent_first_name() != null ? child.getAdolescent_first_name() : child.getFirst_name())
                        + " " +
                        (child.getAdolescent_last_name() != null ? child.getAdolescent_last_name() : child.getLast_name());
                String birthdate = child.getAdolescent_birthdate();

                String color = null;
                String date = null;

                VcaVisitationModel visit = null;
                try {
                    visit = VcaVisitationDao.getVcaVisitationNotification(uniqueId);
                } catch (Exception ignored) {
                }
                if (visit != null) {
                    color = visit.getStatus_color();
                    date = visit.getVisit_date();
                } else {
                    VcaAssessmentModel assessment = null;
                    try {
                        assessment = VcaAssessmentDao.getVcaVisitationNotificationFromAssessment(uniqueId);
                    } catch (Exception ignored) {
                    }
                    if (assessment != null) {
                        color = assessment.getStatus_color();
                        date = assessment.getDate_edited();
                    }
                }

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
