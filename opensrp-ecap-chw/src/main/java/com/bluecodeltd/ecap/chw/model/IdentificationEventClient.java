package com.bluecodeltd.ecap.chw.model;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class IdentificationEventClient {

    private final Event event;

    private final Client client;

    public IdentificationEventClient(Event event, Client client) {
        this.event = event;
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public Client getClient() {
        return client;
    }

    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

