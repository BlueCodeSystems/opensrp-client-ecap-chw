package com.bluecodeltd.ecap.chw.model;

public class MalariaMonthlyModel {

    private String baseEntityId;
    private String reportingMonth;
    private String reportingYear;
    private String facilityName;
    private String reporterName;
    private String reportStatus;
    private String province;
    private String district;
    private String ward;
    private String partner;

    // Section A Q1
    private String saQ1F04, saQ1F515, saQ1F1619, saQ1F20Plus;
    private String saQ1FCalhiv, saQ1FHei, saQ1FWlhiv, saQ1FSv, saQ1FAgyw, saQ1FHivPos, saQ1FSiblings, saQ1FCaregivers;
    private String saQ1M04, saQ1M515, saQ1M1619, saQ1M20Plus;
    private String saQ1MCalhiv, saQ1MHei, saQ1MWlhiv, saQ1MSv, saQ1MSiblings, saQ1MCaregivers;
    
    // Section A Q2
    private String saQ2F04, q2F515, q2F1619, q2F20Plus;
    private String q2FCalhiv, q2FHei, q2FWlhiv, q2FSv, q2FAgyw, q2FHivPos, q2FSiblings, q2FCaregivers;
    private String q2M04, q2M515, q2M1619, q2M20Plus;
    private String q2MCalhiv, q2MHei, q2MWlhiv, q2MSv, q2MSiblings, q2MCaregivers;

    // Section A Q3
    private String q3F04, q3F515, q3F1619, q3F20Plus;
    private String q3FCalhiv, q3FHei, q3FWlhiv, q3FSv, q3FAgyw, q3FHivPos, q3FSiblings, q3FCaregivers;
    private String q3M04, q3M515, q3M1619, q3M20Plus;
    private String q3MCalhiv, q3MHei, q3MWlhiv, q3MSv, q3MSiblings, q3MCaregivers;

    // Section A Q4-Q10
    private String q4F04, q4F515, q4F1619, q4F20Plus, q4M04, q4M515, q4M1619, q4M20Plus;
    private String q4FCalhiv, q4FHei, q4FWlhiv, q4FSv, q4FAgyw, q4FHivPos, q4FSiblings, q4FCaregivers;
    private String q4MCalhiv, q4MHei, q4MWlhiv, q4MSv, q4MSiblings, q4MCaregivers;

    private String q5F04, q5F515, q5F1619, q5F20Plus, q5M04, q5M515, q5M1619, q5M20Plus;
    private String q5FCalhiv, q5FHei, q5FWlhiv, q5FSv, q5FAgyw, q5FHivPos, q5FSiblings, q5FCaregivers;
    private String q5MCalhiv, q5MHei, q5MWlhiv, q5MSv, q5MSiblings, q5MCaregivers;

    private String q6F04, q6F515, q6F1619, q6F20Plus, q6M04, q6M515, q6M1619, q6M20Plus;
    private String q6FCalhiv, q6FHei, q6FWlhiv, q6FSv, q6FAgyw, q6FHivPos, q6FSiblings, q6FCaregivers;
    private String q6MCalhiv, q6MHei, q6MWlhiv, q6MSv, q6MSiblings, q6MCaregivers;

    private String q7F04, q7F515, q7F1619, q7F20Plus, q7M04, q7M515, q7M1619, q7M20Plus;
    private String q7FCalhiv, q7FHei, q7FWlhiv, q7FSv, q7FAgyw, q7FHivPos, q7FSiblings, q7FCaregivers;
    private String q7MCalhiv, q7MHei, q7MWlhiv, q7MSv, q7MSiblings, q7MCaregivers;

    private String q8F04, q8F515, q8F1619, q8F20Plus, q8M04, q8M515, q8M1619, q8M20Plus;
    private String q8FCalhiv, q8FHei, q8FWlhiv, q8FSv, q8FAgyw, q8FHivPos, q8FSiblings, q8FCaregivers;
    private String q8MCalhiv, q8MHei, q8MWlhiv, q8MSv, q8MSiblings, q8MCaregivers;

    private String q9F04, q9F515, q9F1619, q9F20Plus, q9M04, q9M515, q9M1619, q9M20Plus;
    private String q9FCalhiv, q9FHei, q9FWlhiv, q9FSv, q9FAgyw, q9FHivPos, q9FSiblings, q9FCaregivers;
    private String q9MCalhiv, q9MHei, q9MWlhiv, q9MSv, q9MSiblings, q9MCaregivers;

    private String q10F04, q10F515, q10F1619, q10F20Plus, q10M04, q10M515, q10M1619, q10M20Plus;
    private String q10FCalhiv, q10FHei, q10FWlhiv, q10FSv, q10FAgyw, q10FHivPos, q10FSiblings, q10FCaregivers;
    private String q10MCalhiv, q10MHei, q10MWlhiv, q10MSv, q10MSiblings, q10MCaregivers;

    // Section B Q1
    private String sbQ1F04, sbQ1F515, sbQ1F1619, sbQ1F20Plus;
    private String sbQ1M04, sbQ1M515, sbQ1M1619, sbQ1M20Plus;

    // Section B Q2
    private String sbQ2F04, sbQ2F515, sbQ2F1619, sbQ2F20Plus;
    private String sbQ2M04, sbQ2M515, sbQ2M1619, sbQ2M20Plus;

    // Section B Q3
    private String sbQ3F04, sbQ3F515, sbQ3F1619, sbQ3F20Plus;
    private String sbQ3M04, sbQ3M515, sbQ3M1619, sbQ3M20Plus;

    // Section B Q4-Q10
    private String sbQ4F04, sbQ4F515, sbQ4F1619, sbQ4F20Plus, sbQ4M04, sbQ4M515, sbQ4M1619, sbQ4M20Plus;
    private String sbQ5F04, sbQ5F515, sbQ5F1619, sbQ5F20Plus, sbQ5M04, sbQ5M515, sbQ5M1619, sbQ5M20Plus;
    private String sbQ6F04, sbQ6F515, sbQ6F1619, sbQ6F20Plus, sbQ6M04, sbQ6M515, sbQ6M1619, sbQ6M20Plus;
    private String sbQ7F04, sbQ7F515, sbQ7F1619, sbQ7F20Plus, sbQ7M04, sbQ7M515, sbQ7M1619, sbQ7M20Plus;
    private String sbQ8F04, sbQ8F515, sbQ8F1619, sbQ8F20Plus, sbQ8M04, sbQ8M515, sbQ8M1619, sbQ8M20Plus;
    private String sbQ9F04, sbQ9F515, sbQ9F1619, sbQ9F20Plus, sbQ9M04, sbQ9M515, sbQ9M1619, sbQ9M20Plus;
    private String sbQ10F04, sbQ10F515, sbQ10F1619, sbQ10F20Plus, sbQ10M04, sbQ10M515, sbQ10M1619, sbQ10M20Plus;

    // Section C
    private String scQ1, scQ2, scQ3, scQ4, scQ5, scQ6, scQ7;

    private String comment;

