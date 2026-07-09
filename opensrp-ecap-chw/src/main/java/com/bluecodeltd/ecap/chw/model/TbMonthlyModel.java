package com.bluecodeltd.ecap.chw.model;

public class TbMonthlyModel {

    private String baseEntityId;
    private String reportingMonth;
    private String reportingYear;
    private String facilityName;
    private String reportStatus;
    private String comment;
    private String province;
    private String district;
    private String ward;
    private String partner;

    // Fields for Q1 to Q7
    private String q1FLt1, q1F14, q1F59, q1F1014, q1F1519, q1F20Plus, q1FPc18Plus, q1FTotal;
    private String q1MLt1, q1M14, q1M59, q1M1014, q1M1519, q1M20Plus, q1MPc18Plus, q1MTotal;
    private String q1SubHei, q1SubCalhiv, q1SubCPlhiv, q1SubPcLhiv, q1SubOther, q1SubTotal;

    private String q2FLt1, q2F14, q2F59, q2F1014, q2F1519, q2F20Plus, q2FPc18Plus, q2FTotal;
    private String q2MLt1, q2M14, q2M59, q2M1014, q2M1519, q2M20Plus, q2MPc18Plus, q2MTotal;
    private String q2SubHei, q2SubCalhiv, q2SubCPlhiv, q2SubPcLhiv, q2SubOther, q2SubTotal;

    private String q3FLt1, q3F14, q3F59, q3F1014, q3F1519, q3F20Plus, q3FPc18Plus, q3FTotal;
    private String q3MLt1, q3M14, q3M59, q3M1014, q3M1519, q3M20Plus, q3MPc18Plus, q3MTotal;
    private String q3SubHei, q3SubCalhiv, q3SubCPlhiv, q3SubPcLhiv, q3SubOther, q3SubTotal;

    private String q4FLt1, q4F14, q4F59, q4F1014, q4F1519, q4F20Plus, q4FPc18Plus, q4FTotal;
    private String q4MLt1, q4M14, q4M59, q4M1014, q4M1519, q4M20Plus, q4MPc18Plus, q4MTotal;
    private String q4SubHei, q4SubCalhiv, q4SubCPlhiv, q4SubPcLhiv, q4SubOther, q4SubTotal;

    private String q5FLt1, q5F14, q5F59, q5F1014, q5F1519, q5F20Plus, q5FPc18Plus, q5FTotal;
    private String q5MLt1, q5M14, q5M59, q5M1014, q5M1519, q5M20Plus, q5MPc18Plus, q5MTotal;
    private String q5SubHei, q5SubCalhiv, q5SubCPlhiv, q5SubPcLhiv, q5SubOther, q5SubTotal;

    private String q6FLt1, q6F14, q6F59, q6F1014, q6F1519, q6F20Plus, q6FPc18Plus, q6FTotal;
    private String q6MLt1, q6M14, q6M59, q6M1014, q6M1519, q6M20Plus, q6MPc18Plus, q6MTotal;
    private String q6SubHei, q6SubCalhiv, q6SubCPlhiv, q6SubPcLhiv, q6SubOther, q6SubTotal;

    private String q7FLt1, q7F14, q7F59, q7F1014, q7F1519, q7F20Plus, q7FPc18Plus, q7FTotal;
    private String q7MLt1, q7M14, q7M59, q7M1014, q7M1519, q7M20Plus, q7MPc18Plus, q7MTotal;
    private String q7SubHei, q7SubCalhiv, q7SubCPlhiv, q7SubPcLhiv, q7SubOther, q7SubTotal;

