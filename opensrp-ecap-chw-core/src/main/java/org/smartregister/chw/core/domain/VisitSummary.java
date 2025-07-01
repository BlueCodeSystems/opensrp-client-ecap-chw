package org.smartregister.chw.core.domain;

import java.util.Date;

public class VisitSummary {
    private String visitType;
    private Date visitDate;
    private Date dateCreated;
    private String baseEntityID;

    public VisitSummary(String visitType, Date visitDate, Date dateCreated, String baseEntityId) {
        this.visitType = visitType;
        this.visitDate = visitDate;
        this.dateCreated = dateCreated;
        this.baseEntityID = baseEntityId;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }
}
