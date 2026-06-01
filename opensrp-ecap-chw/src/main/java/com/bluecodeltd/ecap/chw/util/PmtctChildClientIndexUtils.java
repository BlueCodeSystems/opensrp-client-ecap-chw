package com.bluecodeltd.ecap.chw.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;

import timber.log.Timber;

public final class PmtctChildClientIndexUtils {

    private static final String EVENT_TYPE_MOTHER_PMTCT_CHILD = "Mother Pmtct Child";
    private static final String ATTRIBUTES = "attributes";
    private static final String INFANT_FIRST_NAME = "infant_first_name";
    private static final String INFANT_LAST_NAME = "infant_lastname";
    private static final String INFANT_DOB = "infants_date_of_birth";
    private static final String INFANT_SEX = "infants_sex";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ADOLESCENT_DOB = "adolescent_birthdate";
    private static final String GENDER = "gender";

    private PmtctChildClientIndexUtils() {
    }

    /**
     * Mirrors PMTCT child form attributes to top-level client fields so that
     * ec_client_index columns (first_name, last_name, gender, adolescent_birthdate)
     * are populated correctly when a "Mother Pmtct Child" event is saved.
     */
    public static void mirrorClientIndexAttributes(JSONObject clientJsonObject, Event event) {
        if (clientJsonObject == null || event == null
                || !EVENT_TYPE_MOTHER_PMTCT_CHILD.equals(event.getEventType())) {
            return;
        }

        try {
            JSONObject attributes = clientJsonObject.optJSONObject(ATTRIBUTES);
            if (attributes == null) {
                attributes = new JSONObject();
                clientJsonObject.put(ATTRIBUTES, attributes);
            }

            // Copy infant name to top-level client fields (firstName/lastName) so
            // ec_client_index first_name/last_name columns are populated.
            String infantFirstName = attributes.optString(INFANT_FIRST_NAME, "");
            if (!infantFirstName.trim().isEmpty()) {
                clientJsonObject.put(FIRST_NAME, infantFirstName);
            }
            String infantLastName = attributes.optString(INFANT_LAST_NAME, "");
            if (!infantLastName.trim().isEmpty()) {
                clientJsonObject.put(LAST_NAME, infantLastName);
            }

            copyIfPresent(attributes, INFANT_DOB, ADOLESCENT_DOB);
            copyIfPresent(attributes, INFANT_SEX, GENDER);
        } catch (JSONException e) {
            Timber.e(e, "Failed to mirror PMTCT child attributes for ec_client_index");
        }
    }

    private static void copyIfPresent(JSONObject attributes, String sourceKey, String targetKey)
            throws JSONException {
        String value = attributes.optString(sourceKey, "");
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        String existing = attributes.optString(targetKey, "");
        if (existing == null || existing.trim().isEmpty()) {
            attributes.put(targetKey, value);
        }
    }
}