    // Getters and Setters
    public String getBaseEntityId() { return baseEntityId; }
    public void setBaseEntityId(String baseEntityId) { this.baseEntityId = baseEntityId; }
    public String getReportingMonth() { return reportingMonth; }
    public void setReportingMonth(String reportingMonth) { this.reportingMonth = reportingMonth; }
    public String getReportingYear() { return reportingYear; }
    public void setReportingYear(String reportingYear) { this.reportingYear = reportingYear; }
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public String getQ1FLt1() { return q1FLt1; }
    public void setQ1FLt1(String q1FLt1) { this.q1FLt1 = q1FLt1; }
    public String getQ1F14() { return q1F14; }
    public void setQ1F14(String q1F14) { this.q1F14 = q1F14; }
    public String getQ1F59() { return q1F59; }
    public void setQ1F59(String q1F59) { this.q1F59 = q1F59; }
    public String getQ1F1014() { return q1F1014; }
    public void setQ1F1014(String q1F1014) { this.q1F1014 = q1F1014; }
    public String getQ1F1519() { return q1F1519; }
    public void setQ1F1519(String q1F1519) { this.q1F1519 = q1F1519; }
    public String getQ1F20Plus() { return q1F20Plus; }
    public void setQ1F20Plus(String q1F20Plus) { this.q1F20Plus = q1F20Plus; }
    public String getQ1FPc18Plus() { return q1FPc18Plus; }
    public void setQ1FPc18Plus(String q1FPc18Plus) { this.q1FPc18Plus = q1FPc18Plus; }
    public String getQ1FTotal() { return q1FTotal; }
    public void setQ1FTotal(String q1FTotal) { this.q1FTotal = q1FTotal; }
    public String getQ1MLt1() { return q1MLt1; }
    public void setQ1MLt1(String q1MLt1) { this.q1MLt1 = q1MLt1; }
    public String getQ1M14() { return q1M14; }
    public void setQ1M14(String q1M14) { this.q1M14 = q1M14; }
    public String getQ1M59() { return q1M59; }
    public void setQ1M59(String q1M59) { this.q1M59 = q1M59; }
    public String getQ1M1014() { return q1M1014; }
    public void setQ1M1014(String q1M1014) { this.q1M1014 = q1M1014; }
    public String getQ1M1519() { return q1M1519; }
    public void setQ1M1519(String q1M1519) { this.q1M1519 = q1M1519; }
    public String getQ1M20Plus() { return q1M20Plus; }
    public void setQ1M20Plus(String q1M20Plus) { this.q1M20Plus = q1M20Plus; }
    public String getQ1MPc18Plus() { return q1MPc18Plus; }
    public void setQ1MPc18Plus(String q1MPc18Plus) { this.q1MPc18Plus = q1MPc18Plus; }
    public String getQ1MTotal() { return q1MTotal; }
    public void setQ1MTotal(String q1MTotal) { this.q1MTotal = q1MTotal; }
    public String getQ1SubHei() { return q1SubHei; }
    public void setQ1SubHei(String q1SubHei) { this.q1SubHei = q1SubHei; }
    public String getQ1SubCalhiv() { return q1SubCalhiv; }
    public void setQ1SubCalhiv(String q1SubCalhiv) { this.q1SubCalhiv = q1SubCalhiv; }
    public String getQ1SubCPlhiv() { return q1SubCPlhiv; }
    public void setQ1SubCPlhiv(String q1SubCPlhiv) { this.q1SubCPlhiv = q1SubCPlhiv; }
    public String getQ1SubPcLhiv() { return q1SubPcLhiv; }
    public void setQ1SubPcLhiv(String q1SubPcLhiv) { this.q1SubPcLhiv = q1SubPcLhiv; }
    public String getQ1SubOther() { return q1SubOther; }
    public void setQ1SubOther(String q1SubOther) { this.q1SubOther = q1SubOther; }
    public String getQ1SubTotal() { return q1SubTotal; }
    public void setQ1SubTotal(String q1SubTotal) { this.q1SubTotal = q1SubTotal; }

