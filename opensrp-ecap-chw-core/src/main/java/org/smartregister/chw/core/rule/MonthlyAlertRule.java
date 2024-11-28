package org.smartregister.chw.core.rule;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;

public abstract class MonthlyAlertRule implements ICommonRule {

    private final int[] monthNames = {R.string.january, R.string.february, R.string.march, R.string.april, R.string.may, R.string.june, R.string.july, R.string.august, R.string.september, R.string.october, R.string.november, R.string.december};
    public String buttonStatus = CoreConstants.VisitType.DUE.name();
    public String noOfMonthDue;
    public String noOfDayDue;
    public String visitMonthName;
    private LocalDate dateCreated;
    private LocalDate todayDate;
    public LocalDate lastVisitDate;
    private Context context;

    public MonthlyAlertRule(Context context, long lastVisitDateLong, long dateCreatedLong) {

        this.context = context;

        this.todayDate = new LocalDate();
        if (lastVisitDateLong > 0) {
            this.lastVisitDate = new LocalDate(lastVisitDateLong);
            noOfDayDue = dayDifference(lastVisitDate, todayDate) + " days";
        }

        if (dateCreatedLong > 0) {
            this.dateCreated = new LocalDate(dateCreatedLong);
        }
    }

    private int dayDifference(LocalDate date1, LocalDate date2) {
        return Days.daysBetween(date1, date2).getDays();
    }

    public boolean isExpiry(Integer calYr) {
        return false;
    }

    public boolean isVisitNotDone() {
        return false;
    }

    public boolean isOverdueWithinMonth(Integer value) {
        int diff = getMonthsDifference((lastVisitDate != null ? lastVisitDate : dateCreated), todayDate);
        if (diff >= value) {
            noOfMonthDue = diff + "M";
            return true;
        }
        return false;
    }

    private int getMonthsDifference(LocalDate date1, LocalDate date2) {
        return Months.monthsBetween(
                date1.withDayOfMonth(1),
                date2.withDayOfMonth(1)).getMonths();
    }

    public boolean isDueWithinMonth() {
        if (todayDate.getDayOfMonth() == 1) {
            return true;
        }
        if (lastVisitDate == null) {
            return !isVisitThisMonth(dateCreated, todayDate);
        }

        return !isVisitThisMonth(lastVisitDate, todayDate);
    }

    private boolean isVisitThisMonth(LocalDate lastVisit, LocalDate todayDate) {
        return getMonthsDifference(lastVisit, todayDate) < 1;
    }

    public boolean isVisitWithinTwentyFour() {
        visitMonthName = theMonth(todayDate.getMonthOfYear() - 1);
        noOfDayDue = context.getString(R.string.less_than_twenty_four);
        return (lastVisitDate != null) && !(lastVisitDate.isBefore(todayDate.minusDays(1)) && lastVisitDate.isBefore(todayDate));
    }

    private String theMonth(int month) {
        return context.getResources().getString(monthNames[month]);
    }

    public boolean isVisitWithinThisMonth() {
        return (lastVisitDate != null) && isVisitThisMonth(lastVisitDate, todayDate);
    }

    public Date getFirstDayOfMonth(Date refDate) {
        return new DateTime(refDate).withDayOfMonth(1).toDate();
    }

    public Date getLastDayOfMonth(Date refDate) {
        DateTime first = new DateTime(refDate).withDayOfMonth(1);
        return first.plusMonths(1).minusDays(1).toDate();
    }

    /**
     * visit is due the first day of the month
     *
     * @return
     */
    public Date getDueDate() {
        Date lastDueDate = getLastDueDate();
        if (lastDueDate.getTime() < getFirstDayOfMonth(new Date()).getTime()) {
            return getFirstDayOfMonth(new Date());
        } else {
            return lastDueDate;
        }
    }

    public Date getLastDueDate() {
        if (lastVisitDate != null && getFirstDayOfMonth(lastVisitDate.toDate()).getTime() < dateCreated.toDate().getTime()) {
            return getFirstDayOfMonth(lastVisitDate.toDate());
        } else {
            return dateCreated != null ? dateCreated.toDate() : new LocalDate().toDate();
        }
    }

    public Date getCompletionDate() {
        if (lastVisitDate != null && lastVisitDate.toDate().getTime() >= getDueDate().getTime()) {
            return lastVisitDate.toDate();
        }
        return null;
    }

    public Date getExpiryDate() {
        return getLastDayOfMonth(new Date());
    }

    private LocalDate getDateCreated() {
        return dateCreated != null ? dateCreated : new LocalDate();
    }

    public Date getOverDueDate() {
        Date anchor = (lastVisitDate != null ? lastVisitDate.toDate() : getDateCreated().toDate());
        Date overDue = getLastDayOfMonth(anchor);
        if (overDue.getTime() < getDueDate().getTime()) {
            return getDueDate();
        }
        return overDue;
    }

    @Override
    public abstract String getRuleKey();

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }
}
