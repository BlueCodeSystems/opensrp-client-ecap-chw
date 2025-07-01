package org.smartregister.chw.core.model;

public class ChildVisit {
    private String visitStatus;
    private long lastVisitTime;
    private String lastVisitMonthName;
    private String lastVisitDays;

    private String noOfMonthDue;
    private Long lastNotVisitDate;


    public String getNoOfMonthDue() {
        return noOfMonthDue;
    }

    public void setNoOfMonthDue(String noOfMonthDue) {
        this.noOfMonthDue = noOfMonthDue;
    }

    public String getLastVisitDays() {
        return lastVisitDays;
    }

    public void setLastVisitDays(String lastVisitDays) {
        this.lastVisitDays = lastVisitDays;
    }


    public String getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(String visitStatus) {
        this.visitStatus = visitStatus;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(long lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }


    public String getLastVisitMonthName() {
        return lastVisitMonthName;
    }

    public void setLastVisitMonthName(String lastVisitMonthName) {
        this.lastVisitMonthName = lastVisitMonthName;
    }

    public Long getLastNotVisitDate() {
        return lastNotVisitDate;
    }

    public void setLastNotVisitDate(Long lastNotVisitDate) {
        this.lastNotVisitDate = lastNotVisitDate;
    }
}