    public String getQ2FLt1() { return q2FLt1; }
    public void setQ2FLt1(String q2FLt1) { this.q2FLt1 = q2FLt1; }
    public String getQ2F14() { return q2F14; }
    public void setQ2F14(String q2F14) { this.q2F14 = q2F14; }
    public String getQ2F59() { return q2F59; }
    public void setQ2F59(String q2F59) { this.q2F59 = q2F59; }
    public String getQ2F1014() { return q2F1014; }
    public void setQ2F1014(String q2F1014) { this.q2F1014 = q2F1014; }
    public String getQ2F1519() { return q2F1519; }
    public void setQ2F1519(String q2F1519) { this.q2F1519 = q2F1519; }
    public String getQ2F20Plus() { return q2F20Plus; }
    public void setQ2F20Plus(String q2F20Plus) { this.q2F20Plus = q2F20Plus; }
    public String getQ2FPc18Plus() { return q2FPc18Plus; }
    public void setQ2FPc18Plus(String q2FPc18Plus) { this.q2FPc18Plus = q2FPc18Plus; }
    public String getQ2FTotal() { return q2FTotal; }
    public void setQ2FTotal(String q2FTotal) { this.q2FTotal = q2FTotal; }
    public String getQ2MLt1() { return q2MLt1; }
    public void setQ2MLt1(String q2MLt1) { this.q2MLt1 = q2MLt1; }
    public String getQ2M14() { return q2M14; }
    public void setQ2M14(String q2M14) { this.q2M14 = q2M14; }
    public String getQ2M59() { return q2M59; }
    public void setQ2M59(String q2M59) { this.q2M59 = q2M59; }
    public String getQ2M1014() { return q2M1014; }
    public void setQ2M1014(String q2M1014) { this.q2M1014 = q2M1014; }
    public String getQ2M1519() { return q2M1519; }
    public void setQ2M1519(String q2M1519) { this.q2M1519 = q2M1519; }
    public String getQ2M20Plus() { return q2M20Plus; }
    public void setQ2M20Plus(String q2M20Plus) { this.q2M20Plus = q2M20Plus; }
    public String getQ2MPc18Plus() { return q2MPc18Plus; }
    public void setQ2MPc18Plus(String q2MPc18Plus) { this.q2MPc18Plus = q2MPc18Plus; }
    public String getQ2MTotal() { return q2MTotal; }
    public void setQ2MTotal(String q2MTotal) { this.q2MTotal = q2MTotal; }
    public String getQ2SubHei() { return q2SubHei; }
    public void setQ2SubHei(String q2SubHei) { this.q2SubHei = q2SubHei; }
    public String getQ2SubCalhiv() { return q2SubCalhiv; }
    public void setQ2SubCalhiv(String q2SubCalhiv) { this.q2SubCalhiv = q2SubCalhiv; }
    public String getQ2SubCPlhiv() { return q2SubCPlhiv; }
    public void setQ2SubCPlhiv(String q2SubCPlhiv) { this.q2SubCPlhiv = q2SubCPlhiv; }
    public String getQ2SubPcLhiv() { return q2SubPcLhiv; }
    public void setQ2SubPcLhiv(String q2SubPcLhiv) { this.q2SubPcLhiv = q2SubPcLhiv; }
    public String getQ2SubOther() { return q2SubOther; }
    public void setQ2SubOther(String q2SubOther) { this.q2SubOther = q2SubOther; }
    public String getQ2SubTotal() { return q2SubTotal; }
    public void setQ2SubTotal(String q2SubTotal) { this.q2SubTotal = q2SubTotal; }

