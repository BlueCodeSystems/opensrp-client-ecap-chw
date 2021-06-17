package org.smartregister.chw.model;

import android.util.Pair;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public abstract class CoreConsentForm {
    public abstract Pair<Client, Event> processRegistration(String jsonString);

    public abstract JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception;
}