    // Getters and Setters
    public String getBaseEntityId() { return baseEntityId; }
    public void setBaseEntityId(String baseEntityId) { this.baseEntityId = baseEntityId; }
    public String getReportingMonth() { return reportingMonth; }
    public void setReportingMonth(String reportingMonth) { this.reportingMonth = reportingMonth; }
    public String getReportingYear() { return reportingYear; }
    public void setReportingYear(String reportingYear) { this.reportingYear = reportingYear; }
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public String getSaQ1F04() { return saQ1F04; }
    public void setSaQ1F04(String saQ1F04) { this.saQ1F04 = saQ1F04; }
    public String getSaQ1F515() { return saQ1F515; }
    public void setSaQ1F515(String saQ1F515) { this.saQ1F515 = saQ1F515; }
    public String getSaQ1F1619() { return saQ1F1619; }
    public void setSaQ1F1619(String saQ1F1619) { this.saQ1F1619 = saQ1F1619; }
    public String getSaQ1F20Plus() { return saQ1F20Plus; }
    public void setSaQ1F20Plus(String saQ1F20Plus) { this.saQ1F20Plus = saQ1F20Plus; }
    public String getSaQ1FCalhiv() { return saQ1FCalhiv; }
    public void setSaQ1FCalhiv(String saQ1FCalhiv) { this.saQ1FCalhiv = saQ1FCalhiv; }
    public String getSaQ1FHei() { return saQ1FHei; }
    public void setSaQ1FHei(String saQ1FHei) { this.saQ1FHei = saQ1FHei; }
    public String getSaQ1FWlhiv() { return saQ1FWlhiv; }
    public void setSaQ1FWlhiv(String saQ1FWlhiv) { this.saQ1FWlhiv = saQ1FWlhiv; }
    public String getSaQ1FSv() { return saQ1FSv; }
    public void setSaQ1FSv(String saQ1FSv) { this.saQ1FSv = saQ1FSv; }
    public String getSaQ1FAgyw() { return saQ1FAgyw; }
    public void setSaQ1FAgyw(String saQ1FAgyw) { this.saQ1FAgyw = saQ1FAgyw; }
    public String getSaQ1FHivPos() { return saQ1FHivPos; }
    public void setSaQ1FHivPos(String saQ1FHivPos) { this.saQ1FHivPos = saQ1FHivPos; }
    public String getSaQ1FSiblings() { return saQ1FSiblings; }
    public void setSaQ1FSiblings(String saQ1FSiblings) { this.saQ1FSiblings = saQ1FSiblings; }
    public String getSaQ1FCaregivers() { return saQ1FCaregivers; }
    public void setSaQ1FCaregivers(String saQ1FCaregivers) { this.saQ1FCaregivers = saQ1FCaregivers; }
    public String getSaQ1M04() { return saQ1M04; }
    public void setSaQ1M04(String saQ1M04) { this.saQ1M04 = saQ1M04; }
    public String getSaQ1M515() { return saQ1M515; }
    public void setSaQ1M515(String saQ1M515) { this.saQ1M515 = saQ1M515; }
    public String getSaQ1M1619() { return saQ1M1619; }
    public void setSaQ1M1619(String saQ1M1619) { this.saQ1M1619 = saQ1M1619; }
    public String getSaQ1M20Plus() { return saQ1M20Plus; }
    public void setSaQ1M20Plus(String saQ1M20Plus) { this.saQ1M20Plus = saQ1M20Plus; }
    public String getSaQ1MCalhiv() { return saQ1MCalhiv; }
    public void setSaQ1MCalhiv(String saQ1MCalhiv) { this.saQ1MCalhiv = saQ1MCalhiv; }
    public String getSaQ1MHei() { return saQ1MHei; }
    public void setSaQ1MHei(String saQ1MHei) { this.saQ1MHei = saQ1MHei; }
    public String getSaQ1MWlhiv() { return saQ1MWlhiv; }
    public void setSaQ1MWlhiv(String saQ1MWlhiv) { this.saQ1MWlhiv = saQ1MWlhiv; }
    public String getSaQ1MSv() { return saQ1MSv; }
    public void setSaQ1MSv(String saQ1MSv) { this.saQ1MSv = saQ1MSv; }
    public String getSaQ1MSiblings() { return saQ1MSiblings; }
    public void setSaQ1MSiblings(String saQ1MSiblings) { this.saQ1MSiblings = saQ1MSiblings; }
    public String getSaQ1MCaregivers() { return saQ1MCaregivers; }
    public void setSaQ1MCaregivers(String saQ1MCaregivers) { this.saQ1MCaregivers = saQ1MCaregivers; }

    public String getSaQ2F04() { return saQ2F04; }
    public void setSaQ2F04(String saQ2F04) { this.saQ2F04 = saQ2F04; }
    public String getQ2F515() { return q2F515; }
    public void setQ2F515(String q2F515) { this.q2F515 = q2F515; }
    public String getQ2F1619() { return q2F1619; }
    public void setQ2F1619(String q2F1619) { this.q2F1619 = q2F1619; }
    public String getQ2F20Plus() { return q2F20Plus; }
    public void setQ2F20Plus(String q2F20Plus) { this.q2F20Plus = q2F20Plus; }
    public String getQ2FCalhiv() { return q2FCalhiv; }
    public void setQ2FCalhiv(String q2FCalhiv) { this.q2FCalhiv = q2FCalhiv; }
    public String getQ2FHei() { return q2FHei; }
    public void setQ2FHei(String q2FHei) { this.q2FHei = q2FHei; }
    public String getQ2FWlhiv() { return q2FWlhiv; }
    public void setQ2FWlhiv(String q2FWlhiv) { this.q2FWlhiv = q2FWlhiv; }
    public String getQ2FSv() { return q2FSv; }
    public void setQ2FSv(String q2FSv) { this.q2FSv = q2FSv; }
    public String getQ2FAgyw() { return q2FAgyw; }
    public void setQ2FAgyw(String q2FAgyw) { this.q2FAgyw = q2FAgyw; }
    public String getQ2FHivPos() { return q2FHivPos; }
    public void setQ2FHivPos(String q2FHivPos) { this.q2FHivPos = q2FHivPos; }
    public String getQ2FSiblings() { return q2FSiblings; }
    public void setQ2FSiblings(String q2FSiblings) { this.q2FSiblings = q2FSiblings; }
    public String getQ2FCaregivers() { return q2FCaregivers; }
    public void setQ2FCaregivers(String q2FCaregivers) { this.q2FCaregivers = q2FCaregivers; }
    public String getQ2M04() { return q2M04; }
    public void setQ2M04(String q2M04) { this.q2M04 = q2M04; }
    public String getQ2M515() { return q2M515; }
    public void setQ2M515(String q2M515) { this.q2M515 = q2M515; }
    public String getQ2M1619() { return q2M1619; }
    public void setQ2M1619(String q2M1619) { this.q2M1619 = q2M1619; }
    public String getQ2M20Plus() { return q2M20Plus; }
    public void setQ2M20Plus(String q2M20Plus) { this.q2M20Plus = q2M20Plus; }
    public String getQ2MCalhiv() { return q2MCalhiv; }
    public void setQ2MCalhiv(String q2MCalhiv) { this.q2MCalhiv = q2MCalhiv; }
    public String getQ2MHei() { return q2MHei; }
    public void setQ2MHei(String q2MHei) { this.q2MHei = q2MHei; }
    public String getQ2MWlhiv() { return q2MWlhiv; }
    public void setQ2MWlhiv(String q2MWlhiv) { this.q2MWlhiv = q2MWlhiv; }
    public String getQ2MSv() { return q2MSv; }
    public void setQ2MSv(String q2MSv) { this.q2MSv = q2MSv; }
    public String getQ2MSiblings() { return q2MSiblings; }
    public void setQ2MSiblings(String q2MSiblings) { this.q2MSiblings = q2MSiblings; }
    public String getQ2MCaregivers() { return q2MCaregivers; }
    public void setQ2MCaregivers(String q2MCaregivers) { this.q2MCaregivers = q2MCaregivers; }

