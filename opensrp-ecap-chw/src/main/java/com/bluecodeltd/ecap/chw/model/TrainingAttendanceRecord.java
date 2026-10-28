package com.bluecodeltd.ecap.chw.model;

/** One training a staff member attended, as stored in ec_training_attendance. Dates are dd-MM-yyyy. */
public class TrainingAttendanceRecord {

    public String baseEntityId;
    public String trainingTitle;
    public String trainingTopic;
    public String startDate;
    public String endDate;
    public String trainingMode;
    public String venue;
    public String facilitator;
    public String attendedFull;
    public String certificateReceived;
    public String comment;
    public String caseworkerName;
}
