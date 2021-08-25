package com.bluecodeltd.ecap.chw.model;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class MotherIndexEventClient {

    private final Event event;

    private final Client client;

    public MotherIndexEventClient(Event event, Client client) {
        this.event = event;
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public Client getClient() {
        return client;
    }
}
