package org.smartregister.chw.core.rule;

import android.content.Context;

public class RoutineHouseHoldVisitRule extends MonthlyAlertRule {

    public RoutineHouseHoldVisitRule(Context context, long lastVisitDateLong, long dateCreatedLong) {
        super(context, lastVisitDateLong, dateCreatedLong);
    }

    @Override
    public String getRuleKey() {
        return "routineHouseHoldVisitRule";
    }
}
