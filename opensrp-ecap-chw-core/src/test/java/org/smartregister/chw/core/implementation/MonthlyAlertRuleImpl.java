package org.smartregister.chw.core.implementation;

import android.content.Context;

import org.smartregister.chw.core.rule.MonthlyAlertRule;

public class MonthlyAlertRuleImpl extends MonthlyAlertRule {

    public MonthlyAlertRuleImpl(Context context, long lastVisitDateLong, long dateCreatedLong) {
        super(context, lastVisitDateLong, dateCreatedLong);
    }

    @Override
    public String getRuleKey() {
        return null;
    }
}