    public String getQ3F04() { return q3F04; }
    public void setQ3F04(String q3F04) { this.q3F04 = q3F04; }
    public String getQ3F515() { return q3F515; }
    public void setQ3F515(String q3F515) { this.q3F515 = q3F515; }
    public String getQ3F1619() { return q3F1619; }
    public void setQ3F1619(String q3F1619) { this.q3F1619 = q3F1619; }
    public String getQ3F20Plus() { return q3F20Plus; }
    public void setQ3F20Plus(String q3F20Plus) { this.q3F20Plus = q3F20Plus; }
    public String getQ3FCalhiv() { return q3FCalhiv; }
    public void setQ3FCalhiv(String q3FCalhiv) { this.q3FCalhiv = q3FCalhiv; }
    public String getQ3FHei() { return q3FHei; }
    public void setQ3FHei(String q3FHei) { this.q3FHei = q3FHei; }
    public String getQ3FWlhiv() { return q3FWlhiv; }
    public void setQ3FWlhiv(String q3FWlhiv) { this.q3FWlhiv = q3FWlhiv; }
    public String getQ3FSv() { return q3FSv; }
    public void setQ3FSv(String q3FSv) { this.q3FSv = q3FSv; }
    public String getQ3FAgyw() { return q3FAgyw; }
    public void setQ3FAgyw(String q3FAgyw) { this.q3FAgyw = q3FAgyw; }
    public String getQ3FHivPos() { return q3FHivPos; }
    public void setQ3FHivPos(String q3FHivPos) { this.q3FHivPos = q3FHivPos; }
    public String getQ3FSiblings() { return q3FSiblings; }
    public void setQ3FSiblings(String q3FSiblings) { this.q3FSiblings = q3FSiblings; }
    public String getQ3FCaregivers() { return q3FCaregivers; }
    public void setQ3FCaregivers(String q3FCaregivers) { this.q3FCaregivers = q3FCaregivers; }
    public String getQ3M04() { return q3M04; }
    public void setQ3M04(String q3M04) { this.q3M04 = q3M04; }
    public String getQ3M515() { return q3M515; }
    public void setQ3M515(String q3M515) { this.q3M515 = q3M515; }
    public String getQ3M1619() { return q3M1619; }
    public void setQ3M1619(String q3M1619) { this.q3M1619 = q3M1619; }
    public String getQ3M20Plus() { return q3M20Plus; }
    public void setQ3M20Plus(String q3M20Plus) { this.q3M20Plus = q3M20Plus; }
    public String getQ3MCalhiv() { return q3MCalhiv; }
    public void setQ3MCalhiv(String q3MCalhiv) { this.q3MCalhiv = q3MCalhiv; }
    public String getQ3MHei() { return q3MHei; }
    public void setQ3MHei(String q3MHei) { this.q3MHei = q3MHei; }
    public String getQ3MWlhiv() { return q3MWlhiv; }
    public void setQ3MWlhiv(String q3MWlhiv) { this.q3MWlhiv = q3MWlhiv; }
    public String getQ3MSv() { return q3MSv; }
    public void setQ3MSv(String q3MSv) { this.q3MSv = q3MSv; }
    public String getQ3MSiblings() { return q3MSiblings; }
    public void setQ3MSiblings(String q3MSiblings) { this.q3MSiblings = q3MSiblings; }
    public String getQ3MCaregivers() { return q3MCaregivers; }
    public void setQ3MCaregivers(String q3MCaregivers) { this.q3MCaregivers = q3MCaregivers; }

    public String getQ4F04() { return q4F04; }
    public void setQ4F04(String q4F04) { this.q4F04 = q4F04; }
    public String getQ4F515() { return q4F515; }
    public void setQ4F515(String q4F515) { this.q4F515 = q4F515; }
    public String getQ4F1619() { return q4F1619; }
    public void setQ4F1619(String q4F1619) { this.q4F1619 = q4F1619; }
    public String getQ4F20Plus() { return q4F20Plus; }
    public void setQ4F20Plus(String q4F20Plus) { this.q4F20Plus = q4F20Plus; }
    public String getQ4FCalhiv() { return q4FCalhiv; }
    public void setQ4FCalhiv(String q4FCalhiv) { this.q4FCalhiv = q4FCalhiv; }
    public String getQ4FHei() { return q4FHei; }
    public void setQ4FHei(String q4FHei) { this.q4FHei = q4FHei; }
    public String getQ4FWlhiv() { return q4FWlhiv; }
    public void setQ4FWlhiv(String q4FWlhiv) { this.q4FWlhiv = q4FWlhiv; }
    public String getQ4FSv() { return q4FSv; }
    public void setQ4FSv(String q4FSv) { this.q4FSv = q4FSv; }
    public String getQ4FAgyw() { return q4FAgyw; }
    public void setQ4FAgyw(String q4FAgyw) { this.q4FAgyw = q4FAgyw; }
    public String getQ4FHivPos() { return q4FHivPos; }
    public void setQ4FHivPos(String q4FHivPos) { this.q4FHivPos = q4FHivPos; }
    public String getQ4FSiblings() { return q4FSiblings; }
    public void setQ4FSiblings(String q4FSiblings) { this.q4FSiblings = q4FSiblings; }
    public String getQ4FCaregivers() { return q4FCaregivers; }
    public void setQ4FCaregivers(String q4FCaregivers) { this.q4FCaregivers = q4FCaregivers; }
    public String getQ4M04() { return q4M04; }
    public void setQ4M04(String q4M04) { this.q4M04 = q4M04; }
    public String getQ4M515() { return q4M515; }
    public void setQ4M515(String q4M515) { this.q4M515 = q4M515; }
    public String getQ4M1619() { return q4M1619; }
    public void setQ4M1619(String q4M1619) { this.q4M1619 = q4M1619; }
    public String getQ4M20Plus() { return q4M20Plus; }
    public void setQ4M20Plus(String q4M20Plus) { this.q4M20Plus = q4M20Plus; }
    public String getQ4MCalhiv() { return q4MCalhiv; }
    public void setQ4MCalhiv(String q4MCalhiv) { this.q4MCalhiv = q4MCalhiv; }
    public String getQ4MHei() { return q4MHei; }
    public void setQ4MHei(String q4MHei) { this.q4MHei = q4MHei; }
    public String getQ4MWlhiv() { return q4MWlhiv; }
    public void setQ4MWlhiv(String q4MWlhiv) { this.q4MWlhiv = q4MWlhiv; }
    public String getQ4MSv() { return q4MSv; }
    public void setQ4MSv(String q4MSv) { this.q4MSv = q4MSv; }
    public String getQ4MSiblings() { return q4MSiblings; }
    public void setQ4MSiblings(String q4MSiblings) { this.q4MSiblings = q4MSiblings; }
    public String getQ4MCaregivers() { return q4MCaregivers; }
    public void setQ4MCaregivers(String q4MCaregivers) { this.q4MCaregivers = q4MCaregivers; }

