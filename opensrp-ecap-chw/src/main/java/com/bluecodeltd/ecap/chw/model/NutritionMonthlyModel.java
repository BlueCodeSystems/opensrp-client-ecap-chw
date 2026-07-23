package com.bluecodeltd.ecap.chw.model;

public class NutritionMonthlyModel {

    private String baseEntityId;
    private String reportingPeriod;
    private String reportingYear;
    private String facilityName;
    private String reporterName;
    private String province;
    private String district;
    private String ward;
    private String partner;
    private String reportStatus;

    // Section A
    private String cAlhiv, hei, cwlhiv, cPbfa, siblings;

    // Section B
    private String hhPracticingDietDiversity, hhPracticingExclusiveBf, hhPracticingComplementaryFeeding, hhWashActivities, hhVisitedAssessment, ppmamIdentified, ppmamReferredCommenced, otherChildrenPmam, plwArtPmtctNutritionAssessment, plwReceivedIfas, hhFoodInsecurityCounselled;

    // Section C
    private String mnpChildren623Received, mnpPlwReceived, vitaChildren611Months, vitaChildren1259Months, vitaPlwSupplemented, dewormingChildren1259, dewormingPlw;

    // Section D
    private String ecdCentresSupportedMonitoring, ecdCentresWithFeeding, ecdChildrenEnrolled, ecdCaregiversTrained, ecdDevelopmentalScreening;

    // Section E
    private String wfaUnderweight, wfaOverweight, wfaNormal;

    // Section F
    private String nutritionGrade1, nutritionGrade2, nutritionNr;

    // Section G
    private String muacRedBelow115, muacYellow115To125, muacGreen125Plus, muacOedema;

    // Section H
    private String stiReferred, stiTreated;

    // Section I
    private String referralNutritionToHealth, referralFeedbackReceived, referralDateOfReferral, referralDateOfFeedback, referralHivTbIntegration;

    private String comment;

    // Getters and Setters
    public String getBaseEntityId() { return baseEntityId; }
    public void setBaseEntityId(String baseEntityId) { this.baseEntityId = baseEntityId; }
    public String getReportingPeriod() { return reportingPeriod; }
    public void setReportingPeriod(String reportingPeriod) { this.reportingPeriod = reportingPeriod; }
    public String getReportingYear() { return reportingYear; }
    public void setReportingYear(String reportingYear) { this.reportingYear = reportingYear; }
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    public String getCAlhiv() { return cAlhiv; }
    public void setCAlhiv(String cAlhiv) { this.cAlhiv = cAlhiv; }
    public String getHei() { return hei; }
    public void setHei(String hei) { this.hei = hei; }
    public String getCwlhiv() { return cwlhiv; }
    public void setCwlhiv(String cwlhiv) { this.cwlhiv = cwlhiv; }
    public String getCPbfa() { return cPbfa; }
    public void setCPbfa(String cPbfa) { this.cPbfa = cPbfa; }
    public String getSiblings() { return siblings; }
    public void setSiblings(String siblings) { this.siblings = siblings; }

    public String getHhPracticingDietDiversity() { return hhPracticingDietDiversity; }
    public void setHhPracticingDietDiversity(String hhPracticingDietDiversity) { this.hhPracticingDietDiversity = hhPracticingDietDiversity; }
    public String getHhPracticingExclusiveBf() { return hhPracticingExclusiveBf; }
    public void setHhPracticingExclusiveBf(String hhPracticingExclusiveBf) { this.hhPracticingExclusiveBf = hhPracticingExclusiveBf; }
    public String getHhPracticingComplementaryFeeding() { return hhPracticingComplementaryFeeding; }
    public void setHhPracticingComplementaryFeeding(String hhPracticingComplementaryFeeding) { this.hhPracticingComplementaryFeeding = hhPracticingComplementaryFeeding; }
    public String getHhWashActivities() { return hhWashActivities; }
    public void setHhWashActivities(String hhWashActivities) { this.hhWashActivities = hhWashActivities; }
    public String getHhVisitedAssessment() { return hhVisitedAssessment; }
    public void setHhVisitedAssessment(String hhVisitedAssessment) { this.hhVisitedAssessment = hhVisitedAssessment; }

    public String getPpmamIdentified() { return ppmamIdentified; }
    public void setPpmamIdentified(String ppmamIdentified) { this.ppmamIdentified = ppmamIdentified; }
    public String getPpmamReferredCommenced() { return ppmamReferredCommenced; }
    public void setPpmamReferredCommenced(String ppmamReferredCommenced) { this.ppmamReferredCommenced = ppmamReferredCommenced; }
    public String getOtherChildrenPmam() { return otherChildrenPmam; }
    public void setOtherChildrenPmam(String otherChildrenPmam) { this.otherChildrenPmam = otherChildrenPmam; }
    public String getPlwArtPmtctNutritionAssessment() { return plwArtPmtctNutritionAssessment; }
    public void setPlwArtPmtctNutritionAssessment(String plwArtPmtctNutritionAssessment) { this.plwArtPmtctNutritionAssessment = plwArtPmtctNutritionAssessment; }
    public String getPlwReceivedIfas() { return plwReceivedIfas; }
    public void setPlwReceivedIfas(String plwReceivedIfas) { this.plwReceivedIfas = plwReceivedIfas; }
    public String getHhFoodInsecurityCounselled() { return hhFoodInsecurityCounselled; }
    public void setHhFoodInsecurityCounselled(String hhFoodInsecurityCounselled) { this.hhFoodInsecurityCounselled = hhFoodInsecurityCounselled; }

