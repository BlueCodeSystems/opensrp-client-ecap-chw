package org.smartregister.chw.core.rule;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.Date;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by Qazi Abubakar
 */
public class FamilyKitAlertRule extends MonthlyAlertRule {

    public FamilyKitAlertRule(Context context, long lastVisitDateLong, long dateCreatedLong) {
        super(context, lastVisitDateLong, dateCreatedLong);
    }

    public Date getDueDate() {
        Date lastDueDate = getLastDueDate();
        if (lastDueDate.getTime() < getFirstDayOfMonth(new Date()).getTime()) {
            if (lastVisitDate != null && getFirstDayOfMonth(lastVisitDate.toDate()).getTime() < getFirstDayOfMonth(new Date()).getTime()) {
                return getFirstDayOfMonth(new DateTime(lastVisitDate.toDate()).plusMonths(1).toDate());
            }
            return getFirstDayOfMonth(new Date());
        } else {
            return lastDueDate;
        }
    }

    @Override
    public String getRuleKey() {
        return "familyKitAlertRule";
    }

}