    public String getQ5F04() { return q5F04; }
    public void setQ5F04(String q5F04) { this.q5F04 = q5F04; }
    public String getQ5F515() { return q5F515; }
    public void setQ5F515(String q5F515) { this.q5F515 = q5F515; }
    public String getQ5F1619() { return q5F1619; }
    public void setQ5F1619(String q5F1619) { this.q5F1619 = q5F1619; }
    public String getQ5F20Plus() { return q5F20Plus; }
    public void setQ5F20Plus(String q5F20Plus) { this.q5F20Plus = q5F20Plus; }
    public String getQ5FCalhiv() { return q5FCalhiv; }
    public void setQ5FCalhiv(String q5FCalhiv) { this.q5FCalhiv = q5FCalhiv; }
    public String getQ5FHei() { return q5FHei; }
    public void setQ5FHei(String q5FHei) { this.q5FHei = q5FHei; }
    public String getQ5FWlhiv() { return q5FWlhiv; }
    public void setQ5FWlhiv(String q5FWlhiv) { this.q5FWlhiv = q5FWlhiv; }
    public String getQ5FSv() { return q5FSv; }
    public void setQ5FSv(String q5FSv) { this.q5FSv = q5FSv; }
    public String getQ5FAgyw() { return q5FAgyw; }
    public void setQ5FAgyw(String q5FAgyw) { this.q5FAgyw = q5FAgyw; }
    public String getQ5FHivPos() { return q5FHivPos; }
    public void setQ5FHivPos(String q5FHivPos) { this.q5FHivPos = q5FHivPos; }
    public String getQ5FSiblings() { return q5FSiblings; }
    public void setQ5FSiblings(String q5FSiblings) { this.q5FSiblings = q5FSiblings; }
    public String getQ5FCaregivers() { return q5FCaregivers; }
    public void setQ5FCaregivers(String q5FCaregivers) { this.q5FCaregivers = q5FCaregivers; }
    public String getQ5M04() { return q5M04; }
    public void setQ5M04(String q5M04) { this.q5M04 = q5M04; }
    public String getQ5M515() { return q5M515; }
    public void setQ5M515(String q5M515) { this.q5M515 = q5M515; }
    public String getQ5M1619() { return q5M1619; }
    public void setQ5M1619(String q5M1619) { this.q5M1619 = q5M1619; }
    public String getQ5M20Plus() { return q5M20Plus; }
    public void setQ5M20Plus(String q5M20Plus) { this.q5M20Plus = q5M20Plus; }
    public String getQ5MCalhiv() { return q5MCalhiv; }
    public void setQ5MCalhiv(String q5MCalhiv) { this.q5MCalhiv = q5MCalhiv; }
    public String getQ5MHei() { return q5MHei; }
    public void setQ5MHei(String q5MHei) { this.q5MHei = q5MHei; }
    public String getQ5MWlhiv() { return q5MWlhiv; }
    public void setQ5MWlhiv(String q5MWlhiv) { this.q5MWlhiv = q5MWlhiv; }
    public String getQ5MSv() { return q5MSv; }
    public void setQ5MSv(String q5MSv) { this.q5MSv = q5MSv; }
    public String getQ5MSiblings() { return q5MSiblings; }
    public void setQ5MSiblings(String q5MSiblings) { this.q5MSiblings = q5MSiblings; }
    public String getQ5MCaregivers() { return q5MCaregivers; }
    public void setQ5MCaregivers(String q5MCaregivers) { this.q5MCaregivers = q5MCaregivers; }

    public String getQ6F04() { return q6F04; }
    public void setQ6F04(String q6F04) { this.q6F04 = q6F04; }
    public String getQ6F515() { return q6F515; }
    public void setQ6F515(String q6F515) { this.q6F515 = q6F515; }
    public String getQ6F1619() { return q6F1619; }
    public void setQ6F1619(String q6F1619) { this.q6F1619 = q6F1619; }
    public String getQ6F20Plus() { return q6F20Plus; }
    public void setQ6F20Plus(String q6F20Plus) { this.q6F20Plus = q6F20Plus; }
    public String getQ6FCalhiv() { return q6FCalhiv; }
    public void setQ6FCalhiv(String q6FCalhiv) { this.q6FCalhiv = q6FCalhiv; }
    public String getQ6FHei() { return q6FHei; }
    public void setQ6FHei(String q6FHei) { this.q6FHei = q6FHei; }
    public String getQ6FWlhiv() { return q6FWlhiv; }
    public void setQ6FWlhiv(String q6FWlhiv) { this.q6FWlhiv = q6FWlhiv; }
    public String getQ6FSv() { return q6FSv; }
    public void setQ6FSv(String q6FSv) { this.q6FSv = q6FSv; }
    public String getQ6FAgyw() { return q6FAgyw; }
    public void setQ6FAgyw(String q6FAgyw) { this.q6FAgyw = q6FAgyw; }
    public String getQ6FHivPos() { return q6FHivPos; }
    public void setQ6FHivPos(String q6FHivPos) { this.q6FHivPos = q6FHivPos; }
    public String getQ6FSiblings() { return q6FSiblings; }
    public void setQ6FSiblings(String q6FSiblings) { this.q6FSiblings = q6FSiblings; }
    public String getQ6FCaregivers() { return q6FCaregivers; }
    public void setQ6FCaregivers(String q6FCaregivers) { this.q6FCaregivers = q6FCaregivers; }
    public String getQ6M04() { return q6M04; }
    public void setQ6M04(String q6M04) { this.q6M04 = q6M04; }
    public String getQ6M515() { return q6M515; }
    public void setQ6M515(String q6M515) { this.q6M515 = q6M515; }
    public String getQ6M1619() { return q6M1619; }
    public void setQ6M1619(String q6M1619) { this.q6M1619 = q6M1619; }
    public String getQ6M20Plus() { return q6M20Plus; }
    public void setQ6M20Plus(String q6M20Plus) { this.q6M20Plus = q6M20Plus; }
    public String getQ6MCalhiv() { return q6MCalhiv; }
    public void setQ6MCalhiv(String q6MCalhiv) { this.q6MCalhiv = q6MCalhiv; }
    public String getQ6MHei() { return q6MHei; }
    public void setQ6MHei(String q6MHei) { this.q6MHei = q6MHei; }
    public String getQ6MWlhiv() { return q6MWlhiv; }
    public void setQ6MWlhiv(String q6MWlhiv) { this.q6MWlhiv = q6MWlhiv; }
    public String getQ6MSv() { return q6MSv; }
    public void setQ6MSv(String q6MSv) { this.q6MSv = q6MSv; }
    public String getQ6MSiblings() { return q6MSiblings; }
    public void setQ6MSiblings(String q6MSiblings) { this.q6MSiblings = q6MSiblings; }
    public String getQ6MCaregivers() { return q6MCaregivers; }
    public void setQ6MCaregivers(String q6MCaregivers) { this.q6MCaregivers = q6MCaregivers; }

