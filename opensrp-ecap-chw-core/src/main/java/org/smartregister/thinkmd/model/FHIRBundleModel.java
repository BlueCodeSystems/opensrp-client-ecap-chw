package org.smartregister.thinkmd.model;

/**
 * Minimal POJO mirroring the ThinkMD FHIR bundle model used when submitting
 * assessment results. Only the fields referenced within the project are
 * implemented to keep the build self-contained while the upstream dependency is
 * unavailable.
 */
public class FHIRBundleModel {

    private String randomlyGeneratedId;
    private String encounterId;
    private String muacValueCode;
    private String muacValueDisplay;
    private String gender;
    private String dob;
    private String ageInDays;
    private String uniqueIdGeneratedForThinkMD;
    private String patientId;
    private String practitionerId;
    private String userName;
    private String locationId;
    private String rootPackageName;
    private String appVersion;
    private String displayLanguage;
    private String appName;
    private String appLanguage;
    private String endPointPackageName;

    public String getRandomlyGeneratedId() {
        return randomlyGeneratedId;
    }

    public void setRandomlyGeneratedId(String randomlyGeneratedId) {
        this.randomlyGeneratedId = randomlyGeneratedId;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getMUACValueCode() {
        return muacValueCode;
    }

    public void setMUACValueCode(String muacValueCode) {
        this.muacValueCode = muacValueCode;
    }

    public String getMUACValueDisplay() {
        return muacValueDisplay;
    }

    public void setMUACValueDisplay(String muacValueDisplay) {
        this.muacValueDisplay = muacValueDisplay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAgeInDays() {
        return ageInDays;
    }

    public void setAgeInDays(String ageInDays) {
        this.ageInDays = ageInDays;
    }

    public String getUniqueIdGeneratedForThinkMD() {
        return uniqueIdGeneratedForThinkMD;
    }

    public void setUniqueIdGeneratedForThinkMD(String uniqueIdGeneratedForThinkMD) {
        this.uniqueIdGeneratedForThinkMD = uniqueIdGeneratedForThinkMD;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getRootPackageName() {
        return rootPackageName;
    }

    public void setRootPackageName(String rootPackageName) {
        this.rootPackageName = rootPackageName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public void setDisplayLanguage(String displayLanguage) {
        this.displayLanguage = displayLanguage;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(String appLanguage) {
        this.appLanguage = appLanguage;
    }

    public String getEndPointPackageName() {
        return endPointPackageName;
    }

    public void setEndPointPackageName(String endPointPackageName) {
        this.endPointPackageName = endPointPackageName;
    }

}
