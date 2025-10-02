package org.smartregister.chw.anc.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.io.Serializable;

import timber.log.Timber;

@SuppressWarnings("serial")
public class MemberObject implements Serializable {
    protected String lastMenstrualPeriod;
    protected String address;
    protected String chwMemberId;
    protected String baseEntityId;
    protected String familyBaseEntityId;
    protected String familyHead;
    protected String familyHeadName;
    protected String familyHeadPhoneNumber;
    protected String primaryCareGiver;
    protected String familyName;
    protected String lastContactVisit;
    protected String lastInteractedWith;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String gravida;
    protected String pregnancyRiskLevel;
    protected String dob;
    protected String phoneNumber;
    protected int confirmedContacts = 0;
    protected String dateCreated;
    protected String hasAncCard;
    protected String deliveryKit;
    protected String gps;
    protected String landmark;

    public MemberObject() {
    }

    public MemberObject(CommonPersonObjectClient pc) {
        lastMenstrualPeriod = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_MENSTRUAL_PERIOD, false);
        baseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
        lastContactVisit = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_CONTACT_VISIT, false);
        firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        dob = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        dateCreated = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DATE_CREATED, false);
        address = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false);
        chwMemberId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);

        familyBaseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.RELATIONAL_ID, false);
        familyBaseEntityId = StringUtils.isNotBlank(familyBaseEntityId) ? familyBaseEntityId : Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.RELATIONALID, false);
        familyHead = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false);
        primaryCareGiver = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false);
        familyName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FAMILY_NAME, false);
        phoneNumber = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.PHONE_NUMBER, false);
        hasAncCard = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.HAS_ANC_CARD, false);
        deliveryKit = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DELIVERY_KIT, false);
        gps = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GPS, false);
        landmark = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LANDMARK, false);


        String visits = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.CONFIRMED_VISITS, false);
        if (StringUtils.isNotBlank(visits)) {
            confirmedContacts = Integer.parseInt(visits);
        }
    }

    private String getAncMemberNameAndAge(String firstName, String middleName, String surName, String age) {
        int integerAge = new Period(new DateTime(age), new DateTime()).getYears();

        String name = Utils.getName(firstName, middleName);
        name = Utils.getName(name, surName);

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(age)) {
            return name + ", " + integerAge;
        }
        return "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMemberName() {
        return getAncMemberNameAndAge(
                firstName == null ? "" : firstName,
                middleName == null ? "" : middleName,
                lastName == null ? "" : lastName,
                dob == null ? "" : dob);
    }

    public String getLastMenstrualPeriod() {
        return lastMenstrualPeriod;
    }

    public String getAddress() {
        return address;
    }

    public String getChwMemberId() {
        return chwMemberId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyHead() {
        return familyHead;
    }

    public String getPrimaryCareGiver() {
        return primaryCareGiver;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getLastContactVisit() {
        return lastContactVisit;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getDob() {
        return dob;
    }

    public String getHasAncCard() {
        return hasAncCard;
    }

    public int getConfirmedContacts() {
        return confirmedContacts;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getAge() {
        try {
            return new Period(new DateTime(getDob()), new DateTime()).getYears();
        } catch (Exception e) {
            Timber.e(e);
        }
        return 0;
    }

    public int getGestationAge() {
        try {
            return Days.daysBetween(DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(lastMenstrualPeriod), new DateTime()).getDays() / 7;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFullName() {
        if (StringUtils.isNotBlank(getMiddleName())) {
            return Utils.getName(Utils.getName(getFirstName(), getMiddleName()), getLastName());
        }
        return Utils.getName(getFirstName(), getLastName());
    }

    public String getLastInteractedWith() {
        return lastInteractedWith;
    }

    public void setLastMenstrualPeriod(String lastMenstrualPeriod) {
        this.lastMenstrualPeriod = lastMenstrualPeriod;
    }

    public String getGravida() {
        return this.gravida;
    }

    public void setGravida(String gravida) {
        this.gravida = gravida;
    }

    public String getPregnancyRiskLevel() {
        return pregnancyRiskLevel;
    }

    public void setPregnancyRiskLevel(String pregnancyRiskLevel) {
        this.pregnancyRiskLevel = pregnancyRiskLevel;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setChwMemberId(String chwMemberId) {
        this.chwMemberId = chwMemberId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public void setFamilyHead(String familyHead) {
        this.familyHead = familyHead;
    }

    public void setPrimaryCareGiver(String primaryCareGiver) {
        this.primaryCareGiver = primaryCareGiver;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setLastContactVisit(String lastContactVisit) {
        this.lastContactVisit = lastContactVisit;
    }

    public void setLastInteractedWith(String lastInteractedWith) {
        this.lastInteractedWith = lastInteractedWith;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setConfirmedContacts(int confirmedContacts) {
        this.confirmedContacts = confirmedContacts;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setHasAncCard(String hasAncCard) {
        this.hasAncCard = hasAncCard;
    }

    public String getFamilyHeadName() {
        return familyHeadName;
    }

    public void setFamilyHeadName(String familyHeadName) {
        this.familyHeadName = familyHeadName;
    }

    public String getFamilyHeadPhoneNumber() {
        return familyHeadPhoneNumber;
    }

    public void setFamilyHeadPhoneNumber(String familyHeadPhoneNumber) {
        this.familyHeadPhoneNumber = familyHeadPhoneNumber;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getDeliveryKit() {
        return deliveryKit;
    }

    public void setDeliveryKit(String deliveryKit) {
        this.deliveryKit = deliveryKit;
    }
}