    public String getMnpChildren623Received() { return mnpChildren623Received; }
    public void setMnpChildren623Received(String mnpChildren623Received) { this.mnpChildren623Received = mnpChildren623Received; }
    public String getMnpPlwReceived() { return mnpPlwReceived; }
    public void setMnpPlwReceived(String mnpPlwReceived) { this.mnpPlwReceived = mnpPlwReceived; }
    public String getVitaChildren611Months() { return vitaChildren611Months; }
    public void setVitaChildren611Months(String vitaChildren611Months) { this.vitaChildren611Months = vitaChildren611Months; }
    public String getVitaChildren1259Months() { return vitaChildren1259Months; }
    public void setVitaChildren1259Months(String vitaChildren1259Months) { this.vitaChildren1259Months = vitaChildren1259Months; }
    public String getVitaPlwSupplemented() { return vitaPlwSupplemented; }
    public void setVitaPlwSupplemented(String vitaPlwSupplemented) { this.vitaPlwSupplemented = vitaPlwSupplemented; }
    public String getDewormingChildren1259() { return dewormingChildren1259; }
    public void setDewormingChildren1259(String dewormingChildren1259) { this.dewormingChildren1259 = dewormingChildren1259; }
    public String getDewormingPlw() { return dewormingPlw; }
    public void setDewormingPlw(String dewormingPlw) { this.dewormingPlw = dewormingPlw; }

    public String getEcdCentresSupportedMonitoring() { return ecdCentresSupportedMonitoring; }
    public void setEcdCentresSupportedMonitoring(String ecdCentresSupportedMonitoring) { this.ecdCentresSupportedMonitoring = ecdCentresSupportedMonitoring; }
    public String getEcdCentresWithFeeding() { return ecdCentresWithFeeding; }
    public void setEcdCentresWithFeeding(String ecdCentresWithFeeding) { this.ecdCentresWithFeeding = ecdCentresWithFeeding; }
    public String getEcdChildrenEnrolled() { return ecdChildrenEnrolled; }
    public void setEcdChildrenEnrolled(String ecdChildrenEnrolled) { this.ecdChildrenEnrolled = ecdChildrenEnrolled; }
    public String getEcdCaregiversTrained() { return ecdCaregiversTrained; }
    public void setEcdCaregiversTrained(String ecdCaregiversTrained) { this.ecdCaregiversTrained = ecdCaregiversTrained; }
    public String getEcdDevelopmentalScreening() { return ecdDevelopmentalScreening; }
    public void setEcdDevelopmentalScreening(String ecdDevelopmentalScreening) { this.ecdDevelopmentalScreening = ecdDevelopmentalScreening; }

    public String getWfaUnderweight() { return wfaUnderweight; }
    public void setWfaUnderweight(String wfaUnderweight) { this.wfaUnderweight = wfaUnderweight; }
    public String getWfaOverweight() { return wfaOverweight; }
    public void setWfaOverweight(String wfaOverweight) { this.wfaOverweight = wfaOverweight; }
    public String getWfaNormal() { return wfaNormal; }
    public void setWfaNormal(String wfaNormal) { this.wfaNormal = wfaNormal; }

    public String getNutritionGrade1() { return nutritionGrade1; }
    public void setNutritionGrade1(String nutritionGrade1) { this.nutritionGrade1 = nutritionGrade1; }
    public String getNutritionGrade2() { return nutritionGrade2; }
    public void setNutritionGrade2(String nutritionGrade2) { this.nutritionGrade2 = nutritionGrade2; }
    public String getNutritionNr() { return nutritionNr; }
    public void setNutritionNr(String nutritionNr) { this.nutritionNr = nutritionNr; }

    public String getMuacRedBelow115() { return muacRedBelow115; }
    public void setMuacRedBelow115(String muacRedBelow115) { this.muacRedBelow115 = muacRedBelow115; }
    public String getMuacYellow115To125() { return muacYellow115To125; }
    public void setMuacYellow115To125(String muacYellow115To125) { this.muacYellow115To125 = muacYellow115To125; }
    public String getMuacGreen125Plus() { return muacGreen125Plus; }
    public void setMuacGreen125Plus(String muacGreen125Plus) { this.muacGreen125Plus = muacGreen125Plus; }
    public String getMuacOedema() { return muacOedema; }
    public void setMuacOedema(String muacOedema) { this.muacOedema = muacOedema; }

    public String getStiReferred() { return stiReferred; }
    public void setStiReferred(String stiReferred) { this.stiReferred = stiReferred; }
    public String getStiTreated() { return stiTreated; }
    public void setStiTreated(String stiTreated) { this.stiTreated = stiTreated; }

    public String getReferralNutritionToHealth() { return referralNutritionToHealth; }
    public void setReferralNutritionToHealth(String referralNutritionToHealth) { this.referralNutritionToHealth = referralNutritionToHealth; }
    public String getReferralFeedbackReceived() { return referralFeedbackReceived; }
    public void setReferralFeedbackReceived(String referralFeedbackReceived) { this.referralFeedbackReceived = referralFeedbackReceived; }
    public String getReferralDateOfReferral() { return referralDateOfReferral; }
    public void setReferralDateOfReferral(String referralDateOfReferral) { this.referralDateOfReferral = referralDateOfReferral; }
    public String getReferralDateOfFeedback() { return referralDateOfFeedback; }
    public void setReferralDateOfFeedback(String referralDateOfFeedback) { this.referralDateOfFeedback = referralDateOfFeedback; }
    public String getReferralHivTbIntegration() { return referralHivTbIntegration; }
    public void setReferralHivTbIntegration(String referralHivTbIntegration) { this.referralHivTbIntegration = referralHivTbIntegration; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