    public String getQ7F04() { return q7F04; }
    public void setQ7F04(String q7F04) { this.q7F04 = q7F04; }
    public String getQ7F515() { return q7F515; }
    public void setQ7F515(String q7F515) { this.q7F515 = q7F515; }
    public String getQ7F1619() { return q7F1619; }
    public void setQ7F1619(String q7F1619) { this.q7F1619 = q7F1619; }
    public String getQ7F20Plus() { return q7F20Plus; }
    public void setQ7F20Plus(String q7F20Plus) { this.q7F20Plus = q7F20Plus; }
    public String getQ7FCalhiv() { return q7FCalhiv; }
    public void setQ7FCalhiv(String q7FCalhiv) { this.q7FCalhiv = q7FCalhiv; }
    public String getQ7FHei() { return q7FHei; }
    public void setQ7FHei(String q7FHei) { this.q7FHei = q7FHei; }
    public String getQ7FWlhiv() { return q7FWlhiv; }
    public void setQ7FWlhiv(String q7FWlhiv) { this.q7FWlhiv = q7FWlhiv; }
    public String getQ7FSv() { return q7FSv; }
    public void setQ7FSv(String q7FSv) { this.q7FSv = q7FSv; }
    public String getQ7FAgyw() { return q7FAgyw; }
    public void setQ7FAgyw(String q7FAgyw) { this.q7FAgyw = q7FAgyw; }
    public String getQ7FHivPos() { return q7FHivPos; }
    public void setQ7FHivPos(String q7FHivPos) { this.q7FHivPos = q7FHivPos; }
    public String getQ7FSiblings() { return q7FSiblings; }
    public void setQ7FSiblings(String q7FSiblings) { this.q7FSiblings = q7FSiblings; }
    public String getQ7FCaregivers() { return q7FCaregivers; }
    public void setQ7FCaregivers(String q7FCaregivers) { this.q7FCaregivers = q7FCaregivers; }
    public String getQ7M04() { return q7M04; }
    public void setQ7M04(String q7M04) { this.q7M04 = q7M04; }
    public String getQ7M515() { return q7M515; }
    public void setQ7M515(String q7M515) { this.q7M515 = q7M515; }
    public String getQ7M1619() { return q7M1619; }
    public void setQ7M1619(String q7M1619) { this.q7M1619 = q7M1619; }
    public String getQ7M20Plus() { return q7M20Plus; }
    public void setQ7M20Plus(String q7M20Plus) { this.q7M20Plus = q7M20Plus; }
    public String getQ7MCalhiv() { return q7MCalhiv; }
    public void setQ7MCalhiv(String q7MCalhiv) { this.q7MCalhiv = q7MCalhiv; }
    public String getQ7MHei() { return q7MHei; }
    public void setQ7MHei(String q7MHei) { this.q7MHei = q7MHei; }
    public String getQ7MWlhiv() { return q7MWlhiv; }
    public void setQ7MWlhiv(String q7MWlhiv) { this.q7MWlhiv = q7MWlhiv; }
    public String getQ7MSv() { return q7MSv; }
    public void setQ7MSv(String q7MSv) { this.q7MSv = q7MSv; }
    public String getQ7MSiblings() { return q7MSiblings; }
    public void setQ7MSiblings(String q7MSiblings) { this.q7MSiblings = q7MSiblings; }
    public String getQ7MCaregivers() { return q7MCaregivers; }
    public void setQ7MCaregivers(String q7MCaregivers) { this.q7MCaregivers = q7MCaregivers; }

    public String getQ8F04() { return q8F04; }
    public void setQ8F04(String q8F04) { this.q8F04 = q8F04; }
    public String getQ8F515() { return q8F515; }
    public void setQ8F515(String q8F515) { this.q8F515 = q8F515; }
    public String getQ8F1619() { return q8F1619; }
    public void setQ8F1619(String q8F1619) { this.q8F1619 = q8F1619; }
    public String getQ8F20Plus() { return q8F20Plus; }
    public void setQ8F20Plus(String q8F20Plus) { this.q8F20Plus = q8F20Plus; }
    public String getQ8FCalhiv() { return q8FCalhiv; }
    public void setQ8FCalhiv(String q8FCalhiv) { this.q8FCalhiv = q8FCalhiv; }
    public String getQ8FHei() { return q8FHei; }
    public void setQ8FHei(String q8FHei) { this.q8FHei = q8FHei; }
    public String getQ8FWlhiv() { return q8FWlhiv; }
    public void setQ8FWlhiv(String q8FWlhiv) { this.q8FWlhiv = q8FWlhiv; }
    public String getQ8FSv() { return q8FSv; }
    public void setQ8FSv(String q8FSv) { this.q8FSv = q8FSv; }
    public String getQ8FAgyw() { return q8FAgyw; }
    public void setQ8FAgyw(String q8FAgyw) { this.q8FAgyw = q8FAgyw; }
    public String getQ8FHivPos() { return q8FHivPos; }
    public void setQ8FHivPos(String q8FHivPos) { this.q8FHivPos = q8FHivPos; }
    public String getQ8FSiblings() { return q8FSiblings; }
    public void setQ8FSiblings(String q8FSiblings) { this.q8FSiblings = q8FSiblings; }
    public String getQ8FCaregivers() { return q8FCaregivers; }
    public void setQ8FCaregivers(String q8FCaregivers) { this.q8FCaregivers = q8FCaregivers; }
    public String getQ8M04() { return q8M04; }
    public void setQ8M04(String q8M04) { this.q8M04 = q8M04; }
    public String getQ8M515() { return q8M515; }
    public void setQ8M515(String q8M515) { this.q8M515 = q8M515; }
    public String getQ8M1619() { return q8M1619; }
    public void setQ8M1619(String q8M1619) { this.q8M1619 = q8M1619; }
    public String getQ8M20Plus() { return q8M20Plus; }
    public void setQ8M20Plus(String q8M20Plus) { this.q8M20Plus = q8M20Plus; }
    public String getQ8MCalhiv() { return q8MCalhiv; }
    public void setQ8MCalhiv(String q8MCalhiv) { this.q8MCalhiv = q8MCalhiv; }
    public String getQ8MHei() { return q8MHei; }
    public void setQ8MHei(String q8MHei) { this.q8MHei = q8MHei; }
    public String getQ8MWlhiv() { return q8MWlhiv; }
    public void setQ8MWlhiv(String q8MWlhiv) { this.q8MWlhiv = q8MWlhiv; }
    public String getQ8MSv() { return q8MSv; }
    public void setQ8MSv(String q8MSv) { this.q8MSv = q8MSv; }
    public String getQ8MSiblings() { return q8MSiblings; }
    public void setQ8MSiblings(String q8MSiblings) { this.q8MSiblings = q8MSiblings; }
    public String getQ8MCaregivers() { return q8MCaregivers; }
    public void setQ8MCaregivers(String q8MCaregivers) { this.q8MCaregivers = q8MCaregivers; }

