package org.smartregister.chw.anc.domain;

import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.Date;

public class VaccineDisplay {
    private VaccineWrapper vaccineWrapper;
    private Date startDate;
    private Date endDate;
    private Date dateGiven;
    private Boolean isValid;

    public VaccineWrapper getVaccineWrapper() {
        return vaccineWrapper;
    }

    public void setVaccineWrapper(VaccineWrapper vaccineWrapper) {
        this.vaccineWrapper = vaccineWrapper;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(Date dateGiven) {
        this.dateGiven = dateGiven;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
