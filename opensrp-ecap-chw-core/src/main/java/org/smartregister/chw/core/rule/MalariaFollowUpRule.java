package org.smartregister.chw.core.rule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;

public class MalariaFollowUpRule implements ICommonRule {

    public static final String RULE_KEY = "malariaFollowUpRule";

    @NonNull
    private Date testDate;
    @Nullable
    private Date latestFollowUpDate;
    private String buttonStatus = "";

    public MalariaFollowUpRule(@NonNull Date testDate, @Nullable Date latestFollowUpDate) {
        this.testDate = testDate;
        this.latestFollowUpDate = latestFollowUpDate;
    }

    public int getDatesDiff() {
        return Days.daysBetween(new DateTime(testDate), new DateTime()).getDays();
    }

    public void setButtonStatus(String buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    public boolean isExpired(){
        return latestFollowUpDate != null && latestFollowUpDate.getTime() > testDate.getTime();
    }

    @Override
    public String getRuleKey() {
        return "malariaFollowUpRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }

}