    public String getQ9F04() { return q9F04; }
    public void setQ9F04(String q9F04) { this.q9F04 = q9F04; }
    public String getQ9F515() { return q9F515; }
    public void setQ9F515(String q9F515) { this.q9F515 = q9F515; }
    public String getQ9F1619() { return q9F1619; }
    public void setQ9F1619(String q9F1619) { this.q9F1619 = q9F1619; }
    public String getQ9F20Plus() { return q9F20Plus; }
    public void setQ9F20Plus(String q9F20Plus) { this.q9F20Plus = q9F20Plus; }
    public String getQ9FCalhiv() { return q9FCalhiv; }
    public void setQ9FCalhiv(String q9FCalhiv) { this.q9FCalhiv = q9FCalhiv; }
    public String getQ9FHei() { return q9FHei; }
    public void setQ9FHei(String q9FHei) { this.q9FHei = q9FHei; }
    public String getQ9FWlhiv() { return q9FWlhiv; }
    public void setQ9FWlhiv(String q9FWlhiv) { this.q9FWlhiv = q9FWlhiv; }
    public String getQ9FSv() { return q9FSv; }
    public void setQ9FSv(String q9FSv) { this.q9FSv = q9FSv; }
    public String getQ9FAgyw() { return q9FAgyw; }
    public void setQ9FAgyw(String q9FAgyw) { this.q9FAgyw = q9FAgyw; }
    public String getQ9FHivPos() { return q9FHivPos; }
    public void setQ9FHivPos(String q9FHivPos) { this.q9FHivPos = q9FHivPos; }
    public String getQ9FSiblings() { return q9FSiblings; }
    public void setQ9FSiblings(String q9FSiblings) { this.q9FSiblings = q9FSiblings; }
    public String getQ9FCaregivers() { return q9FCaregivers; }
    public void setQ9FCaregivers(String q9FCaregivers) { this.q9FCaregivers = q9FCaregivers; }
    public String getQ9M04() { return q9M04; }
    public void setQ9M04(String q9M04) { this.q9M04 = q9M04; }
    public String getQ9M515() { return q9M515; }
    public void setQ9M515(String q9M515) { this.q9M515 = q9M515; }
    public String getQ9M1619() { return q9M1619; }
    public void setQ9M1619(String q9M1619) { this.q9M1619 = q9M1619; }
    public String getQ9M20Plus() { return q9M20Plus; }
    public void setQ9M20Plus(String q9M20Plus) { this.q9M20Plus = q9M20Plus; }
    public String getQ9MCalhiv() { return q9MCalhiv; }
    public void setQ9MCalhiv(String q9MCalhiv) { this.q9MCalhiv = q9MCalhiv; }
    public String getQ9MHei() { return q9MHei; }
    public void setQ9MHei(String q9MHei) { this.q9MHei = q9MHei; }
    public String getQ9MWlhiv() { return q9MWlhiv; }
    public void setQ9MWlhiv(String q9MWlhiv) { this.q9MWlhiv = q9MWlhiv; }
    public String getQ9MSv() { return q9MSv; }
    public void setQ9MSv(String q9MSv) { this.q9MSv = q9MSv; }
    public String getQ9MSiblings() { return q9MSiblings; }
    public void setQ9MSiblings(String q9MSiblings) { this.q9MSiblings = q9MSiblings; }
    public String getQ9MCaregivers() { return q9MCaregivers; }
    public void setQ9MCaregivers(String q9MCaregivers) { this.q9MCaregivers = q9MCaregivers; }

    public String getQ10F04() { return q10F04; }
    public void setQ10F04(String q10F04) { this.q10F04 = q10F04; }
    public String getQ10F515() { return q10F515; }
    public void setQ10F515(String q10F515) { this.q10F515 = q10F515; }
    public String getQ10F1619() { return q10F1619; }
    public void setQ10F1619(String q10F1619) { this.q10F1619 = q10F1619; }
    public String getQ10F20Plus() { return q10F20Plus; }
    public void setQ10F20Plus(String q10F20Plus) { this.q10F20Plus = q10F20Plus; }
    public String getQ10FCalhiv() { return q10FCalhiv; }
    public void setQ10FCalhiv(String q10FCalhiv) { this.q10FCalhiv = q10FCalhiv; }
    public String getQ10FHei() { return q10FHei; }
    public void setQ10FHei(String q10FHei) { this.q10FHei = q10FHei; }
    public String getQ10FWlhiv() { return q10FWlhiv; }
    public void setQ10FWlhiv(String q10FWlhiv) { this.q10FWlhiv = q10FWlhiv; }
    public String getQ10FSv() { return q10FSv; }
    public void setQ10FSv(String q10FSv) { this.q10FSv = q10FSv; }
    public String getQ10FAgyw() { return q10FAgyw; }
    public void setQ10FAgyw(String q10FAgyw) { this.q10FAgyw = q10FAgyw; }
    public String getQ10FHivPos() { return q10FHivPos; }
    public void setQ10FHivPos(String q10FHivPos) { this.q10FHivPos = q10FHivPos; }
    public String getQ10FSiblings() { return q10FSiblings; }
    public void setQ10FSiblings(String q10FSiblings) { this.q10FSiblings = q10FSiblings; }
    public String getQ10FCaregivers() { return q10FCaregivers; }
    public void setQ10FCaregivers(String q10FCaregivers) { this.q10FCaregivers = q10FCaregivers; }
    public String getQ10M04() { return q10M04; }
    public void setQ10M04(String q10M04) { this.q10M04 = q10M04; }
    public String getQ10M515() { return q10M515; }
    public void setQ10M515(String q10M515) { this.q10M515 = q10M515; }
    public String getQ10M1619() { return q10M1619; }
    public void setQ10M1619(String q10M1619) { this.q10M1619 = q10M1619; }
    public String getQ10M20Plus() { return q10M20Plus; }
    public void setQ10M20Plus(String q10M20Plus) { this.q10M20Plus = q10M20Plus; }
    public String getQ10MCalhiv() { return q10MCalhiv; }
    public void setQ10MCalhiv(String q10MCalhiv) { this.q10MCalhiv = q10MCalhiv; }
    public String getQ10MHei() { return q10MHei; }
    public void setQ10MHei(String q10MHei) { this.q10MHei = q10MHei; }
    public String getQ10MWlhiv() { return q10MWlhiv; }
    public void setQ10MWlhiv(String q10MWlhiv) { this.q10MWlhiv = q10MWlhiv; }
    public String getQ10MSv() { return q10MSv; }
    public void setQ10MSv(String q10MSv) { this.q10MSv = q10MSv; }
    public String getQ10MSiblings() { return q10MSiblings; }
    public void setQ10MSiblings(String q10MSiblings) { this.q10MSiblings = q10MSiblings; }
    public String getQ10MCaregivers() { return q10MCaregivers; }
    public void setQ10MCaregivers(String q10MCaregivers) { this.q10MCaregivers = q10MCaregivers; }

    public String getSbQ1F04() { return sbQ1F04; }
    public void setSbQ1F04(String sbQ1F04) { this.sbQ1F04 = sbQ1F04; }
    public String getSbQ1F515() { return sbQ1F515; }
    public void setSbQ1F515(String sbQ1F515) { this.sbQ1F515 = sbQ1F515; }
    public String getSbQ1F1619() { return sbQ1F1619; }
    public void setSbQ1F1619(String sbQ1F1619) { this.sbQ1F1619 = sbQ1F1619; }
    public String getSbQ1F20Plus() { return sbQ1F20Plus; }
    public void setSbQ1F20Plus(String sbQ1F20Plus) { this.sbQ1F20Plus = sbQ1F20Plus; }
    public String getSbQ1M04() { return sbQ1M04; }
    public void setSbQ1M04(String sbQ1M04) { this.sbQ1M04 = sbQ1M04; }
    public String getSbQ1M515() { return sbQ1M515; }
    public void setSbQ1M515(String sbQ1M515) { this.sbQ1M515 = sbQ1M515; }
    public String getSbQ1M1619() { return sbQ1M1619; }
    public void setSbQ1M1619(String sbQ1M1619) { this.sbQ1M1619 = sbQ1M1619; }
    public String getSbQ1M20Plus() { return sbQ1M20Plus; }
    public void setSbQ1M20Plus(String sbQ1M20Plus) { this.sbQ1M20Plus = sbQ1M20Plus; }