    public String getQ3FLt1() { return q3FLt1; }
    public void setQ3FLt1(String q3FLt1) { this.q3FLt1 = q3FLt1; }
    public String getQ3F14() { return q3F14; }
    public void setQ3F14(String q3F14) { this.q3F14 = q3F14; }
    public String getQ3F59() { return q3F59; }
    public void setQ3F59(String q3F59) { this.q3F59 = q3F59; }
    public String getQ3F1014() { return q3F1014; }
    public void setQ3F1014(String q3F1014) { this.q3F1014 = q3F1014; }
    public String getQ3F1519() { return q3F1519; }
    public void setQ3F1519(String q3F1519) { this.q3F1519 = q3F1519; }
    public String getQ3F20Plus() { return q3F20Plus; }
    public void setQ3F20Plus(String q3F20Plus) { this.q3F20Plus = q3F20Plus; }
    public String getQ3FPc18Plus() { return q3FPc18Plus; }
    public void setQ3FPc18Plus(String q3FPc18Plus) { this.q3FPc18Plus = q3FPc18Plus; }
    public String getQ3FTotal() { return q3FTotal; }
    public void setQ3FTotal(String q3FTotal) { this.q3FTotal = q3FTotal; }
    public String getQ3MLt1() { return q3MLt1; }
    public void setQ3MLt1(String q3MLt1) { this.q3MLt1 = q3MLt1; }
    public String getQ3M14() { return q3M14; }
    public void setQ3M14(String q3M14) { this.q3M14 = q3M14; }
    public String getQ3M59() { return q3M59; }
    public void setQ3M59(String q3M59) { this.q3M59 = q3M59; }
    public String getQ3M1014() { return q3M1014; }
    public void setQ3M1014(String q3M1014) { this.q3M1014 = q3M1014; }
    public String getQ3M1519() { return q3M1519; }
    public void setQ3M1519(String q3M1519) { this.q3M1519 = q3M1519; }
    public String getQ3M20Plus() { return q3M20Plus; }
    public void setQ3M20Plus(String q3M20Plus) { this.q3M20Plus = q3M20Plus; }
    public String getQ3MPc18Plus() { return q3MPc18Plus; }
    public void setQ3MPc18Plus(String q3MPc18Plus) { this.q3MPc18Plus = q3MPc18Plus; }
    public String getQ3MTotal() { return q3MTotal; }
    public void setQ3MTotal(String q3MTotal) { this.q3MTotal = q3MTotal; }
    public String getQ3SubHei() { return q3SubHei; }
    public void setQ3SubHei(String q3SubHei) { this.q3SubHei = q3SubHei; }
    public String getQ3SubCalhiv() { return q3SubCalhiv; }
    public void setQ3SubCalhiv(String q3SubCalhiv) { this.q3SubCalhiv = q3SubCalhiv; }
    public String getQ3SubCPlhiv() { return q3SubCPlhiv; }
    public void setQ3SubCPlhiv(String q3SubCPlhiv) { this.q3SubCPlhiv = q3SubCPlhiv; }
    public String getQ3SubPcLhiv() { return q3SubPcLhiv; }
    public void setQ3SubPcLhiv(String q3SubPcLhiv) { this.q3SubPcLhiv = q3SubPcLhiv; }
    public String getQ3SubOther() { return q3SubOther; }
    public void setQ3SubOther(String q3SubOther) { this.q3SubOther = q3SubOther; }
    public String getQ3SubTotal() { return q3SubTotal; }
    public void setQ3SubTotal(String q3SubTotal) { this.q3SubTotal = q3SubTotal; }

    public String getQ4FLt1() { return q4FLt1; }
    public void setQ4FLt1(String q4FLt1) { this.q4FLt1 = q4FLt1; }
    public String getQ4F14() { return q4F14; }
    public void setQ4F14(String q4F14) { this.q4F14 = q4F14; }
    public String getQ4F59() { return q4F59; }
    public void setQ4F59(String q4F59) { this.q4F59 = q4F59; }
    public String getQ4F1014() { return q4F1014; }
    public void setQ4F1014(String q4F1014) { this.q4F1014 = q4F1014; }
    public String getQ4F1519() { return q4F1519; }
    public void setQ4F1519(String q4F1519) { this.q4F1519 = q4F1519; }
    public String getQ4F20Plus() { return q4F20Plus; }
    public void setQ4F20Plus(String q4F20Plus) { this.q4F20Plus = q4F20Plus; }
    public String getQ4FPc18Plus() { return q4FPc18Plus; }
    public void setQ4FPc18Plus(String q4FPc18Plus) { this.q4FPc18Plus = q4FPc18Plus; }
    public String getQ4FTotal() { return q4FTotal; }
    public void setQ4FTotal(String q4FTotal) { this.q4FTotal = q4FTotal; }
    public String getQ4MLt1() { return q4MLt1; }
    public void setQ4MLt1(String q4MLt1) { this.q4MLt1 = q4MLt1; }
    public String getQ4M14() { return q4M14; }
    public void setQ4M14(String q4M14) { this.q4M14 = q4M14; }
    public String getQ4M59() { return q4M59; }
    public void setQ4M59(String q4M59) { this.q4M59 = q4M59; }
    public String getQ4M1014() { return q4M1014; }
    public void setQ4M1014(String q4M1014) { this.q4M1014 = q4M1014; }
    public String getQ4M1519() { return q4M1519; }
    public void setQ4M1519(String q4M1519) { this.q4M1519 = q4M1519; }
    public String getQ4M20Plus() { return q4M20Plus; }
    public void setQ4M20Plus(String q4M20Plus) { this.q4M20Plus = q4M20Plus; }
    public String getQ4MPc18Plus() { return q4MPc18Plus; }
    public void setQ4MPc18Plus(String q4MPc18Plus) { this.q4MPc18Plus = q4MPc18Plus; }
    public String getQ4MTotal() { return q4MTotal; }
    public void setQ4MTotal(String q4MTotal) { this.q4MTotal = q4MTotal; }
    public String getQ4SubHei() { return q4SubHei; }
    public void setQ4SubHei(String q4SubHei) { this.q4SubHei = q4SubHei; }
    public String getQ4SubCalhiv() { return q4SubCalhiv; }
    public void setQ4SubCalhiv(String q4SubCalhiv) { this.q4SubCalhiv = q4SubCalhiv; }
    public String getQ4SubCPlhiv() { return q4SubCPlhiv; }
    public void setQ4SubCPlhiv(String q4SubCPlhiv) { this.q4SubCPlhiv = q4SubCPlhiv; }
    public String getQ4SubPcLhiv() { return q4SubPcLhiv; }
    public void setQ4SubPcLhiv(String q4SubPcLhiv) { this.q4SubPcLhiv = q4SubPcLhiv; }
    public String getQ4SubOther() { return q4SubOther; }
    public void setQ4SubOther(String q4SubOther) { this.q4SubOther = q4SubOther; }
    public String getQ4SubTotal() { return q4SubTotal; }
    public void setQ4SubTotal(String q4SubTotal) { this.q4SubTotal = q4SubTotal; }

