package org.smartregister.chw.core.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.dao.ChwNotificationDao;
import org.smartregister.chw.core.domain.NotificationRecord;

@Implements(ChwNotificationDao.class)
public class ChwNotificationDaoShadowHelper {

    @Implementation
    public static NotificationRecord getSickChildFollowUpRecord(String notificationId) {
        NotificationRecord record = initNotificationRecord();
        record.setCareGiverName("Mama Yake");
        record.setDiagnosis("Dehydration");
        return record;
    }

    @Implementation
    public static NotificationRecord getAncPncDangerSignsOutcomeRecord(String notificationId, String table) {
        NotificationRecord record = initNotificationRecord();
        record.setDangerSigns("Headache");
        record.setActionTaken("Referred");
        return record;
    }

    @Implementation
    public static NotificationRecord getMalariaFollowUpRecord(String notificationId) {
        NotificationRecord record = initNotificationRecord();
        record.setResults("Malaria");
        record.setActionTaken("Referred");
        return record;
    }

    @Implementation
    public static NotificationRecord getFamilyPlanningRecord(String notificationId) {
        NotificationRecord record = initNotificationRecord();
        record.setMethod("Pills");
        return record;
    }

    private static NotificationRecord initNotificationRecord() {
        NotificationRecord record = new NotificationRecord("test-base-entity-id");
        record.setClientName("Test Client");
        record.setVisitDate("2020-05-21 17:16:31");
        record.setVillage("Gachie");
        return record;
    }

}