    public String getSbQ2F04() { return sbQ2F04; }
    public void setSbQ2F04(String sbQ2F04) { this.sbQ2F04 = sbQ2F04; }
    public String getSbQ2F515() { return sbQ2F515; }
    public void setSbQ2F515(String sbQ2F515) { this.sbQ2F515 = sbQ2F515; }
    public String getSbQ2F1619() { return sbQ2F1619; }
    public void setSbQ2F1619(String sbQ2F1619) { this.sbQ2F1619 = sbQ2F1619; }
    public String getSbQ2F20Plus() { return sbQ2F20Plus; }
    public void setSbQ2F20Plus(String sbQ2F20Plus) { this.sbQ2F20Plus = sbQ2F20Plus; }
    public String getSbQ2M04() { return sbQ2M04; }
    public void setSbQ2M04(String sbQ2M04) { this.sbQ2M04 = sbQ2M04; }
    public String getSbQ2M515() { return sbQ2M515; }
    public void setSbQ2M515(String sbQ2M515) { this.sbQ2M515 = sbQ2M515; }
    public String getSbQ2M1619() { return sbQ2M1619; }
    public void setSbQ2M1619(String sbQ2M1619) { this.sbQ2M1619 = sbQ2M1619; }
    public String getSbQ2M20Plus() { return sbQ2M20Plus; }
    public void setSbQ2M20Plus(String sbQ2M20Plus) { this.sbQ2M20Plus = sbQ2M20Plus; }

    public String getSbQ3F04() { return sbQ3F04; }
    public void setSbQ3F04(String sbQ3F04) { this.sbQ3F04 = sbQ3F04; }
    public String getSbQ3F515() { return sbQ3F515; }
    public void setSbQ3F515(String sbQ3F515) { this.sbQ3F515 = sbQ3F515; }
    public String getSbQ3F1619() { return sbQ3F1619; }
    public void setSbQ3F1619(String sbQ3F1619) { this.sbQ3F1619 = sbQ3F1619; }
    public String getSbQ3F20Plus() { return sbQ3F20Plus; }
    public void setSbQ3F20Plus(String sbQ3F20Plus) { this.sbQ3F20Plus = sbQ3F20Plus; }
    public String getSbQ3M04() { return sbQ3M04; }
    public void setSbQ3M04(String sbQ3M04) { this.sbQ3M04 = sbQ3M04; }
    public String getSbQ3M515() { return sbQ3M515; }
    public void setSbQ3M515(String sbQ3M515) { this.sbQ3M515 = sbQ3M515; }
    public String getSbQ3M1619() { return sbQ3M1619; }
    public void setSbQ3M1619(String sbQ3M1619) { this.sbQ3M1619 = sbQ3M1619; }
    public String getSbQ3M20Plus() { return sbQ3M20Plus; }
    public void setSbQ3M20Plus(String sbQ3M20Plus) { this.sbQ3M20Plus = sbQ3M20Plus; }

    public String getSbQ4F04() { return sbQ4F04; }
    public void setSbQ4F04(String sbQ4F04) { this.sbQ4F04 = sbQ4F04; }
    public String getSbQ4F515() { return sbQ4F515; }
    public void setSbQ4F515(String sbQ4F515) { this.sbQ4F515 = sbQ4F515; }
    public String getSbQ4F1619() { return sbQ4F1619; }
    public void setSbQ4F1619(String sbQ4F1619) { this.sbQ4F1619 = sbQ4F1619; }
    public String getSbQ4F20Plus() { return sbQ4F20Plus; }
    public void setSbQ4F20Plus(String sbQ4F20Plus) { this.sbQ4F20Plus = sbQ4F20Plus; }
    public String getSbQ4M04() { return sbQ4M04; }
    public void setSbQ4M04(String sbQ4M04) { this.sbQ4M04 = sbQ4M04; }
    public String getSbQ4M515() { return sbQ4M515; }
    public void setSbQ4M515(String sbQ4M515) { this.sbQ4M515 = sbQ4M515; }
    public String getSbQ4M1619() { return sbQ4M1619; }
    public void setSbQ4M1619(String sbQ4M1619) { this.sbQ4M1619 = sbQ4M1619; }
    public String getSbQ4M20Plus() { return sbQ4M20Plus; }
    public void setSbQ4M20Plus(String sbQ4M20Plus) { this.sbQ4M20Plus = sbQ4M20Plus; }

    public String getSbQ5F04() { return sbQ5F04; }
    public void setSbQ5F04(String sbQ5F04) { this.sbQ5F04 = sbQ5F04; }
    public String getSbQ5F515() { return sbQ5F515; }
    public void setSbQ5F515(String sbQ5F515) { this.sbQ5F515 = sbQ5F515; }
    public String getSbQ5F1619() { return sbQ5F1619; }
    public void setSbQ5F1619(String sbQ5F1619) { this.sbQ5F1619 = sbQ5F1619; }
    public String getSbQ5F20Plus() { return sbQ5F20Plus; }
    public void setSbQ5F20Plus(String sbQ5F20Plus) { this.sbQ5F20Plus = sbQ5F20Plus; }
    public String getSbQ5M04() { return sbQ5M04; }
    public void setSbQ5M04(String sbQ5M04) { this.sbQ5M04 = sbQ5M04; }
    public String getSbQ5M515() { return sbQ5M515; }
    public void setSbQ5M515(String sbQ5M515) { this.sbQ5M515 = sbQ5M515; }
    public String getSbQ5M1619() { return sbQ5M1619; }
    public void setSbQ5M1619(String sbQ5M1619) { this.sbQ5M1619 = sbQ5M1619; }
    public String getSbQ5M20Plus() { return sbQ5M20Plus; }
    public void setSbQ5M20Plus(String sbQ5M20Plus) { this.sbQ5M20Plus = sbQ5M20Plus; }

    public String getSbQ6F04() { return sbQ6F04; }
    public void setSbQ6F04(String sbQ6F04) { this.sbQ6F04 = sbQ6F04; }
    public String getSbQ6F515() { return sbQ6F515; }
    public void setSbQ6F515(String sbQ6F515) { this.sbQ6F515 = sbQ6F515; }
    public String getSbQ6F1619() { return sbQ6F1619; }
    public void setSbQ6F1619(String sbQ6F1619) { this.sbQ6F1619 = sbQ6F1619; }
    public String getSbQ6F20Plus() { return sbQ6F20Plus; }
    public void setSbQ6F20Plus(String sbQ6F20Plus) { this.sbQ6F20Plus = sbQ6F20Plus; }
    public String getSbQ6M04() { return sbQ6M04; }
    public void setSbQ6M04(String sbQ6M04) { this.sbQ6M04 = sbQ6M04; }
    public String getSbQ6M515() { return sbQ6M515; }
    public void setSbQ6M515(String sbQ6M515) { this.sbQ6M515 = sbQ6M515; }
    public String getSbQ6M1619() { return sbQ6M1619; }
    public void setSbQ6M1619(String sbQ6M1619) { this.sbQ6M1619 = sbQ6M1619; }
    public String getSbQ6M20Plus() { return sbQ6M20Plus; }
    public void setSbQ6M20Plus(String sbQ6M20Plus) { this.sbQ6M20Plus = sbQ6M20Plus; }