    public String getQ5FLt1() { return q5FLt1; }
    public void setQ5FLt1(String q5FLt1) { this.q5FLt1 = q5FLt1; }
    public String getQ5F14() { return q5F14; }
    public void setQ5F14(String q5F14) { this.q5F14 = q5F14; }
    public String getQ5F59() { return q5F59; }
    public void setQ5F59(String q5F59) { this.q5F59 = q5F59; }
    public String getQ5F1014() { return q5F1014; }
    public void setQ5F1014(String q5F1014) { this.q5F1014 = q5F1014; }
    public String getQ5F1519() { return q5F1519; }
    public void setQ5F1519(String q5F1519) { this.q5F1519 = q5F1519; }
    public String getQ5F20Plus() { return q5F20Plus; }
    public void setQ5F20Plus(String q5F20Plus) { this.q5F20Plus = q5F20Plus; }
    public String getQ5FPc18Plus() { return q5FPc18Plus; }
    public void setQ5FPc18Plus(String q5FPc18Plus) { this.q5FPc18Plus = q5FPc18Plus; }
    public String getQ5FTotal() { return q5FTotal; }
    public void setQ5FTotal(String q5FTotal) { this.q5FTotal = q5FTotal; }
    public String getQ5MLt1() { return q5MLt1; }
    public void setQ5MLt1(String q5MLt1) { this.q5MLt1 = q5MLt1; }
    public String getQ5M14() { return q5M14; }
    public void setQ5M14(String q5M14) { this.q5M14 = q5M14; }
    public String getQ5M59() { return q5M59; }
    public void setQ5M59(String q5M59) { this.q5M59 = q5M59; }
    public String getQ5M1014() { return q5M1014; }
    public void setQ5M1014(String q5M1014) { this.q5M1014 = q5M1014; }
    public String getQ5M1519() { return q5M1519; }
    public void setQ5M1519(String q5M1519) { this.q5M1519 = q5M1519; }
    public String getQ5M20Plus() { return q5M20Plus; }
    public void setQ5M20Plus(String q5M20Plus) { this.q5M20Plus = q5M20Plus; }
    public String getQ5MPc18Plus() { return q5MPc18Plus; }
    public void setQ5MPc18Plus(String q5MPc18Plus) { this.q5MPc18Plus = q5MPc18Plus; }
    public String getQ5MTotal() { return q5MTotal; }
    public void setQ5MTotal(String q5MTotal) { this.q5MTotal = q5MTotal; }
    public String getQ5SubHei() { return q5SubHei; }
    public void setQ5SubHei(String q5SubHei) { this.q5SubHei = q5SubHei; }
    public String getQ5SubCalhiv() { return q5SubCalhiv; }
    public void setQ5SubCalhiv(String q5SubCalhiv) { this.q5SubCalhiv = q5SubCalhiv; }
    public String getQ5SubCPlhiv() { return q5SubCPlhiv; }
    public void setQ5SubCPlhiv(String q5SubCPlhiv) { this.q5SubCPlhiv = q5SubCPlhiv; }
    public String getQ5SubPcLhiv() { return q5SubPcLhiv; }
    public void setQ5SubPcLhiv(String q5SubPcLhiv) { this.q5SubPcLhiv = q5SubPcLhiv; }
    public String getQ5SubOther() { return q5SubOther; }
    public void setQ5SubOther(String q5SubOther) { this.q5SubOther = q5SubOther; }
    public String getQ5SubTotal() { return q5SubTotal; }
    public void setQ5SubTotal(String q5SubTotal) { this.q5SubTotal = q5SubTotal; }

