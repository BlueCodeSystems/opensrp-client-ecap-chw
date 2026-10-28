package com.bluecodeltd.ecap.chw.util;

/**
 * Staff training attendance: one record per training the signed-in caseworker attended. The form saves a
 * "Training Attendance" event, which syncs like any other, and the client processor writes it to TABLE
 * (see ec_client_classification.json and ec_client_fields.json; keep COLUMNS in step with the latter).
 */
public final class TrainingAttendance {

    public static final String TABLE = "ec_training_attendance";
    public static final String ENCOUNTER_TYPE = "Training Attendance";
    public static final String FORM_NAME = "training_attendance";

    /** Columns beyond the id, relationalid, details and is_closed every register table has. */
    public static final String[] COLUMNS = {
            "base_entity_id", "form_id", "training_title", "training_topic", "start_date", "end_date",
            "training_mode", "venue", "facilitator", "attended_full", "certificate_received", "comment",
            "caseworker_name", "facility", "province", "district", "date_created", "delete_status",
            "last_interacted_with"
    };

    private TrainingAttendance() {
    }
}
