package org.smartregister.chw.core.rule;

import android.content.Context;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by mahmud on 23/07/2019.
 */
public class WashCheckAlertRule extends MonthlyAlertRule {

    public WashCheckAlertRule(Context context, long lastVisitDateLong, long dateCreatedLong) {
        super(context, lastVisitDateLong, dateCreatedLong);
    }

    @Override
    public String getRuleKey() {
        return "washCheckAlertRule";
    }

}