    public String getQ6FLt1() { return q6FLt1; }
    public void setQ6FLt1(String q6FLt1) { this.q6FLt1 = q6FLt1; }
    public String getQ6F14() { return q6F14; }
    public void setQ6F14(String q6F14) { this.q6F14 = q6F14; }
    public String getQ6F59() { return q6F59; }
    public void setQ6F59(String q6F59) { this.q6F59 = q6F59; }
    public String getQ6F1014() { return q6F1014; }
    public void setQ6F1014(String q6F1014) { this.q6F1014 = q6F1014; }
    public String getQ6F1519() { return q6F1519; }
    public void setQ6F1519(String q6F1519) { this.q6F1519 = q6F1519; }
    public String getQ6F20Plus() { return q6F20Plus; }
    public void setQ6F20Plus(String q6F20Plus) { this.q6F20Plus = q6F20Plus; }
    public String getQ6FPc18Plus() { return q6FPc18Plus; }
    public void setQ6FPc18Plus(String q6FPc18Plus) { this.q6FPc18Plus = q6FPc18Plus; }
    public String getQ6FTotal() { return q6FTotal; }
    public void setQ6FTotal(String q6FTotal) { this.q6FTotal = q6FTotal; }
    public String getQ6MLt1() { return q6MLt1; }
    public void setQ6MLt1(String q6MLt1) { this.q6MLt1 = q6MLt1; }
    public String getQ6M14() { return q6M14; }
    public void setQ6M14(String q6M14) { this.q6M14 = q6M14; }
    public String getQ6M59() { return q6M59; }
    public void setQ6M59(String q6M59) { this.q6M59 = q6M59; }
    public String getQ6M1014() { return q6M1014; }
    public void setQ6M1014(String q6M1014) { this.q6M1014 = q6M1014; }
    public String getQ6M1519() { return q6M1519; }
    public void setQ6M1519(String q6M1519) { this.q6M1519 = q6M1519; }
    public String getQ6M20Plus() { return q6M20Plus; }
    public void setQ6M20Plus(String q6M20Plus) { this.q6M20Plus = q6M20Plus; }
    public String getQ6MPc18Plus() { return q6MPc18Plus; }
    public void setQ6MPc18Plus(String q6MPc18Plus) { this.q6MPc18Plus = q6MPc18Plus; }
    public String getQ6MTotal() { return q6MTotal; }
    public void setQ6MTotal(String q6MTotal) { this.q6MTotal = q6MTotal; }
    public String getQ6SubHei() { return q6SubHei; }
    public void setQ6SubHei(String q6SubHei) { this.q6SubHei = q6SubHei; }
    public String getQ6SubCalhiv() { return q6SubCalhiv; }
    public void setQ6SubCalhiv(String q6SubCalhiv) { this.q6SubCalhiv = q6SubCalhiv; }
    public String getQ6SubCPlhiv() { return q6SubCPlhiv; }
    public void setQ6SubCPlhiv(String q6SubCPlhiv) { this.q6SubCPlhiv = q6SubCPlhiv; }
    public String getQ6SubPcLhiv() { return q6SubPcLhiv; }
    public void setQ6SubPcLhiv(String q6SubPcLhiv) { this.q6SubPcLhiv = q6SubPcLhiv; }
    public String getQ6SubOther() { return q6SubOther; }
    public void setQ6SubOther(String q6SubOther) { this.q6SubOther = q6SubOther; }
    public String getQ6SubTotal() { return q6SubTotal; }
    public void setQ6SubTotal(String q6SubTotal) { this.q6SubTotal = q6SubTotal; }