    public String getSbQ7F04() { return sbQ7F04; }
    public void setSbQ7F04(String sbQ7F04) { this.sbQ7F04 = sbQ7F04; }
    public String getSbQ7F515() { return sbQ7F515; }
    public void setSbQ7F515(String sbQ7F515) { this.sbQ7F515 = sbQ7F515; }
    public String getSbQ7F1619() { return sbQ7F1619; }
    public void setSbQ7F1619(String sbQ7F1619) { this.sbQ7F1619 = sbQ7F1619; }
    public String getSbQ7F20Plus() { return sbQ7F20Plus; }
    public void setSbQ7F20Plus(String sbQ7F20Plus) { this.sbQ7F20Plus = sbQ7F20Plus; }
    public String getSbQ7M04() { return sbQ7M04; }
    public void setSbQ7M04(String sbQ7M04) { this.sbQ7M04 = sbQ7M04; }
    public String getSbQ7M515() { return sbQ7M515; }
    public void setSbQ7M515(String sbQ7M515) { this.sbQ7M515 = sbQ7M515; }
    public String getSbQ7M1619() { return sbQ7M1619; }
    public void setSbQ7M1619(String sbQ7M1619) { this.sbQ7M1619 = sbQ7M1619; }
    public String getSbQ7M20Plus() { return sbQ7M20Plus; }
    public void setSbQ7M20Plus(String sbQ7M20Plus) { this.sbQ7M20Plus = sbQ7M20Plus; }

    public String getSbQ8F04() { return sbQ8F04; }
    public void setSbQ8F04(String sbQ8F04) { this.sbQ8F04 = sbQ8F04; }
    public String getSbQ8F515() { return sbQ8F515; }
    public void setSbQ8F515(String sbQ8F515) { this.sbQ8F515 = sbQ8F515; }
    public String getSbQ8F1619() { return sbQ8F1619; }
    public void setSbQ8F1619(String sbQ8F1619) { this.sbQ8F1619 = sbQ8F1619; }
    public String getSbQ8F20Plus() { return sbQ8F20Plus; }
    public void setSbQ8F20Plus(String sbQ8F20Plus) { this.sbQ8F20Plus = sbQ8F20Plus; }
    public String getSbQ8M04() { return sbQ8M04; }
    public void setSbQ8M04(String sbQ8M04) { this.sbQ8M04 = sbQ8M04; }
    public String getSbQ8M515() { return sbQ8M515; }
    public void setSbQ8M515(String sbQ8M515) { this.sbQ8M515 = sbQ8M515; }
    public String getSbQ8M1619() { return sbQ8M1619; }
    public void setSbQ8M1619(String sbQ8M1619) { this.sbQ8M1619 = sbQ8M1619; }
    public String getSbQ8M20Plus() { return sbQ8M20Plus; }
    public void setSbQ8M20Plus(String sbQ8M20Plus) { this.sbQ8M20Plus = sbQ8M20Plus; }

    public String getSbQ9F04() { return sbQ9F04; }
    public void setSbQ9F04(String sbQ9F04) { this.sbQ9F04 = sbQ9F04; }
    public String getSbQ9F515() { return sbQ9F515; }
    public void setSbQ9F515(String sbQ9F515) { this.sbQ9F515 = sbQ9F515; }
    public String getSbQ9F1619() { return sbQ9F1619; }
    public void setSbQ9F1619(String sbQ9F1619) { this.sbQ9F1619 = sbQ9F1619; }
    public String getSbQ9F20Plus() { return sbQ9F20Plus; }
    public void setSbQ9F20Plus(String sbQ9F20Plus) { this.sbQ9F20Plus = sbQ9F20Plus; }
    public String getSbQ9M04() { return sbQ9M04; }
    public void setSbQ9M04(String sbQ9M04) { this.sbQ9M04 = sbQ9M04; }
    public String getSbQ9M515() { return sbQ9M515; }
    public void setSbQ9M515(String sbQ9M515) { this.sbQ9M515 = sbQ9M515; }
    public String getSbQ9M1619() { return sbQ9M1619; }
    public void setSbQ9M1619(String sbQ9M1619) { this.sbQ9M1619 = sbQ9M1619; }
    public String getSbQ9M20Plus() { return sbQ9M20Plus; }
    public void setSbQ9M20Plus(String sbQ9M20Plus) { this.sbQ9M20Plus = sbQ9M20Plus; }

    public String getSbQ10F04() { return sbQ10F04; }
    public void setSbQ10F04(String sbQ10F04) { this.sbQ10F04 = sbQ10F04; }
    public String getSbQ10F515() { return sbQ10F515; }
    public void setSbQ10F515(String sbQ10F515) { this.sbQ10F515 = sbQ10F515; }
    public String getSbQ10F1619() { return sbQ10F1619; }
    public void setSbQ10F1619(String sbQ10F1619) { this.sbQ10F1619 = sbQ10F1619; }
    public String getSbQ10F20Plus() { return sbQ10F20Plus; }
    public void setSbQ10F20Plus(String sbQ10F20Plus) { this.sbQ10F20Plus = sbQ10F20Plus; }
    public String getSbQ10M04() { return sbQ10M04; }
    public void setSbQ10M04(String sbQ10M04) { this.sbQ10M04 = sbQ10M04; }
    public String getSbQ10M515() { return sbQ10M515; }
    public void setSbQ10M515(String sbQ10M515) { this.sbQ10M515 = sbQ10M515; }
    public String getSbQ10M1619() { return sbQ10M1619; }
    public void setSbQ10M1619(String sbQ10M1619) { this.sbQ10M1619 = sbQ10M1619; }
    public String getSbQ10M20Plus() { return sbQ10M20Plus; }
    public void setSbQ10M20Plus(String sbQ10M20Plus) { this.sbQ10M20Plus = sbQ10M20Plus; }

    public String getScQ1() { return scQ1; }
    public void setScQ1(String scQ1) { this.scQ1 = scQ1; }
    public String getScQ2() { return scQ2; }
    public void setScQ2(String scQ2) { this.scQ2 = scQ2; }
    public String getScQ3() { return scQ3; }
    public void setScQ3(String scQ3) { this.scQ3 = scQ3; }
    public String getScQ4() { return scQ4; }
    public void setScQ4(String scQ4) { this.scQ4 = scQ4; }
    public String getScQ5() { return scQ5; }
    public void setScQ5(String scQ5) { this.scQ5 = scQ5; }
    public String getScQ6() { return scQ6; }
    public void setScQ6(String scQ6) { this.scQ6 = scQ6; }
    public String getScQ7() { return scQ7; }
    public void setScQ7(String scQ7) { this.scQ7 = scQ7; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