    public String getQ7FLt1() { return q7FLt1; }
    public void setQ7FLt1(String q7FLt1) { this.q7FLt1 = q7FLt1; }
    public String getQ7F14() { return q7F14; }
    public void setQ7F14(String q7F14) { this.q7F14 = q7F14; }
    public String getQ7F59() { return q7F59; }
    public void setQ7F59(String q7F59) { this.q7F59 = q7F59; }
    public String getQ7F1014() { return q7F1014; }
    public void setQ7F1014(String q7F1014) { this.q7F1014 = q7F1014; }
    public String getQ7F1519() { return q7F1519; }
    public void setQ7F1519(String q7F1519) { this.q7F1519 = q7F1519; }
    public String getQ7F20Plus() { return q7F20Plus; }
    public void setQ7F20Plus(String q7F20Plus) { this.q7F20Plus = q7F20Plus; }
    public String getQ7FPc18Plus() { return q7FPc18Plus; }
    public void setQ7FPc18Plus(String q7FPc18Plus) { this.q7FPc18Plus = q7FPc18Plus; }
    public String getQ7FTotal() { return q7FTotal; }
    public void setQ7FTotal(String q7FTotal) { this.q7FTotal = q7FTotal; }
    public String getQ7MLt1() { return q7MLt1; }
    public void setQ7MLt1(String q7MLt1) { this.q7MLt1 = q7MLt1; }
    public String getQ7M14() { return q7M14; }
    public void setQ7M14(String q7M14) { this.q7M14 = q7M14; }
    public String getQ7M59() { return q7M59; }
    public void setQ7M59(String q7M59) { this.q7M59 = q7M59; }
    public String getQ7M1014() { return q7M1014; }
    public void setQ7M1014(String q7M1014) { this.q7M1014 = q7M1014; }
    public String getQ7M1519() { return q7M1519; }
    public void setQ7M1519(String q7M1519) { this.q7M1519 = q7M1519; }
    public String getQ7M20Plus() { return q7M20Plus; }
    public void setQ7M20Plus(String q7M20Plus) { this.q7M20Plus = q7M20Plus; }
    public String getQ7MPc18Plus() { return q7MPc18Plus; }
    public void setQ7MPc18Plus(String q7MPc18Plus) { this.q7MPc18Plus = q7MPc18Plus; }
    public String getQ7MTotal() { return q7MTotal; }
    public void setQ7MTotal(String q7MTotal) { this.q7MTotal = q7MTotal; }
    public String getQ7SubHei() { return q7SubHei; }
    public void setQ7SubHei(String q7SubHei) { this.q7SubHei = q7SubHei; }
    public String getQ7SubCalhiv() { return q7SubCalhiv; }
    public void setQ7SubCalhiv(String q7SubCalhiv) { this.q7SubCalhiv = q7SubCalhiv; }
    public String getQ7SubCPlhiv() { return q7SubCPlhiv; }
    public void setQ7SubCPlhiv(String q7SubCPlhiv) { this.q7SubCPlhiv = q7SubCPlhiv; }
    public String getQ7SubPcLhiv() { return q7SubPcLhiv; }
    public void setQ7SubPcLhiv(String q7SubPcLhiv) { this.q7SubPcLhiv = q7SubPcLhiv; }
    public String getQ7SubOther() { return q7SubOther; }
    public void setQ7SubOther(String q7SubOther) { this.q7SubOther = q7SubOther; }
    public String getQ7SubTotal() { return q7SubTotal; }
    public void setQ7SubTotal(String q7SubTotal) { this.q7SubTotal = q7SubTotal; }
}
